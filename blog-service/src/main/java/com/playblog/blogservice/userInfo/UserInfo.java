package com.playblog.blogservice.userInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String blogTitle;
  String nickname;
  String blogId;
  String profileIntro; // 프로필 소개글


/*  @OneToMany(mappedBy = "fromUserInfo")
  private List<Neighbor> followingList;

  @OneToMany(mappedBy = "toUserInfo")
  private List<Neighbor> followerList;*/

}
