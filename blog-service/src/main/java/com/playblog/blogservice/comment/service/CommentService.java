package com.playblog.blogservice.comment.service;

import com.playblog.blogservice.comment.dto.*;
import com.playblog.blogservice.comment.entity.Comment;
import com.playblog.blogservice.comment.repository.CommentLikeRepository;
import com.playblog.blogservice.comment.repository.CommentRepository;
import com.playblog.blogservice.common.repository.UserRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfoService;
import com.playblog.blogservice.userInfo.dto.UserInfoResponse;
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
    private final UserRepository userRepository;
    private final UserInfoService userInfoService;
    private final CommentLikeRepository commentLikeRepository;

    /**
     * 댓글 작성
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, Long userId) {
        // DB에서 실제 User가 존재하는지 확인
        User author = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .postId(postId)
                .author(author)
                .content(request.getContent())
                .isSecret(request.getIsSecret())
                .build();

        Comment savedComment = commentRepository.save(comment);

        return convertToCommentResponse(savedComment, userId, false);
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

        comment.updateContent(request.getContent(), request.getIsSecret());

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

        // 댓글 소프트 삭제
        comment.markAsDeleted();

        // 댓글 공감 완전한 삭제
        commentLikeRepository.deleteByCommentId(commentId);
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
     * 실제 사용자 정보 조회(안전한 예외처리 포함)
     */
    private CommentResponse.AuthorDto getUserAuthorInfo(Long authorId) {
        try {
            UserInfoResponse userInfo = userInfoService.getUserInfo(authorId);
            return new CommentResponse.AuthorDto(
                    authorId,
                    userInfo.getNickname() != null ? userInfo.getNickname() : "사용자",
                    "https://api.pravatar.cc/150?img=" + (authorId % 50) // ← 임시 이미지
            );
        } catch (Exception e) {
            // 탈퇴한 사용자인 경우
            return new CommentResponse.AuthorDto(
                    authorId,
                    "탈퇴한 사용자",
                    "https://api.pravatar.cc/150?img=" + (authorId % 50)
            );
        }
    }

    /**
     * 프로필 이미지 URL 처리 (파일 서버 문제 대응)
     */
//    private String getProfileImageUrl(String profileImageUrl, Long userId) {
//
//        if(profileImageUrl == null || profileImageUrl.isEmpty()) {
//            return "https://api.pravatar.cc/150?img=" + (userId % 50);
//        }
//
//        // TODO: 파일 서버 문제 해결되면 실제 URL 사용
//        // 현재는 파일 서버 이슈로 임시 이미지 사용
//        return "https://api.pravatar.cc/150?img=" + (userId % 50);
//    }

    /**
     * 권한 체크 후
     * Comment Entity -> CommentResponse 변환
     */
    private CommentResponse convertToCommentResponseWithContent(Comment comment, Long requestUserId, boolean isLiked, String content) {

        CommentResponse.AuthorDto author = getUserAuthorInfo(comment.getAuthorId());

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