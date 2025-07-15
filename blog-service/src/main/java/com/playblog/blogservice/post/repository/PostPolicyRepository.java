package com.playblog.blogservice.post.repository;

import com.playblog.blogservice.post.entity.PostPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostPolicyRepository extends JpaRepository<PostPolicy, Long> {
    Optional<PostPolicy> findByPostId(Long id);

    void deleteByPostId(Long postId);

    // 정책 저장용 Repository
}
