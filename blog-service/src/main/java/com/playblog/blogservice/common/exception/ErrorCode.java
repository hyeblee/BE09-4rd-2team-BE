package com.playblog.blogservice.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    POST_NOT_FOUND("POST_404", "해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("USER_404", "해당 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    TOPIC_NOT_FOUND("TOPIC_404", "해당 주제를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PARAMETER("REQ_400", "잘못된 요청 파라미터입니다.", HttpStatus.BAD_REQUEST),
    EMPTY_RESULT("SEARCH_204", "검색 결과가 없습니다.", HttpStatus.NO_CONTENT),
    UNAUTHORIZED_ACCESS("AUTH_401", "권한이 없습니다.", HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR("SYS_500", "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
