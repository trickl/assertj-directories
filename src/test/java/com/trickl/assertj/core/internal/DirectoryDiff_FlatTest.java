package com.trickl.assertj.core.internal;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.diff.Delta;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

/**
 * This is used to test DirectoryDiff for correctness with flat directories
 *
 * @see DirectoryDiff
 */
public class DirectoryDiff_FlatTest {

    private static DirectoryDiff directoryDiff;
    String basePath = "src/test/resources/DirectoryUtils_equal/simple/";
     
    @BeforeClass
    public static void setUpOnce() {
      directoryDiff = new DirectoryDiff();    
    }
    
    @Test(expected = IOException.class)
    public void should_throw_if_directory_does_not_exist() throws IOException {      
      Path actual = FileSystems.getDefault().getPath(basePath + "does_not_exist");
      Path expected = FileSystems.getDefault().getPath(basePath + "directory");
      List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
      assertThat(diffs).isEmpty();
    }
    
    @Test
    public void should_return_empty_diff_list_if_comparing_self() throws IOException {      
      Path actual = FileSystems.getDefault().getPath(basePath + "directory");
      Path expected = FileSystems.getDefault().getPath(basePath + "directory");
      List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
      assertThat(diffs).isEmpty();
    }
        
    @Test
    public void should_return_empty_diff_list_if_comparing_equal() throws IOException {
        Path actual = FileSystems.getDefault().getPath(basePath + "directory");
        Path expected = FileSystems.getDefault().getPath(basePath + "equal");
        List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
        assertThat(diffs).isEmpty();
    }

    @Test
    public void should_return_delete_delta_when_missing_file1() throws IOException {
        Path actual = FileSystems.getDefault().getPath(basePath + "missing_file1");
        Path expected = FileSystems.getDefault().getPath(basePath + "directory");
        List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
        assertThat(diffs).hasSize(1);
        assertThat(diffs.get(0)).hasToString("TODO");
    }

    @Test
    public void should_return_insert_delta_when_surplus_file2() throws IOException {
        Path actual = FileSystems.getDefault().getPath(basePath + "directory");
        Path expected = FileSystems.getDefault().getPath(basePath + "missing_file2");
        List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
        assertThat(diffs).hasSize(1);
        assertThat(diffs.get(0)).hasToString("TODO");
    }

    @Test
    public void should_return_change_delta_when_file1_different() throws IOException {
        Path actual = FileSystems.getDefault().getPath(basePath + "directory");
        Path expected = FileSystems.getDefault().getPath(basePath + "file1_not_equal");
        List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());
        assertThat(diffs).hasSize(1);
        assertThat(diffs.get(0)).hasToString("TODO");
    }
}
