package com.playblog.blogservice.comment.service;

import com.playblog.blogservice.comment.dto.*;
import com.playblog.blogservice.comment.entity.Comment;
import com.playblog.blogservice.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeService commentLikeService;

    /**
     * 댓글 작성
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long authorId) {
        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .content(request.getContent())
                .isSecret(request.getIsSecret())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return convertToCommentResponse(savedComment, authorId, false);
    }

    /**
     * 댓글 목록 조회
     */
    public CommentsResponse getCommentsByPostId(Long postId, Long requestUserId, Long postAuthorId) {
        // 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);
        Long totalCount = commentRepository.countByPostIdAndIsDeletedFalse(postId);

        // Comment -> CommentResponse 변환
        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> {
                    // 권한에 따른 댓글 내용
                    String displayContent = getDisplayContent(comment, requestUserId, postAuthorId);

                    // 공감 여부 확인
                    boolean isLiked = commentLikeService.isCommentLikedByUser(comment.getId(), requestUserId);

                    return convertToCommentResponseWithContent(comment, requestUserId, isLiked, displayContent);
                })
                .collect(Collectors.toList());

        return new CommentsResponse(commentResponses, totalCount);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request, Long requestUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 본인만 수정 가능
        if (!comment.getAuthorId().equals(requestUserId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(request.getContent());

        // 공감 여부 확인
        boolean isLiked = commentLikeService.isCommentLikedByUser(commentId, requestUserId);

        return convertToCommentResponse(comment, requestUserId, isLiked);
    }

    /**
     * 댓글 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteComment(Long commentId, Long requestUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 본인만 삭제 가능
        if (!comment.getAuthorId().equals(requestUserId)) {
            throw new RuntimeException("댓글 삭제 권한이 없습니다.");
        }

        comment.markAsDeleted();
    }

    /**
     * 댓글 수 조회 (Post Service용)
     */
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostIdAndIsDeletedFalse(postId);
    }

    /**
     * 댓글 목록 조회 시 비밀댓글 권한 체크 (기존 비즈니스 로직)
     */
    public boolean canViewContent(Comment comment, Long requestUserId, Long postAuthorId) {
        // 삭제된 댓글은 볼 수 없음
        if (comment.getIsDeleted()) {
            return false;
        }

        // 비밀댓글이 아니면 누구나 볼 수 있음
        if (!comment.getIsSecret()) {
            return true;
        }

        // 댓글 작성자 본인
        if (comment.getAuthorId().equals(requestUserId)) {
            return true;
        }

        // 게시글 작성자
        if (postAuthorId != null && postAuthorId.equals(requestUserId)) {
            return true;
        }

        return false;
    }

    /**
     * 댓글 내용 표시 (권한에 따라) - 기존 로직
     */
    public String getDisplayContent(Comment comment, Long requestUserId, Long postAuthorId) {
        if (canViewContent(comment, requestUserId, postAuthorId)) {
            return comment.getContent();
        }
        return "비밀댓글입니다";
    }

    /**
     * 권한 체크 후
     * Comment Entity -> CommentResponse 변환
     */
    private CommentResponse convertToCommentResponseWithContent(Comment comment, Long requestUserId, boolean isLiked, String content) {
        CommentResponse.AuthorDto author = new CommentResponse.AuthorDto(
                comment.getAuthorId(),
                "임시닉네임",  // TODO: 실제 닉네임
                "임시프로필URL"  // TODO: 실제 프로필 이미지
        );

        return new CommentResponse(
                comment.getId(),
                author,
                content, // 권한 체크된 내용
                comment.getIsSecret(),
                comment.getLikeCount(),
                isLiked,
                comment.getCreatedAt()
        );
    }

    /**
     * 작성자 본인이 수정하는 거라 권한 체크 x
     * Comment Entity -> CommentResponse 변환
     */
    private CommentResponse convertToCommentResponse(Comment comment, Long requestUserId, boolean isLiked) {
        return convertToCommentResponseWithContent(comment, requestUserId, isLiked, comment.getContent());
    }
}