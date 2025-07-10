package com.playblog.userservice.user;

import com.playblog.userservice.common.exception.DuplicateEmailException;
import com.playblog.userservice.common.response.EmailCheckResponse;
import com.playblog.userservice.user.dto.UserRegisterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/check-emailId")
  public ResponseEntity<EmailCheckResponse> checkEmailDuplicate(@RequestParam("emailId") String emailId) {
    boolean isDuplicate = userService.isEmailDuplicate(emailId);
    EmailCheckResponse response = new EmailCheckResponse("OK", isDuplicate);
    return ResponseEntity.ok(response);
  }


  @PostMapping
  public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
    try {
      userService.registerUser(requestDto);
      return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    } catch (DuplicateEmailException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이메일입니다.");
    }
  }

}
