package com.playblog.userservice.neighbor.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Neighbor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="blodId")
    private UserInfo fromUserInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_user_info_id")
    private UserInfo toUserInfo;

    LocalDateTime followedAt;

    LocalDateTime requestedAt;

    private boolean isMutual;
}
