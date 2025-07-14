package com.playblog.blogservice.comment.repository;

import com.playblog.blogservice.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 게시글의 댓글 목록 조회 (삭제되지 않은 것만)
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    // 게시글의 댓글 수 조회 (삭제되지 않은 것만)
    long countByPostIdAndIsDeletedFalse(Long postId);

    // 댓글이 존재하고 삭제되지 않았는지 확인
    boolean existsByIdAndIsDeletedFalse(Long commentId);

    // 게시글의 최근 댓글 N개 조회
}