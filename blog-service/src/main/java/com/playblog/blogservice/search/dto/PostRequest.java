package com.playblog.blogservice.search.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostRequest {
    private Long userId;
    private String title;
    private String content;
    private String thumbnailImageUrl;
    private List<String> imageUrls;
    private SubTopic subTopic;
}
