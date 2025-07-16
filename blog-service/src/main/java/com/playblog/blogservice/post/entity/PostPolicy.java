package com.playblog.blogservice.post.entity;

import jakarta.persistence.*;
import lombok.*;

import java.beans.Visibility;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 이게 실질적인 문제였음
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "post_id")
    private Post post;

    private Boolean allowComment; // 댓글 허용
    private Boolean allowLike;    // 좋아요 허용
    private Boolean allowSearch;  // 검색 허용

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;


    /* 공개 설정 편의상 중복 설정 (수정필요) */
    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private PostVisibility visibility; // 공개 설정 (PUBLIC, PRIVATE)

//     중복 제거를 위한 헬퍼 메서드
//    public PostVisibility getVisibility() {
//        return post != null ? post.getVisibility() : null;
//    }

    // 테스트용 기본 공개 정책 반환 메서드
    public static PostPolicy defaultPublicPolicy(Post post) {
// DTO 변환 시에는 post.getVisibility()로 접근

        return PostPolicy.builder()
                .post(post)
                .visibility(post.getVisibility())  // 복사//
                .allowComment(true)
                .allowLike(true)
                .allowSearch(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void update(Boolean allowComment, Boolean allowLike, Boolean allowSearch) {
        this.allowComment = allowComment;
        this.allowLike = allowLike;
        this.allowSearch = allowSearch;
    }

}
