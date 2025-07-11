package com.playblog.blogservice.postservice.post.repository;

import com.playblog.blogservice.postservice.post.entity.PostPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostPolicyRepository extends JpaRepository<PostPolicy, Long> {
    Optional<PostPolicy> findByPostId(Long id);

    // 정책 저장용 Repository
}
