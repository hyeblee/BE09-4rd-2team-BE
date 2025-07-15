package com.playblog.blogservice.userInfo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequest {
  private String blogTitle;
  private String nickname;
  private String blogId;
  private String profileIntro;
  private String profileImgUrl;
}
