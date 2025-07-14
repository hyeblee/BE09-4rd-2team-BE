package com.playblog.blogservice.comment.controller;

import com.playblog.blogservice.comment.dto.CommentRequest;
import com.playblog.blogservice.comment.dto.CommentResponse;
import com.playblog.blogservice.comment.dto.CommentsResponse;
import com.playblog.blogservice.comment.service.CommentService;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;

    /**
     * 댓글 작성
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request,
            @RequestHeader("X-User-Id") Long authorId
    ) {
        if (authorId == 0) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        CommentResponse response = commentService.createComment(postId, request, authorId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 목록 조회 - post(게시글)에서 작성자 id 가져오기
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentsResponse> getComments(@PathVariable Long postId, @RequestHeader(value = "X-User-Id", defaultValue = "0") Long requestUserId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        Long postAuthorId = post.getUser()!=null?post.getUser().getId():null;

        CommentsResponse response = commentService.getCommentsByPostId(postId, requestUserId, postAuthorId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request,
            @RequestHeader("X-User-Id") Long requestUserId
    ) {
        if (requestUserId == 0) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        CommentResponse response = commentService.updateComment(commentId, request, requestUserId);

        return ResponseEntity.ok(response);
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long requestUserId
    ) {
        if (requestUserId == 0) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        commentService.deleteComment(commentId, requestUserId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 수 조회 (Post, Search 위한 용도)
     * 사용처: 검색 검색결과, 게시글 상세/목록
     */
    @GetMapping("/posts/{postId}/comments/count")
    public ResponseEntity<Map<String, Object>> getCommentCount(@PathVariable Long postId) {
        Long count = commentService.getCommentCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("postId", postId);
        response.put("commentCount", count);

        return ResponseEntity.ok(response);
    }
}