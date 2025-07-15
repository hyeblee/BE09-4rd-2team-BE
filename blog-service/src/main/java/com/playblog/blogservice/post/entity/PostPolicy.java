package com.playblog.blogservice.post.entity;

import jakarta.persistence.*;
import lombok.*;

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
}
