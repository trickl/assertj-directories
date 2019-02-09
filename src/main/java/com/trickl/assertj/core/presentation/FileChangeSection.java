package com.trickl.assertj.core.presentation;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FileChangeSection {
  private final String actual;
  private final String expected;
  private final int lineNumber;
}
