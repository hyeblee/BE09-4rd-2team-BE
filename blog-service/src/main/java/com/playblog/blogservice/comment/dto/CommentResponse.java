package com.playblog.blogservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long commentId;
    private AuthorDto author;
    private String comment;
    private Boolean isSecret;
    private Long likeCount;
    private Boolean isLiked;
    private LocalDateTime createdAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDto {
        private Long id;
        private String nickname;
        private String profileImage;
    }
}