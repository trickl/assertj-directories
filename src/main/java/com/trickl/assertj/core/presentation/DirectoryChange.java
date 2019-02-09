package com.trickl.assertj.core.presentation;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DirectoryChange {
  private final String path;
  private final List<String> missingFiles;
  private final List<String> unexpectedFiles;
  private final List<FileChange> changedFiles;
  private final List<DirectoryChange> changedSubDirectories;
  private final String errorMessage;
}
