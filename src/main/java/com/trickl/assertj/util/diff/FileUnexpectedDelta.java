package com.trickl.assertj.util.diff;

import java.nio.file.Path;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.assertj.core.util.Lists;
import org.assertj.core.util.diff.Chunk;
import org.assertj.core.util.diff.Delta;

/**
 * Initially copied from https://code.google.com/p/java-diff-utils/.
 *
 * <p>Describes the add-delta between original and revised texts.
 *
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @param <T> The type of the compared elements in the 'lines'.
 */
public class FileUnexpectedDelta<T> extends Delta<T> {

  @Getter private final Path unexpectedFilePath;

  public FileUnexpectedDelta(Path unexpectedFilePath) {
    super(new Chunk(0, Collections.EMPTY_LIST), new Chunk(0, Lists.list(unexpectedFilePath)));
    this.unexpectedFilePath = unexpectedFilePath;
  }

  @Override
  public void applyTo(List<T> target) {}

  @Override
  public void verify(List<T> target) throws IllegalStateException {}

  @Override
  public TYPE getType() {
    return Delta.TYPE.INSERT;
  }
}
