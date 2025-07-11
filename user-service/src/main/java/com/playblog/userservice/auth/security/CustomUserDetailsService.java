package com.playblog.userservice.auth.security;

import com.playblog.userservice.user.User;
import com.playblog.userservice.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  // username 으로 DB에서 사용자 조회 후 CustomUserDetails 반환
  @Override
  public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
    User user = userRepository.findByEmailId(emailId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with emailId: " + emailId));

    return new CustomUserDetails(user);
  }
}
