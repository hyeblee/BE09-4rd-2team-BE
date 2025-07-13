package com.playblog.blogservice.userInfo.dto;

import com.playblog.blogservice.userInfo.UserInfo;
import lombok.Getter;

@Getter
public class UserInfoResponse {
  private Long id;
  private String blogTitle;
  private String nickname;
  private String blogId;
  private String profileIntro;

  public UserInfoResponse(UserInfo userInfo) {
    this.id = userInfo.getId();
    this.blogTitle = userInfo.getBlogTitle();
    this.nickname = userInfo.getNickname();
    this.blogId = userInfo.getBlogId();
    this.profileIntro = userInfo.getProfileIntro();
  }



}
