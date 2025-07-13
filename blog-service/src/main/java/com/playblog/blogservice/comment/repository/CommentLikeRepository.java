package com.playblog.blogservice.comment.repository;

import com.playblog.blogservice.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 공감 확인 - CommentLike 객체 전체를 반환
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    // 공감 여부 확인 - true/false만 반환
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    // 댓글의 공감 수
    long countByCommentId(Long commentId);

    // 공감한 사용자 목록
    List<CommentLike> findByCommentIdOrderByCreatedAtDesc(Long commentId);

    // 공감한 댓글 목록
    List<CommentLike> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 사용자 1명의 댓글 공감 삭제
    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    // 해당 댓글의 모든 공감 삭제
    void deleteByCommentId(Long commentId);
}