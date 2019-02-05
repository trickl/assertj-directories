package com.trickl.assertj.util.diff;

import static org.assertj.core.util.Strings.quote;

import java.nio.file.Path;
import java.util.List;
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

  private final Path changedFilePath;

  public FileChangeDelta(Path changedFilePath) {
    super(new Chunk(0, Lists.list(changedFilePath)), new Chunk(0, Lists.list(changedFilePath)));
    this.changedFilePath = changedFilePath;
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
    return String.format("File %s CHANGED", changedFilePath);
  }
}
