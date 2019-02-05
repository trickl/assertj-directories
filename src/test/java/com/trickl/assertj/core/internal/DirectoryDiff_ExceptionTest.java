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
 * This is used to test DirectoryDiff throws exceptions correctly
 *
 * @see DirectoryDiff
 */
public class DirectoryDiff_ExceptionTest {

    private static DirectoryDiff directoryDiff;
    String basePath = "src/test/resources/DirectoryUtils_equal/exception/";
     
    @BeforeClass
    public static void setUpOnce() {
      directoryDiff = new DirectoryDiff();    
    }
    
    @Test(expected = IOException.class)
    public void should_throw_if_directory_does_not_exist() throws IOException {      
      Path actual = FileSystems.getDefault().getPath(basePath + "does_not_exist");
      Path expected = FileSystems.getDefault().getPath(basePath + "does_not_exist");
      List<Delta<String>> diffs = directoryDiff.diff(actual.toFile(),  expected.toFile());      
    }    
}
