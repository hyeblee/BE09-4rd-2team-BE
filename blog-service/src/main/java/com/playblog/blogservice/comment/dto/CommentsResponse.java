package com.playblog.blogservice.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentsResponse {

    private List<CommentResponse> comments; // 댓글 목록
    private Long totalCount; // 전체 댓글
}