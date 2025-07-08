package com.playblog.blogservice.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="test_user_info")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "blog_title", nullable = false)
    private String blogTitle;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "blog_id", unique = true, nullable = false)
    private String blogId;

    @Column(name = "profile_intro", length = 500)
    private String profileIntro;

    @Column(name = "profile_image_url")
    private String profileImageUrl;
}
