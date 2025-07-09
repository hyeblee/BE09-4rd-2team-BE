package com.playblog.userservice.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String emailId;

  @Column(nullable = false)
  private String deviceId; // 멀티 로그인을 위한 deviceId

  @Column(nullable = false, unique = true, length = 1000)
  private String token;

  @Column(nullable = false)
  private Date expiryDate;

  public void updateToken(String newToken, Date newExpiry) {
    this.token = newToken;
    this.expiryDate = newExpiry;
  }

}

