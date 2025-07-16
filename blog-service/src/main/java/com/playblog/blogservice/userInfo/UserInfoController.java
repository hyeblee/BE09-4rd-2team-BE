package com.playblog.blogservice.userInfo;

import com.playblog.blogservice.userInfo.dto.UserInfoRequest;
import com.playblog.blogservice.userInfo.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-info")
@RequiredArgsConstructor
public class UserInfoController {

  private final UserInfoService userInfoService;

  // 생성
  @PostMapping("/{userId}")
  public UserInfoResponse createUserInfo(@PathVariable Long userId) {
    return userInfoService.createUserInfo(userId);
  }

  // 조회
  @GetMapping("/{userId}")
  public UserInfoResponse getUserInfo(@PathVariable Long userId) {
    return userInfoService.getUserInfo(userId);
  }


  // 수정
  @PatchMapping("/{userId}")
  public UserInfoResponse updateUserInfo(@PathVariable Long userId,
      @RequestBody UserInfoRequest request) {
    return userInfoService.updateUserInfo(userId, request);
  }

  // User 삭제 시, UserInfo 삭제...(?)
  // 삭제
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUserInfo(@PathVariable Long userId) {
    userInfoService.deleteUserInfo(userId);
    return ResponseEntity.noContent().build(); // 204 No Content 반환
  }
}
