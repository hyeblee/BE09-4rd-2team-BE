package com.playblog.blogservice.search.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String content;
    private String nickname;      // 작성자
    private String blogTitle;     // 블로그 이름
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private SubTopic subTopic;
}
