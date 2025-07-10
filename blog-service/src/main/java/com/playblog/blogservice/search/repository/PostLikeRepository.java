package com.playblog.blogservice.search.repository;

import com.playblog.blogservice.search.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    @Query("SELECT l.post.id, COUNT(l) FROM PostLike l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<Object[]> countLikesByPostIds(@Param("postIds") List<Long> postIds);
}
