package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.search.entity.TestPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SearchRepository extends JpaRepository<TestPost, Long> {

    Page<TestPost> findAll(Pageable pageable);

    @Query("SELECT p FROM TestPost p WHERE p.title LIKE %:keyword% " +
            "OR p.content LIKE %:keyword%")
    List<TestPost> findByTitleOrContent(@Param("keyword") String keyword);

    List<TestPost> findBySubTopic(SubTopic subTopic);

    Page<TestPost> findByUserIdInOrderByPublishedAtDesc(List<Long> userIds, Pageable pageable);
}
