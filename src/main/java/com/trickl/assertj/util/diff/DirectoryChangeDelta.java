package com.trickl.assertj.util.diff;

import static com.trickl.assertj.util.diff.FileChangeDelta.toChunk;

import com.google.gson.Gson;
import com.trickl.assertj.core.presentation.DirectoryChange;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.assertj.core.util.diff.Delta;

/**
 * Initially copied from https://code.google.com/p/java-diff-utils/.
 *
 * <p>Describes the change-delta between original and revised texts.
 *
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @param <T> The type of the compared elements in the 'lines'.
 */
public class DirectoryChangeDelta<T> extends Delta<T> {

  @Getter private final DirectoryChange summary;

  /**
   * Create a directory change delta.
   *
   * @param path The directory path
   * @param errorMessage Any error encountered
   */
  public DirectoryChangeDelta(Path path, String errorMessage) {
    super(toChunk(path), FileChangeDelta.toChunk(path));

    summary = DirectoryChange.builder().path(path.toString()).errorMessage(errorMessage).build();
  }

  /**
   * Create a directory change delta.
   *
   * @param path The directory path
   * @param missingFiles Any missing files
   * @param unexpectedFiles Any unexpected files
   * @param changedSubDirectories Any changed subdirectories
   * @param changedFiles Any change files
   */
  public DirectoryChangeDelta(
      Path path,
      Collection<FileMissingDelta> missingFiles,
      Collection<FileUnexpectedDelta> unexpectedFiles,
      Collection<DirectoryChangeDelta> changedSubDirectories,
      Collection<FileChangeDelta> changedFiles) {
    super(FileChangeDelta.toChunk(path), FileChangeDelta.toChunk(path));

    summary =
        DirectoryChange.builder()
            .path(path.toString())
            .unexpectedFiles(
                nullIfEmpty(
                    unexpectedFiles
                        .stream()
                        .map(delta -> delta.getUnexpectedFilePath().toString())
                        .collect(Collectors.toList())))
            .missingFiles(
                nullIfEmpty(
                    missingFiles
                        .stream()
                        .map(delta -> delta.getMissingFilePath().toString())
                        .collect(Collectors.toList())))
            .changedFiles(
                nullIfEmpty(
                    changedFiles
                        .stream()
                        .map(delta -> delta.getSummary())
                        .collect(Collectors.toList())))
            .changedSubDirectories(
                nullIfEmpty(
                    changedSubDirectories
                        .stream()
                        .map(delta -> delta.getSummary())
                        .collect(Collectors.toList())))
            .build();
  }

  private static <T> Collection<T> nullIfEmpty(Collection<T> list) {
    return list.isEmpty() ? null : list;
  }

  private static <T> boolean nullSafeIsEmpty(Collection<T> col) {
    return col == null || col.isEmpty();
  }

  @Override
  public void applyTo(List<T> target) throws IllegalStateException {}

  @Override
  public void verify(List<T> target) throws IllegalStateException {}

  @Override
  public TYPE getType() {
    return Delta.TYPE.CHANGE;
  }

  @Override
  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(summary);
  }

  /**
   * Return true if there any changes in this delta.
   *
   * @return True if changes exist
   */
  public boolean hasChanges() {
    return !nullSafeIsEmpty(summary.getMissingFiles())
        || !nullSafeIsEmpty(summary.getUnexpectedFiles())
        || !nullSafeIsEmpty(summary.getChangedFiles())
        || !nullSafeIsEmpty(summary.getChangedSubDirectories());
  }
}
