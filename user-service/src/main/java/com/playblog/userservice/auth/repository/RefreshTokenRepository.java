package com.playblog.userservice.auth.repository;

import com.playblog.userservice.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  long deleteByEmailIdAndDeviceId(String emailId, String deviceId);
  Optional<RefreshToken> findByEmailIdAndDeviceId(String emailId, String deviceId);
  Optional<RefreshToken> findByToken(String refreshToken);
}
