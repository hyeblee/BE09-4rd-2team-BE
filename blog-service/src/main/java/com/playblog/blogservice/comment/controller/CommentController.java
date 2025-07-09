package com.playblog.blogservice.comment.controller;

import com.playblog.blogservice.comment.entity.Comment;
import com.playblog.blogservice.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 작성
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @RequestBody Map<String, Object> request
    ) {
        String content = (String) request.get("content");
        Boolean isSecret = (Boolean) request.getOrDefault("isSecret", false);
        // TODO: JWT 토큰에서 사용자 ID 추출 (현재는 임시로 1L)
        Long authorId = 1L;

        Comment comment = commentService.createComment(postId, authorId, content, isSecret);

        Map<String, Object> response = new HashMap<>();
        response.put("commentId", comment.getId());
        response.put("message", "댓글이 작성되었습니다");

        return ResponseEntity.ok(response);
    }

    // 2. 댓글 목록 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long postId) {

        Long requestUserId = 1L;
        Long postAuthorId = 1L;

        List<Map<String, Object>> commentList = commentService.getCommentsWithDetails(postId, requestUserId, postAuthorId);


        Map<String, Object> response = new HashMap<>();
        response.put("comments", commentList);
        response.put("totalCount", commentList.size());

        return ResponseEntity.ok(response);
    }

    // 3. 댓글 수정
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> updateComment(
            @PathVariable Long commentId,
            @RequestBody Map<String, Object> request
    ) {
        String content = (String) request.get("content");
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long requestUserId = 1L;

        Comment updatedComment = commentService.updateComment(commentId, content, requestUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("commentId", updatedComment.getId());
        response.put("message", "댓글이 수정되었습니다");

        return ResponseEntity.ok(response);
    }

    // 4. 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long requestUserId = 1L;

        commentService.deleteComment(commentId, requestUserId);

        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
