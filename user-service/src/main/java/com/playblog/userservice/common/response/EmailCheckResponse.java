package com.playblog.userservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailCheckResponse {
  private String status;
  private boolean isDuplicate;
}
