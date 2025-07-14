package com.playblog.blogservice.comment.controller;

import com.playblog.blogservice.comment.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    // 댓글 공감/ 취소 토글
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Map<String, Object>> toggleCommentLike(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        if (userId == 0) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        try {
            boolean isLiked = commentLikeService.toggleCommentLike(commentId, userId);
            long likeCount = commentLikeService.getCommentLikeCount(commentId);

            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 댓글 공감 상태 조회
    @GetMapping("/{commentId}/like/status")
    public ResponseEntity<Map<String, Object>> getCommentLikeStatus(
            @PathVariable Long commentId,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long userId
    ) {

        boolean isLiked = commentLikeService.isCommentLikedByUser(commentId, userId);
        long likeCount = commentLikeService.getCommentLikeCount(commentId);

        Map<String, Object> response = new HashMap<>();
        response.put("commentId", commentId);
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}