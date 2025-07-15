package com.playblog.userservice.user;

import com.playblog.userservice.common.exception.DuplicateEmailException;
import com.playblog.userservice.common.response.EmailCheckResponse;
import com.playblog.userservice.user.dto.UserRegisterRequestDto;
import java.util.HashMap;
import java.util.Map;
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


  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegisterRequestDto requestDto) {
    try {
      Long registeredId = userService.registerUser(requestDto);
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("message", "회원가입 성공");
      responseBody.put("userId", registeredId);

      return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    } catch (DuplicateEmailException e) {
      Map<String, Object> responseBody = new HashMap<>();
      responseBody.put("message", "이미 존재하는 이메일입니다.");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
    }
  }

}
