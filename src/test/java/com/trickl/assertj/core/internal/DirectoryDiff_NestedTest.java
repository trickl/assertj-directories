package com.trickl.assertj.core.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.util.diff.Delta;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is used to test DirectoryUtils for correctness with nested directories
 *
 * @see DirectoryDiff
 */
public class DirectoryDiff_NestedTest {

  private static DirectoryDiff directoryDiff;
  String basePath = "src/test/resources/DirectoryDiff/nested/";

  @BeforeClass
  public static void setUpOnce() {
    directoryDiff = new DirectoryDiff();
  }

  @Test
  public void should_return_empty_diff_list_if_comparing_self() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "directory");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).isEmpty();
  }

  @Test
  public void should_return_empty_diff_list_if_comparing_equal() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "equal");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).isEmpty();
  }

  @Test
  public void should_return_missing_delta_when_missing_directory1() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "missing_file1");
    Path expected = FileSystems.getDefault().getPath(basePath + "directory");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/nested/missing_file1\",\"missingFiles\":[\"directory1\"]}");
  }

  @Test
  public void should_return_unexpected_delta_when_unexpected_file2() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "missing_file2");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/nested/directory\",\"unexpectedFiles\":[\"directory2\"]}");
  }

  @Test
  public void should_return_change_delta_when_file1_different() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "file1_not_equal");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/nested/directory\",\"changedSubDirectories\":[{\"path\":\"src/test/resources/DirectoryDiff/nested/directory/directory1\",\"changedFiles\":[{\"path\":\"src/test/resources/DirectoryDiff/nested/directory/directory1/file1.txt\",\"diffs\":[{\"actual\":\"testtest\",\"expected\":\"test\",\"lineNumber\":1}]}]}]}");
  }
}
