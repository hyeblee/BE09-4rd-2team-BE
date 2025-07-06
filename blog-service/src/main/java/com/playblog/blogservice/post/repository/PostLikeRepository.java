package com.playblog.blogservice.post.repository;

import com.playblog.blogservice.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 특정 사용자가 특정 게시글에 공감했는지 확인
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 특정 사용자가 특정 게시글에 공감했는지 여부
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시글의 공감 수
    long countByPostId(Long postId);

    // 특정 게시글에 공감한 사용자 목록 (최신순)
    List<PostLike> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 특정 사용자가 공감한 게시글 목록 (최신순)
    List<PostLike> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 사용자의 특정 게시글 공감 삭제
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
