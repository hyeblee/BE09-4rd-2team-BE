package com.playblog.blogservice.user;

import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")  // 여기서 테이블명 명시
public class User {

//  @OneToOne(fetch = FetchType.LAZY)
  /*LAZY 로 하면 userInfo는 프록시 객체로 남고,
  userInfo가 실제 DB에서 로딩되려면 트랜잭션이 열려 있어야 합니다.
  그런데 Service나 Controller에서 트랜잭션이 닫힌 뒤에 user.getUserInfo()를 호출하면
  → 프록시 초기화 실패 → NPE 또는 LazyInitializationException 발생.*/
  @JoinColumn(name = "user_info_id")
  @OneToOne(mappedBy = "user")
  private UserInfo userInfo;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String emailId;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

}
