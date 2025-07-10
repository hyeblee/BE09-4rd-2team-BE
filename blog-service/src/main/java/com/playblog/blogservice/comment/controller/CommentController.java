package com.playblog.blogservice.comment.controller;

import com.playblog.blogservice.comment.dto.CommentRequest;
import com.playblog.blogservice.comment.dto.CommentResponse;
import com.playblog.blogservice.comment.dto.CommentsResponse;
import com.playblog.blogservice.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request
    ) {
        // TODO: JWT 토큰에서 사용자 ID 추출 (현재는 임시로 1L)
        Long authorId = 1L;

        CommentResponse response = commentService.createComment(postId, request, authorId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentsResponse> getComments(@PathVariable Long postId) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long requestUserId = 1L;
        // TODO: Post Service에서 게시글 작성자 ID 가져오기
        Long postAuthorId = 1L;

        CommentsResponse response = commentService.getCommentsByPostId(postId, requestUserId, postAuthorId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request
    ) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long requestUserId = 1L;

        CommentResponse response = commentService.updateComment(commentId, request, requestUserId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long requestUserId = 1L;

        commentService.deleteComment(commentId, requestUserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 수 조회 (Post Service용)
     */
    @GetMapping("/posts/{postId}/comments/count")
    public ResponseEntity<Map<String, Object>> getCommentCount(@PathVariable Long postId) {
        Long count = commentService.getCommentCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}