package com.playblog.blogservice.comment.respository;

import com.playblog.blogservice.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 특정 게시글의 댓글 목록 조회 (삭제되지 않은 것만)
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);

    // 특정 게시글의 댓글 수 조회 (삭제되지 않은 것만)
    long countByPostIdAndIsDeletedFalse(Long postId);

    // 특정 사용자가 작성한 댓글 목록
    List<Comment> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);

    // 특정 댓글이 존재하고 삭제되지 않았는지 확인
    boolean existsByIdAndIsDeletedFalse(Long commentId);

    // 특정 게시글의 최근 댓글 N개 조회
    @Query("SELECT c FROM Comment c WHERE c.postId = :postId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByPostId(@Param("postId") Long postId);
}
