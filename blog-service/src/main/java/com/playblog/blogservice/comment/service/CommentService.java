package com.playblog.blogservice.comment.service;

import com.playblog.blogservice.comment.entity.Comment;
import com.playblog.blogservice.comment.repository.CommentLikeRepository;
import com.playblog.blogservice.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentLikeService commentLikeService;

    // 1. 댓글 작성
    @Transactional
    public Comment createComment(Long postId, Long authorId, String content, Boolean isSecret) {
        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .content(content)
                .isSecret(isSecret)
                .build();

        return commentRepository.save(comment);
    }

    // 2. 댓글 목록 조회
    public List<Map<String, Object>> getCommentsWithDetails(Long postId, Long requestUserId, Long postAuthorId) {
        // 1. 댓글 목록 조회
        List<Comment> comments = commentRepository.findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(postId);

        // 2. 댓글 목록을 Map 형태로 전환
        return comments.stream().map(comment -> {
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("commentId", comment.getId());

                    // 작성자 정보
                    commentMap.put("author", Map.of(
                            "id", comment.getAuthorId(),
                            "nickname", "임시닉네임",
                            "profileImage", "임시프로필URL"
                    ));

                    // 댓글 내용(권한에 따라)
                    commentMap.put("content", getDisplayContent(comment, requestUserId, postAuthorId));
                    commentMap.put("isSecret", comment.getIsSecret());

                    // 공감 정보
                    commentMap.put("likeCount", comment.getLikeCount());
                    commentMap.put("isLiked", commentLikeService.isCommentLikedByUser(comment.getId(), requestUserId));

                    commentMap.put("createdAt", comment.getCreatedAt());

                    return commentMap;
                })
                .collect(Collectors.toList());
    }

    // 3. 댓글 수정
    @Transactional
    public Comment updateComment(Long commentId, String content, Long requestUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 작성자 본인만 수정 가능
        if (!comment.getAuthorId().equals(requestUserId)) {
            throw new RuntimeException("댓글 수정 권한이 없습니다.");
        }

        comment.updateContent(content);
        return comment;
    }

    // 4. 댓글 삭제 (소프트 삭제)
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

    // 5. 댓글 목록 조회 시 비밀댓글 권한 체크 (비즈니스 로직)
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

    // 6. 댓글 내용 표시 (권한에 따라)
    public String getDisplayContent(Comment comment, Long requestUserId, Long postAuthorId) {
        if (canViewContent(comment, requestUserId, postAuthorId)) {
            return comment.getContent();
        }
        return "비밀댓글입니다";
    }


}
