package com.playblog.userservice.user;

import com.playblog.userservice.common.response.EmailCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


}
