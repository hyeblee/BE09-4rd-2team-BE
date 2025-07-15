package com.playblog.blogservice.comment.entity;

import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity(name = "BlogComment" )
@Table(name = "comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 댓글 기본 키, 자동 증가

    @ManyToOne(fetch = FetchType.LAZY) //하나의 게시물의 여러개의 댓글 작성 가능
    @JoinColumn(name = "post_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post; // 게시글 id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_secret", nullable = false)
    private Boolean isSecret = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Long likeCount = 0L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comment(Post post, User author, String content, Boolean isSecret) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.isSecret = isSecret != null ? isSecret : false; // null 체크
        this.isDeleted = false; // 생성시 기본값
        this.likeCount = 0L; // 생성시 기본값
    }

    public void updateContent(String content, Boolean isSecret) {
        this.content = content; // 댓글 수정
        if (isSecret != null) {
            this.isSecret = isSecret;
        }
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

    public Long getAuthorId() {
        return author != null ? author.getId() : null;
    }
}