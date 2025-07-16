package com.playblog.blogservice.post.repository;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% " +
            "OR p.content LIKE %:keyword%")
    List<Post> findByTitleOrContent(@Param("keyword") String keyword);

    List<Post> findBySubTopic(SubTopic subTopic);

    Page<Post> findByUserIdInOrderByPublishedAtDesc(List<Long> userIds, Pageable pageable);

    Page<Post> findByUser_UserInfo_IdInOrderByPublishedAtDesc(List<Long> userInfoIds, Pageable pageable);

    Long user(User user);
}
