package com.playblog.userservice.common.exception;

public class DuplicateEmailException extends RuntimeException {
  public DuplicateEmailException() {
    super("이미 사용 중인 이메일입니다.");
  }

  public DuplicateEmailException(String message) {
    super(message);
  }
}
