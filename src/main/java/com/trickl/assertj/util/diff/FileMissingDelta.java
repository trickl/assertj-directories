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
 * <p>Describes the delete-delta between original and revised texts.
 *
 * @author <a href="dm.naumenko@gmail.com">Dmitry Naumenko</a>
 * @param <T> The type of the compared elements in the 'lines'.
 */
public class FileMissingDelta<T> extends Delta<T> {

  @Getter private final Path missingFilePath;

  public FileMissingDelta(Path missingFilePath) {
    super(new Chunk(0, Lists.list(missingFilePath)), new Chunk(0, Collections.EMPTY_LIST));
    this.missingFilePath = missingFilePath;
  }

  @Override
  public void applyTo(List<T> target) throws IllegalStateException {}

  @Override
  public TYPE getType() {
    return Delta.TYPE.DELETE;
  }

  @Override
  public void verify(List<T> target) throws IllegalStateException {}
}
