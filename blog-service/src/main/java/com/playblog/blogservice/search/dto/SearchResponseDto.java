package com.playblog.blogservice.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
public class SearchResponseDto {
    private String title;
    private String content;
    private String author;
    private String createdAt;
}
