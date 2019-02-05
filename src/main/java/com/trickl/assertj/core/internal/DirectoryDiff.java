package com.trickl.assertj.core.internal;

import static org.assertj.core.util.Strings.quote;

import com.trickl.assertj.util.diff.FileChangeDelta;
import com.trickl.assertj.util.diff.FileMissingDelta;
import com.trickl.assertj.util.diff.FileUnexpectedDelta;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Value;
import org.assertj.core.internal.Diff;
import org.assertj.core.util.Lists;
import org.assertj.core.util.VisibleForTesting;
import org.assertj.core.util.diff.Delta;
import org.assertj.core.util.diff.Patch;

/** Compares the contents of two directories. */
@VisibleForTesting
public class DirectoryDiff {

  @Value
  private class MappedFile {
    private Path actualPath;
    private File actualFile;
    private File expectedFile;
  }

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
   * @throws IOException If a file or directory cannot be read
   */
  @VisibleForTesting
  public List<Delta<String>> diff(File actual, File expected, FileFilter filter)
      throws IOException {
    Map<Path, File> actualFileMap = getRelativePathMap(actual, filter);
    Map<Path, File> expectedFileMap = getRelativePathMap(expected, filter);

    Set<Path> missingFiles = subtract(expectedFileMap.keySet(), actualFileMap.keySet());
    Set<Path> unexpectedFiles = subtract(actualFileMap.keySet(), expectedFileMap.keySet());

    Map<Path, List<Delta<String>>> fileDiffs = getFileDiffs(actualFileMap, expectedFileMap, filter);

    return summariseDiffs(missingFiles, unexpectedFiles, fileDiffs).getDeltas();
  }

  private Patch<String> summariseDiffs(
      Set<Path> missingFiles, Set<Path> unexpectedFiles, Map<Path, List<Delta<String>>> fileDiffs) {
    Patch<String> patch = new Patch<>();
    missingFiles.forEach(
        path -> {
          patch.addDelta(new FileMissingDelta(path));
        });
    unexpectedFiles.forEach(
        path -> {
          patch.addDelta(new FileUnexpectedDelta(path));
        });
    fileDiffs
        .entrySet()
        .forEach(
            fileDiff -> {
              if (!fileDiff.getValue().isEmpty()) {
                patch.addDelta(new FileChangeDelta(fileDiff.getKey()));
              }
            });

    return patch;
  }

  private Map<Path, List<Delta<String>>> getFileDiffs(
      Map<Path, File> actual, Map<Path, File> expected, FileFilter filter) {
    return actual
        .entrySet()
        .stream()
        .map(
            actualPathAndFile ->
                new MappedFile(
                    actualPathAndFile.getKey(),
                    actualPathAndFile.getValue(),
                    expected.get(actualPathAndFile.getKey())))
        .filter(mappedFile -> mappedFile.expectedFile != null)
        .map(
            mappedFile ->
                new AbstractMap.SimpleImmutableEntry<>(
                      mappedFile.getActualPath(),
                    diffFiles(mappedFile.getActualFile(), mappedFile.getExpectedFile(), filter)))
        .filter(pathAndDiff -> !pathAndDiff.getValue().isEmpty())
        .collect(
            Collectors.toMap(
                pathAndDiff -> pathAndDiff.getKey(), pathAndDiff -> pathAndDiff.getValue()));
  }

  private List<Delta<String>> diffFiles(File actual, File expected, FileFilter filter) {
    try {
      if (actual.isFile() && expected.isFile()) {
        Charset actualCharSet = Charset.defaultCharset();
        Charset expectedCharSet = Charset.defaultCharset();
        return diff.diff(actual, actualCharSet, expected, expectedCharSet);

      } else if (actual.isDirectory() && expected.isDirectory()) {
        return diff(actual, expected, filter);
      }
    } catch (IOException ex) {
      return Lists.list(new FileChangeDelta(actual.toPath()));
    }

    return Lists.list(new FileChangeDelta(actual.toPath()));
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

  private static Path getRelativePath(File directory, File file) {
    return directory.toPath().relativize(file.toPath());
  }

  private static Set<Path> subtract(Set<Path> a, Set<Path> b) {
    return a.stream().filter(path -> !b.contains(path)).collect(Collectors.toSet());
  }
}
