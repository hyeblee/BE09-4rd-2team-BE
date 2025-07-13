package com.playblog.blogservice.comment.service;

import com.playblog.blogservice.comment.entity.Comment;
import com.playblog.blogservice.comment.entity.CommentLike;
import com.playblog.blogservice.comment.repository.CommentLikeRepository;
import com.playblog.blogservice.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    // 댓글 공감/취소 토글
    @Transactional
    public boolean toggleCommentLike(Long commentId, Long userId) {
        // 1. 댓글 존재 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. "));

        // 2. 삭제된 댓글인지 확인
        if (comment.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 댓글에는 공감할 수 없습니다.");
        }

        // 3. 기존 공감 여부 확인
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(commentId, userId);

        // 이미 공감한 경우
        if (existingLike.isPresent()) {
            // 공감 취소
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
            comment.decrementLikeCount(); // 공감 수 감소
            return false; // 공감 취소
        } else {
            // 공감하지 않은 경우 버튼을 누르는 순간 새로운 CommentLike 생성 필요
            CommentLike commentLike = CommentLike.builder()
                    .commentId(commentId).userId(userId).build(); // 완전히 새로운 객체를 만듦.
            commentLikeRepository.save(commentLike); // db에 저장
            comment.incrementLikeCount(); // 댓글 공감 수 증가
            return true; // 공감 추가
        }
    }

    // 댓글 공감 수 조회
    @Transactional(readOnly = true)
    public long getCommentLikeCount(long commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    // 댓글 공감 여부 확인
    public boolean isCommentLikedByUser(Long commentId, Long userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }


}