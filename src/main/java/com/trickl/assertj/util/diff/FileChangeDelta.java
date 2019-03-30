package com.trickl.assertj.util.diff;

import com.google.gson.Gson;
import com.trickl.assertj.core.presentation.FileChange;
import com.trickl.assertj.core.presentation.FileChangeSection;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Chunk;
import org.assertj.core.util.diff.Delta;

/**
 * Initially copied from https://code.google.com/p/java-diff-utils/.
 *
 * <p>Describes the change-delta between original and revised texts.
 *
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @param <T> The type of the compared elements in the 'lines'.
 */
public class FileChangeDelta<T> extends Delta<T> {

  @Getter private final FileChange summary;
  
  /**
   * Create a file change delta.
   * @param path The path of the changed file.
   * @param errorMessage The error encountered
   */
  public FileChangeDelta(Path path, String errorMessage) {
    super(toChunk(path), toChunk(path));
  
    summary = FileChange.builder()
        .path(path.toString())
        .errorMessage(errorMessage)
        .build();
  }
  
  /**
   * Create a file change delta.
   * @param changedFilePath The path of the changed file.
   * @param diffs The list of changes.
   */
  public FileChangeDelta(Path changedFilePath, List<FileChangeSection> diffs) {
    super(new Chunk(0, Lists.list(changedFilePath)), new Chunk(0, Lists.list(changedFilePath)));
  
    summary = FileChange.builder()
        .path(changedFilePath.toString())
        .diffs(diffs)
        .build();
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
   * Represent a file path as an empty chunk.
   * @param <T> Chunk type
   * @param path The file path
   * @return An empty chunk
   */
  public static <T> Chunk<T> toChunk(Path path) {
    return new Chunk(0, Lists.list(path));
  }
}
