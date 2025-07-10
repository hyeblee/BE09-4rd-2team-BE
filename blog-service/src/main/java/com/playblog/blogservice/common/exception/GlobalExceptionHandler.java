package com.playblog.blogservice.common.exception;

import com.playblog.blogservice.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SearchException.class)
    public ResponseEntity<ApiResponse<String>> handleSearchException(SearchException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(400)
                .body(ApiResponse.fail(errorCode.getMessage(), errorCode.getCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGeneralException(Exception e) {
        // 로그를 남기거나 추가적인 처리를 할 수 있습니다.
        return ResponseEntity
                .status(500)
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR.getCode()));
    }
}

