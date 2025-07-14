package com.playblog.blogservice.postlike.controller;

import com.playblog.blogservice.postlike.dto.PostLikeResponse;
import com.playblog.blogservice.postlike.dto.PostLikesResponse;
import com.playblog.blogservice.postlike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api")
public class PostLikeController {

    private final PostLikeService postLikeService;

    /**
     * 포스트 공감/취소 (토글)
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> togglePostLike(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        if (userId == 0) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        PostLikeResponse response = postLikeService.togglePostLike(postId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 공감한 블로거 목록 조회
     */
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<PostLikesResponse> getPostLikes(@PathVariable Long postId) {
        PostLikesResponse response = postLikeService.getPostLikeUsers(postId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 공감 여부 확인
     */
    @GetMapping("/posts/{postId}/like/status")
    public ResponseEntity<PostLikeResponse> getPostLikeStatus(
            @PathVariable Long postId,
            @RequestHeader(value = "X-User-Id", defaultValue = "0") Long userId
    ) {
        PostLikeResponse response = postLikeService.isPostLikedByUser(postId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 공감 수 조회 (Post, Search 위한 용도)
     * - 검색: 검색 결과에서 사용
     * - 게시글: 게시글 상세/목록에서 사용
     */
    @GetMapping("/posts/{postId}/like/count")
    public ResponseEntity<Map<String, Object>> getPostLikeCount(@PathVariable Long postId) {
        long likeCount = postLikeService.getPostLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("postId", postId);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}