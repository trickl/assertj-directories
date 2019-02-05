package com.trickl.assertj.directories.api;

import java.io.File;
import org.assertj.core.api.AbstractFileAssert;

/** 
 * Assertions for directories {@link File}s. 
 */
public class DirectoryAssert extends AbstractFileAssert<DirectoryAssert> {
  public DirectoryAssert(File actual) {
    super(actual, DirectoryAssert.class);
  }
}
