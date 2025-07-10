package com.playblog.blogservice.search.entity;

import com.playblog.blogservice.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "test_user_info")
@Getter
public class TestUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // User를 참조하는 외래키 컬럼
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)  // 외래키 관계 필드에만 적용
    private TestUser user;

    String blogTitle;
    String nickname;
    String blogId;
    String profileIntro; // 프로필 소개글
    String profileImageUrl; // 프로필 이미지 URL
}
