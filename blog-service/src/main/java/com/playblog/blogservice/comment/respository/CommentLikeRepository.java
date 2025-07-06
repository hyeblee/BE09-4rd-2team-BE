package com.playblog.blogservice.comment.respository;

import com.playblog.blogservice.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 특정 사용자가 특정 댓글에 공감했는지 확인
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    // 특정 사용자가 특정 댓글에 공감했는지 여부
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    // 특정 댓글의 공감 수
    long countByCommentId(Long commentId);

    // 특정 댓글에 공감한 사용자 목록
    List<CommentLike> findByCommentIdOrderByCreatedAtDesc(Long commentId);

    // 특정 사용자가 공감한 댓글 목록
    List<CommentLike> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 특정 사용자의 특정 댓글 공감 삭제
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
