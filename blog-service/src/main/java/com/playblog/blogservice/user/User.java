package com.playblog.blogservice.user;


import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")  // 여기서 테이블명 명시
public class User {

  // FK
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_info_id")
  private UserInfo userInfo;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

}
