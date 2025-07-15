package com.playblog.blogservice.userInfo.dto;

import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import lombok.Getter;

@Getter
public class UserInfoResponse {
  private User user;
  private String blogTitle;
  private String nickname;
  private String blogId;
  private String profileIntro;
  private String profileImgeUrl;

  public UserInfoResponse(UserInfo userInfo) {
    this.user = userInfo.getUser();
    this.blogTitle = userInfo.getBlogTitle();
    this.nickname = userInfo.getNickname();
    this.blogId = userInfo.getBlogId();
    this.profileIntro = userInfo.getProfileIntro();
    this.profileImgeUrl = userInfo.getProfileImageUrl();
  }



}
