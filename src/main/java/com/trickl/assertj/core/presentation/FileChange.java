package com.trickl.assertj.core.presentation;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileChange {
  private final String path;
  private final List<FileChangeSection> diffs;
  private final String errorMessage;
}
