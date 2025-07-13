package com.playblog.userservice.user.dto;

import com.playblog.userservice.user.Role;
import com.playblog.userservice.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterRequestDto {
    private String emailId;
    private String password;

    public User toEntity() {
      return User.builder()
          .emailId(emailId)
          .password(password)
          .role(Role.USER)
          .build();
    }
}
