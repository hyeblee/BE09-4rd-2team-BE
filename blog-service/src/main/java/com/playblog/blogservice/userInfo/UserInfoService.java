package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.user.User;
import com.playblog.blogservice.user.UserRepository;
import com.playblog.blogservice.userInfo.dto.UserInfoRequest;
import com.playblog.blogservice.userInfo.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
// 사용자 정보 생성, 수정, 삭제는 나와 Admin만 가능하게 제한
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;
  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  public UserInfoResponse getUserInfo(Long userId) {
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: id=" + userId));
    return new UserInfoResponse(userInfo);
  }

  @Transactional
  public UserInfoResponse createUserInfo(Long userId) {
    if (userInfoRepository.existsById(userId)) {
      return new UserInfoResponse(userInfoRepository.findById(userId).get());
    }
    UserInfo info = new UserInfo();
    User user = userRepository.findById(userId).orElseThrow();
    String emailId = user.getEmailId();
    info.setUser(user);
    info.setBlogTitle(emailId+"님의블로그");
    info.setNickname("");
    info.setBlogId(emailId);
    info.setProfileIntro("");
    info.setProfileImageUrl("");
    return new UserInfoResponse(userInfoRepository.save(info));
  }

  @Transactional
  public UserInfoResponse updateUserInfo(Long userId, UserInfoRequest dto) {
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: id=" + userId));

    userInfo.setBlogTitle(dto.getBlogTitle());
    userInfo.setNickname(dto.getNickname());
    userInfo.setProfileIntro(dto.getProfileIntro());
    userInfo.setProfileImageUrl(dto.getProfileImgUrl());

    return new UserInfoResponse(userInfo);
  }

  public void deleteUserInfo(Long userId) {
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: id=" + userId));
    userInfoRepository.delete(userInfo);
  }
}
