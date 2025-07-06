package com.playblog.blogservice.search.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PostRequest {
    private Long userId;
    private String title;
    private String content;
    private String thumbnailImageUrl;
    private List<String> imageUrls;
    private SubTopic subTopic;
}
