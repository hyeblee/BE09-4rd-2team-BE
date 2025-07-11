package com.playblog.blogservice.postlike.repository;

import com.playblog.blogservice.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 게시글에 공감 확인
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 게시글에 공감 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 게시글의 공감 수
    long countByPostId(Long postId);

    // 게시글에 공감한 사용자 목록 (최신순)
    List<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 게시글 공감 삭제
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
