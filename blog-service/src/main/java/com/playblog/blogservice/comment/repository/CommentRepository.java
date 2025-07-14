package com.playblog.blogservice.comment.repository;

import com.playblog.blogservice.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 댓글 목록 조회 (삭제되지 않은 것만)
    List<Comment> findByPost_IdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    // 게시글의 댓글 수 조회 (삭제되지 않은 것만)
    long countByPost_IdAndIsDeletedFalse(Long postId);
}