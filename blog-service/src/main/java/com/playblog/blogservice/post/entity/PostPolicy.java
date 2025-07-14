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
    private Long id; // Post의 PK와 같음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    private Boolean allowComment; // 댓글 허용
    private Boolean allowLike;    // 좋아요 허용
    private Boolean allowSearch;  // 검색 허용
}
