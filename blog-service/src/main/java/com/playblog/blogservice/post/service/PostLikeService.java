package com.playblog.blogservice.post.service;

import com.playblog.blogservice.post.entity.PostLike;
import com.playblog.blogservice.post.repository.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;

    // 1. 게시글 공감 토글 (있으면 취소, 없으면 추가)
    @Transactional
    public boolean togglePostLike(Long postId, Long userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            // 이미 공감했으면 공감 취소
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);
            return false; // 공감 취소됨
        } else {
            // 공감하지 않았으면 공감 추가
            PostLike newLike = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
                    .build();
            postLikeRepository.save(newLike);
            return true; // 공감 추가됨
        }
    }

    // 2. 게시글의 공감 수 조회
    public long getPostLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    // 3. 게시글 공감 여부 확인
    public boolean isPostLikedByUser(Long postId, Long userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    // 4. 게시글에 공감한 사용자 목록 조회
    public List<PostLike> getPostLikeUsers(Long postId) {
        return postLikeRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    // 5. 공감한 게시글 목록 조회
    public List<PostLike> getUserLikedPosts(Long userId) {
        return postLikeRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // 6. 게시글 공감 강제 추가 (중복 체크 없이)
    @Transactional
    public PostLike addPostLike(Long postId, Long userId) {
        // 이미 공감했는지 체크
        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("이미 공감한 게시글입니다.");
        }

        PostLike postLike = PostLike.builder()
                .postId(postId)
                .userId(userId)
                .build();

        return postLikeRepository.save(postLike);
    }

    // 7. 게시글 공감 강제 삭제
    @Transactional
    public void removePostLike(Long postId, Long userId) {
        if (!postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new RuntimeException("공감하지 않은 게시글입니다.");
        }

        postLikeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}
