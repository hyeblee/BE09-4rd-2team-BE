package com.playblog.blogservice.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    private Boolean isSecret = false;
}