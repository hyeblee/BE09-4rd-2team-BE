package com.playblog.blogservice.post.repository;

import com.playblog.blogservice.post.entity.TestLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestLikeRepository extends JpaRepository<TestLike, Long> {
    Boolean existsByPostIdAndUserId(Long postId, Long userId);
    Long countByPostId(Long postId);
}
