package com.trickl.assertj.core.presentation;

import java.util.Collection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DirectoryChange {
  private final String path;
  private final Collection<String> missingFiles;
  private final Collection<String> unexpectedFiles;
  private final Collection<FileChange> changedFiles;
  private final Collection<DirectoryChange> changedSubDirectories;
  private final String errorMessage;
}
