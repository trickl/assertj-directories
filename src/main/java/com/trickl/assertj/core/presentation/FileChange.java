package com.trickl.assertj.core.presentation;

import java.util.Collection;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileChange {
  private final String path;
  private final Collection<FileChangeSection> diffs;
  private final String errorMessage;
}
