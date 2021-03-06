package com.trickl.assertj.core.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import org.assertj.core.util.diff.Delta;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This is used to test DirectoryDiff for correctness with flat directories
 *
 * @see DirectoryDiff
 */
public class DirectoryDiff_FlatTest {

  private static DirectoryDiff directoryDiff;
  String basePath = "src/test/resources/DirectoryDiff/simple/";

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
  public void should_return_missing_delta_when_missing_file1() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "missing_file1");
    Path expected = FileSystems.getDefault().getPath(basePath + "directory");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/simple/missing_file1\",\"missingFiles\":[\"file1.txt\"]}");
  }

  @Test
  public void should_return_unexpected_delta_when_unexpected_file2() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "missing_file2");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/simple/directory\",\"unexpectedFiles\":[\"file2.txt\"]}");
  }

  @Test
  public void should_return_change_delta_when_file1_different() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "directory");
    Path expected = FileSystems.getDefault().getPath(basePath + "file1_not_equal");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/simple/directory\",\"changedFiles\":[{\"path\":\"src/test/resources/DirectoryDiff/simple/directory/file1.txt\",\"diffs\":[{\"actual\":\"testtest\",\"expected\":\"test\",\"lineNumber\":1}]}]}");
  }
  
  @Test
  public void should_return_error_if_directory_does_not_exist() throws IOException {
    Path actual = FileSystems.getDefault().getPath(basePath + "does_not_exist");
    Path expected = FileSystems.getDefault().getPath(basePath + "directory");
    List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(), expected.toFile());
    assertThat(diffs).hasSize(1);
    assertThat(diffs.get(0)).hasToString("{\"path\":\"src/test/resources/DirectoryDiff/simple/does_not_exist\",\"errorMessage\":\"Directory does not exist \\u0027src/test/resources/DirectoryDiff/simple/does_not_exist\\u0027\"}");
  }
}
