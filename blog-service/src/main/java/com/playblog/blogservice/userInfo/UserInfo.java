package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.user.User;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
public class UserInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  // User를 참조하는 외래키 컬럼
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)  // 외래키 관계 필드에만 적용
  private User user;

  String blogTitle; // 블로그명
  String nickname; // 별명
  String blogId; // 블로그 아이디 (= 로그인 이메일 아이디)
  String profileIntro; // 프로필 소개글
  String profileImageUrl; // 프로필 이미지 URL

}
