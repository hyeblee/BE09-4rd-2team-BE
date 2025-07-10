package com.playblog.blogservice.search.entity;

import com.playblog.blogservice.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name="test_post_likes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private TestPost testPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
