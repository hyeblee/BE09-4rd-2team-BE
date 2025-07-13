package com.playblog.blogservice.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikesResponse {

    private List<PostLikeUserResponse> likedUsers; // 공감한 블로거 목록
    private Long totalCount;
}