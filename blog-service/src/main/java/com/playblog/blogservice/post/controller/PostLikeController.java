package com.playblog.blogservice.post.controller;

import com.playblog.blogservice.post.entity.PostLike;
import com.playblog.blogservice.post.service.PostLikeService;
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
public class PostLikeController {

    private final PostLikeService postLikeService;

    // 1. 포스트 공감/취소 (토글)
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> togglePostLike(@PathVariable Long postId) {
        // TODO: JWT 토큰에서 사용자 ID 추출 (현재는 임시로 1L)
        Long userId = 1L;

        boolean isLiked = postLikeService.togglePostLike(postId, userId);
        long likeCount = postLikeService.getPostLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    // 2. 공감한 블로거 목록 조회
    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<Map<String, Object>> getPostLikes(@PathVariable Long postId) {
        List<PostLike> postLikes = postLikeService.getPostLikeUsers(postId);

        List<Map<String, Object>> likedUsers = postLikes.stream()
                .map(postLike -> {
                    Map<String, Object> userMap = new HashMap<>();
                    Map<String, Object> user = new HashMap<>();
                    user.put("id", postLike.getUserId());
                    user.put("nickname", "임시닉네임"); // TODO: User Service 연동
                    user.put("profileImage", "임시프로필URL");
                    user.put("introduceText", "한 줄 소개");

                    userMap.put("user", user);
                    userMap.put("isNeighbor", true); // TODO: 이웃 여부 확인
                    userMap.put("likedAt", postLike.getCreatedAt());
                    return userMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("likedUsers", likedUsers);
        response.put("totalCount", likedUsers.size());

        return ResponseEntity.ok(response);
    }

    // 3. 게시글 공감 여부 확인
    @GetMapping("/posts/{postId}/like/status")
    public ResponseEntity<Map<String, Object>> getPostLikeStatus(@PathVariable Long postId) {
        // TODO: JWT 토큰에서 사용자 ID 추출
        Long userId = 1L;

        boolean isLiked = postLikeService.isPostLikedByUser(postId, userId);
        long likeCount = postLikeService.getPostLikeCount(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", isLiked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }
}
