package com.playblog.blogservice.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogSearchDto {
    private String blogTitle;
    private String profileIntro;
    private String profileImageUrl;
    private String nickname;
    private String blogId;
}
