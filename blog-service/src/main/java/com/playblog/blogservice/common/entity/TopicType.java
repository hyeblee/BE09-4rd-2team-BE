package com.playblog.blogservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum TopicType {
    ENTERTAIN("엔터테인먼트.예술"),
    LIFE("생활.노하우.쇼핑"),
    HOBBY("취미.여가.여행"),
    KNOWLEDGE("지식.동향");

    private final String topicTypeName;
}
