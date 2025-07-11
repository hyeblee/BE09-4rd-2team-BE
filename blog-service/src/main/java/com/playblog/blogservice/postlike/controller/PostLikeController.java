package com.playblog.blogservice.postlike.controller;

import com.playblog.blogservice.postlike.dto.PostLikeResponse;
import com.playblog.blogservice.postlike.dto.PostLikesResponse;
import com.playblog.blogservice.postlike.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 1. 포스트 공감/취소 (토글)
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> togglePostLike(@PathVariable Long postId) {
        // TODO: JWT 토큰에서 사용자 ID 추출 (현재는 임시로 1L)
        Long userId = 1L;

        PostLikeResponse response = postLikeService.togglePostLike(postId, userId);

        return ResponseEntity.ok(response);
    }

    // 2. 공감한 블로거 목록 조회
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<PostLikesResponse> getPostLikes(@PathVariable Long postId) {
        PostLikesResponse response = postLikeService.getPostLikeUsers(postId);

        return ResponseEntity.ok(response);
    }

    // 3. 게시글 공감 여부 확인
    @GetMapping("/posts/{postId}/like/status")
    public ResponseEntity<PostLikeResponse> getPostLikeStatus(@PathVariable Long postId) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long userId = 1L;

        PostLikeResponse response = postLikeService.isPostLikedByUser(postId, userId);

        return ResponseEntity.ok(response);
    }
}
