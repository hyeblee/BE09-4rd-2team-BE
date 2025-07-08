package com.playblog.blogservice.common.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
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
