package com.playblog.userservice.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.playblog.userservice.common.exception.DuplicateEmailException;
import com.playblog.userservice.user.dto.UserRegisterRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void 회원가입하면_비밀번호가_암호화되어_저장된다() {
    // given
    String rawPassword = "password123";
    UserRegisterRequestDto dto = new UserRegisterRequestDto("newuser@example.com", rawPassword);

    // when
    userService.registerUser(dto);

    // then
    User savedUser = userRepository.findByEmailId(dto.getEmailId())
        .orElseThrow(() -> new AssertionError("User not saved"));

    assertThat(savedUser.getEmailId()).isEqualTo(dto.getEmailId());
    // 암호가 평문이 아니고, 인코딩된 형태인지 확인
    assertThat(passwordEncoder.matches(rawPassword, savedUser.getPassword())).isTrue();
  }

  @Test
  void 이메일중복이면_회원가입시_예외를_던진다() {
    UserRegisterRequestDto dto = new UserRegisterRequestDto("tests", "password123");

    // when & then
    assertThrows(DuplicateEmailException.class, () -> userService.registerUser(dto));
  }


}
