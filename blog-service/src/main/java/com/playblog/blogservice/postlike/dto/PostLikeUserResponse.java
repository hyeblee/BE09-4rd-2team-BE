package com.playblog.blogservice.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeUserResponse {

    private UserDto user;
    private boolean isNeighbor;
    private LocalDateTime likedAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String nickname;
        private String profileImage;
        private String introduceText;
    }
}