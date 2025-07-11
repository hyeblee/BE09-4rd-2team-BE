package com.playblog.blogservice.postservice.post.entity;

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

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId // Post의 PK 공유
    private Post post;

    private Boolean allowComment; // 댓글 허용
    private Boolean allowLike;    // 좋아요 허용
    private Boolean allowSearch;  // 검색 허용
}
