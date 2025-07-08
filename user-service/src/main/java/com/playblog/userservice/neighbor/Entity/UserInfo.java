package com.playblog.userservice.neighbor.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String blogTitle;
    private String profileIntro;
    private String blogId;     // 블로그 주소
    private String nickname;   // 블로그 별명
    private String profileImageUrl;

    // 블로그 → 유저 (1:1)
    @OneToOne
    @JoinColumn(name="id")
    private User user;

    // 내가 추가한 이웃들 (팔로잉)
    @OneToMany(mappedBy = "fromUserInfo", cascade = CascadeType.ALL)
    private List<Neighbor> following = new ArrayList<>();

    // 나를 이웃으로 추가한 사람들 (팔로워)
    @OneToMany(mappedBy = "toUserInfo", cascade = CascadeType.ALL)
    private List<Neighbor> followers = new ArrayList<>();
}
