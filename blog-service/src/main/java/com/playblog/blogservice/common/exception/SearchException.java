package com.playblog.blogservice.common.exception;

public class SearchException extends RuntimeException {
    private final ErrorCode errorcode;

    public SearchException(ErrorCode errorcode) {
        super(errorcode.getMessage());
        this.errorcode = errorcode;
    }

    public ErrorCode getErrorCode() {
        return errorcode;
    }
}
