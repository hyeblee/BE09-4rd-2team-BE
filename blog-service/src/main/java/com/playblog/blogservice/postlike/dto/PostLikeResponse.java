package com.playblog.blogservice.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeResponse {

    private Boolean isLiked;
    private Long likeCount;
}