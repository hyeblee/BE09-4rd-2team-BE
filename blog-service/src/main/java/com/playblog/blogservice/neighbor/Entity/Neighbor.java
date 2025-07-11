package com.playblog.blogservice.neighbor.Entity;

import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Neighbor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="from_user_info_id")
    private UserInfo fromUserInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="to_user_info_id")
    private UserInfo toUserInfo;

    LocalDate followedAt;

    LocalDate requestedAt;

    @Enumerated(EnumType.STRING)
    private NeighborStatus status;

    public void setStatus(NeighborStatus neighborStatus) {
        this.status = neighborStatus;
    }

    public void setFollowedAt(LocalDate now) {

    }
}
