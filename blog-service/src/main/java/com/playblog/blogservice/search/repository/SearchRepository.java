package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.search.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% " +
            "OR p.content LIKE %:keyword%")
    List<Post> findByTitleOrContent(@Param("keyword") String keyword);

    List<Post> findBySubTopic(SubTopic subTopic);

    Page<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds, Pageable pageable);
}
