package com.trickl.assertj.core.internal;

import static java.lang.String.format;
import static org.assertj.core.error.ShouldHaveSameContent.shouldHaveSameContent;
import static org.assertj.core.util.Preconditions.checkArgument;
import static org.assertj.core.util.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import org.assertj.core.api.AssertionInfo;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Files;
import org.assertj.core.util.VisibleForTesting;
import org.assertj.core.util.diff.Delta;

/** Reusable assertions for directories <code>{@link File}</code>s. */
public class Directories {

  private static final String UNABLE_TO_COMPARE_DIR_CONTENTS =
      "Unable to compare contents of directories:<%s> and:<%s>";
  private static final Directories INSTANCE = new Directories();

  /**
   * Returns the singleton instance of this class.
   *
   * @return the singleton instance of this class.
   */
  public static Directories instance() {
    return INSTANCE;
  }

  @VisibleForTesting DirectoryDiff directoryDiff = new DirectoryDiff();
  @VisibleForTesting Failures failures = Failures.instance();
  @VisibleForTesting Files files = Files.instance();

  @VisibleForTesting
  Directories() {}

  /**
   * Asserts that the given directories have same content.
   *
   * @param info contains information about the assertion.
   * @param actual the "actual" directory.
   * @param expected the "expected" directory.
   * @throws NullPointerException if {@code expected} is {@code null}.
   * @throws IllegalArgumentException if {@code expected} is not an existing directory.
   * @throws AssertionError if {@code actual} is {@code null}.
   * @throws AssertionError if {@code actual} is not an existing directory.
   * @throws UncheckedIOException if an I/O error occurs.
   * @throws AssertionError if the given directories do not have same content.
   */
  public void assertSameContentAs(AssertionInfo info, File actual, File expected) {
    verifyIsDirectory(expected);
    files.assertIsDirectory(info, actual);

    try {
      List<Delta<String>> diffs = directoryDiff.diff(actual, expected);
      if (diffs.isEmpty()) {
        return;
      }
      throw failures.failure(info, shouldHaveSameContent(actual, expected, diffs));
    } catch (IOException e) {
      throw new UncheckedIOException(format(UNABLE_TO_COMPARE_DIR_CONTENTS, actual, expected), e);
    }
  }

  private void verifyIsDirectory(File expected) {
    checkNotNull(expected, "The directory to compare to should not be null");
    checkArgument(
        expected.isDirectory(),
        "Expected directory:<'%s'> should be an existing directory",
        expected);
  }
}
