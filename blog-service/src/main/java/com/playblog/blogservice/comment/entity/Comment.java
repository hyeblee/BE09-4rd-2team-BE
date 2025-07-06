package com.playblog.blogservice.comment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 기본 키, 자동 증가

    @Column(name = "post_id", nullable = false)
    private Long postId; // 게시글 id

    @Column(name = "author_id", nullable = false)
    private Long authorId; // 작성자 id

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret = false;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Builder
    public Comment(Long postId, Long authorId, String content, Boolean isSecret) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.isSecret = isSecret != null ? isSecret : false; // null 체크
        this.isDeleted = false; // 생성시 기본값
        this.likeCount = 0L; // 생성시 기본값
    }

    public void updateContent(String content) {
        this.content = content; // 댓글 수정
    }

    public void markAsDeleted() {
        this.isDeleted = true; // 소프트 삭제(실제 삭제 x)
    }

    public void incrementLikeCount() {
        this.likeCount++; // 공감 수 증가
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--; // 공감 수 감소 (0 미만 방지)
        }
    }
}
