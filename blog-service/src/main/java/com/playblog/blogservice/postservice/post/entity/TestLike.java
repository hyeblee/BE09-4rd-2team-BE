package com.playblog.blogservice.postservice.post.entity;

import com.playblog.blogservice.user.User;
import jakarta.persistence.*;


@Entity
@Table(name = "test_post_likes")
public class TestLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK, AUTO_INCREMENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

// + createdAt 넣어도 좋음
}
