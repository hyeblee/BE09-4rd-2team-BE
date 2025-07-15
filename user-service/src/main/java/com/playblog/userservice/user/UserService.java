package com.playblog.userservice.user;

import com.playblog.userservice.common.exception.DuplicateEmailException;
import com.playblog.userservice.user.dto.UserRegisterRequestDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public boolean isEmailDuplicate(String emailId) {
    return userRepository.findByEmailId(emailId).isPresent();
  }


  @Transactional
  public Long registerUser(UserRegisterRequestDto requestDto) {
    System.out.println(requestDto);
    if (isEmailDuplicate(requestDto.getEmailId())) {
      throw new DuplicateEmailException();
    }
    User user = requestDto.toEntity();

    String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

    user.setPassword(encodedPassword);  // 암호화된 비밀번호 세팅
    userRepository.save(user);
    return user.getId();
  }
}
