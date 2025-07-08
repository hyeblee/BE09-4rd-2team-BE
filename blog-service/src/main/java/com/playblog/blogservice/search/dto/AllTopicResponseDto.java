package com.playblog.blogservice.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class AllTopicResponseDto {
        private String topicType;
        private String topicName;
        private List<SubTopicResponseDto> subTopics;
}
