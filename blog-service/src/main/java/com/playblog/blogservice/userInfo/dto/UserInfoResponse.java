package com.playblog.blogservice.userInfo.dto;

import com.playblog.blogservice.userInfo.UserInfo;
import lombok.Getter;

@Getter
public class UserInfoResponse {
  private Long userId;
  private String blogTitle;
  private String nickname;
  private String blogId;
  private String profileIntro;
  private String profileImgUrl;

  public UserInfoResponse(UserInfo userInfo) {
    this.userId = userInfo.getUser().getId();
    this.blogTitle = userInfo.getBlogTitle();
    this.nickname = userInfo.getNickname();
    this.blogId = userInfo.getBlogId();
    this.profileIntro = userInfo.getProfileIntro();
    this.profileImgUrl = userInfo.getProfileImageUrl();
  }



}
