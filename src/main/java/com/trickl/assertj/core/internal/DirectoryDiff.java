package com.trickl.assertj.core.internal;

import static org.assertj.core.util.Strings.quote;

import com.trickl.assertj.core.presentation.FileChangeSection;
import com.trickl.assertj.util.diff.DirectoryChangeDelta;
import com.trickl.assertj.util.diff.FileChangeDelta;
import com.trickl.assertj.util.diff.FileMissingDelta;
import com.trickl.assertj.util.diff.FileUnexpectedDelta;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.internal.Diff;
import org.assertj.core.util.Lists;
import org.assertj.core.util.VisibleForTesting;
import org.assertj.core.util.diff.Delta;

/** Compares the contents of two directories. */
@VisibleForTesting
public class DirectoryDiff {

  @VisibleForTesting Diff diff = new Diff();

  @VisibleForTesting
  public List<Delta<String>> diff(File actual, File expected) throws IOException {
    return diff(actual, expected, include -> true);
  }

  /**
   * Find the differences between two directories.
   *
   * @param actual The directory under comparison
   * @param expected The expected directory structure and content
   * @param filter The files to consider
   * @return A list of differences
   */
  @VisibleForTesting
  public List<Delta<String>> diff(File actual, File expected, FileFilter filter) {
    List<Delta<String>> deltas = new LinkedList<>();
    if (actual.isFile() && filter.accept(actual)) {
      diffFile(actual, expected).ifPresent(delta -> deltas.add(delta));
    } else if (!actual.isFile()) {
      diffDirectory(actual, expected, filter).ifPresent(delta -> deltas.add(delta));
    }
    return deltas;
  }

  /**
   * Find the differences between two directories.
   *
   * @param actual The directory under comparison
   * @param expected The expected directory structure and content
   * @param filter The files to consider
   * @return A list of differences
   * @throws IOException If a file or directory cannot be read
   */
  @VisibleForTesting
  public Optional<DirectoryChangeDelta> diffDirectory(
      File actual, File expected, FileFilter filter) {
    Map<Path, File> actualFileMap;
    try {
      throwIfDifferentFileTypes(actual, expected);
      actualFileMap = getRelativePathMap(actual, filter);
    } catch (IOException ex) {
      return Optional.of(new DirectoryChangeDelta(actual.toPath(), ex.getMessage()));
    }

    Map<Path, File> expectedFileMap;
    try {
      expectedFileMap = getRelativePathMap(expected, filter);
    } catch (IOException ex) {
      return Optional.of(new DirectoryChangeDelta(expected.toPath(), ex.getMessage()));
    }

    Set<Path> missingFiles = subtractPaths(expectedFileMap.keySet(), actualFileMap.keySet());
    Set<Path> unexpectedFiles = subtractPaths(actualFileMap.keySet(), expectedFileMap.keySet());
    Set<Path> commonFiles = commonPaths(actualFileMap.keySet(), expectedFileMap.keySet());
    
    List<FileMissingDelta> missingFileDeltas = toFileMissingDeltas(missingFiles);
    List<FileUnexpectedDelta> unexpectedFileDeltas = toFileUnexpectedDeltas(unexpectedFiles);
    List<FileChangeDelta> changedFileDeltas =
        getFileChangedDeltas(commonFiles, actualFileMap, expectedFileMap);
    List<DirectoryChangeDelta> dirChangedDeltas =
        getDirectoryChangedDeltas(commonFiles, filter, actualFileMap, expectedFileMap);

    DirectoryChangeDelta delta =
        new DirectoryChangeDelta(
            actual.toPath(),
            missingFileDeltas,
            unexpectedFileDeltas,
            dirChangedDeltas,
            changedFileDeltas);

    return delta.hasChanges() ? Optional.of(delta) : Optional.empty();
  }

  private Optional<FileChangeDelta> diffFile(File actual, File expected) {
    List<FileChangeSection> diffs;
    try {
      throwIfDifferentFileTypes(actual, expected);

      Charset actualCharSet = Charset.defaultCharset();
      Charset expectedCharSet = Charset.defaultCharset();
      List<Delta<String>> fileDiffs = diff.diff(actual, actualCharSet, expected, expectedCharSet);
      if (fileDiffs.isEmpty()) {
        return Optional.empty();
      }

      diffs =
          fileDiffs
              .stream()
              .map(
                  change ->
                      FileChangeSection.builder()
                          .actual(String.join(",", change.getOriginal().getLines()))
                          .expected(String.join(",", change.getRevised().getLines()))
                          .lineNumber(change.lineNumber())
                          .build())
              .collect(Collectors.toList());

    } catch (IOException ex) {
      return Optional.of(new FileChangeDelta(actual.toPath(), ex.getMessage()));
    }

    return Optional.of(new FileChangeDelta(actual.toPath(), diffs));
  }

  private Map<Path, File> getRelativePathMap(File directory, FileFilter filter) throws IOException {
    if (!directory.exists()) {
      String message = String.format("Directory does not exist %s", quote(directory.getPath()));
      throw new IOException(message);
    } else if (!directory.isDirectory()) {
      String message = String.format("File is not a directory %s", quote(directory.getPath()));
      throw new IOException(message);
    }
    return Arrays.asList(directory.listFiles(filter))
        .stream()
        .collect(Collectors.toMap(file -> getRelativePath(directory, file), file -> file));
  }

  private static void throwIfDifferentFileTypes(File a, File b) throws IOException {
    if (a.isFile() != b.isFile()) {
      String message =
          String.format(
              "File %s is different file type than %s", quote(a.getPath()), quote(b.getPath()));
      throw new IOException(message);
    }
  }

  private static Path getRelativePath(File directory, File file) {
    return directory.toPath().relativize(file.toPath());
  }

  private static Set<Path> subtractPaths(Set<Path> a, Set<Path> b) {
    return a.stream().filter(path -> !b.contains(path)).collect(Collectors.toSet());
  }

  private static Set<Path> commonPaths(Set<Path> a, Set<Path> b) {
    return a.stream().filter(path -> b.contains(path)).collect(Collectors.toSet());
  }

  private static List<FileMissingDelta> toFileMissingDeltas(Collection<Path> paths) {
    return paths.stream().map(path -> new FileMissingDelta(path)).collect(Collectors.toList());
  }

  private static List<FileUnexpectedDelta> toFileUnexpectedDeltas(Collection<Path> paths) {
    return paths.stream().map(path -> new FileUnexpectedDelta(path)).collect(Collectors.toList());
  }

  private List<FileChangeDelta> getFileChangedDeltas(
      Collection<Path> paths, Map<Path, File> actualFileMap, Map<Path, File> expectedFileMap) {
    return paths
        .stream()
        .filter(path -> actualFileMap.get(path).isFile())
        .map(path -> diffFile(actualFileMap.get(path), expectedFileMap.get(path)))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .collect(Collectors.toList());
  }

  private List<DirectoryChangeDelta> getDirectoryChangedDeltas(
      Collection<Path> paths,
      FileFilter filter,
      Map<Path, File> actualFileMap,
      Map<Path, File> expectedFileMap) {
    return paths
        .stream()
        .filter(path -> actualFileMap.get(path).isDirectory())
        .map(path -> diffDirectory(actualFileMap.get(path), expectedFileMap.get(path), filter))
        .filter(optional -> optional.isPresent())
        .map(optional -> optional.get())
        .collect(Collectors.toList());
  }
}
