package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.search.entity.TestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<TestComment, Long> {
    @Query("SELECT c.testPost.id, COUNT(c) FROM TestComment c WHERE c.testPost.id IN :postIds GROUP BY c.testPost.id")
    List<Object[]> countCommentsByPostIds(@Param("postIds") List<Long> postIds);
}
