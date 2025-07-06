package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.userInfo.dto.UserInfoRequest;
import com.playblog.blogservice.userInfo.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserInfoService {

  private final UserInfoRepository userInfoRepository;

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
    info.setId(userId);
    info.setNickname("");
    info.setBlogTitle("");
    info.setBlogId("");
    info.setProfileIntro("");
    return new UserInfoResponse(userInfoRepository.save(info));
  }

  @Transactional
  public UserInfoResponse updateUserInfo(Long userId, UserInfoRequest dto) {
    UserInfo userInfo = userInfoRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 유저 없음: id=" + userId));

    userInfo.setNickname(dto.getNickname());
    userInfo.setBlogTitle(dto.getBlogTitle());
    userInfo.setBlogId(dto.getBlogId());
    userInfo.setProfileIntro(dto.getProfileIntro());

    return new UserInfoResponse(userInfo);
  }
}
