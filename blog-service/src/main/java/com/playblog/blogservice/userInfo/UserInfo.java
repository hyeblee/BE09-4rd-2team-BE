package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

  String blogTitle;
  String nickname;
  String blogId;
  String profileIntro; // 프로필 소개글
  String profileImageUrl; // 프로필 이미지 URL

  // Neighbor은 단방향 참조 예정..
/*  @OneToMany(mappedBy = "fromUserInfo")
  private List<Neighbor> followingList;

  @OneToMany(mappedBy = "toUserInfo")
  private List<Neighbor> followerList;*/

}
