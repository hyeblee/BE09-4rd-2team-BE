package com.playblog.userservice.neighbor.dto;


import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.NeighborStatus;
import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NeighborDto {

    private Long id;

    private Long fromUserInfo;

    private Long toUserInfo;

    LocalDateTime followedAt;

    LocalDateTime requestedAt;

    private NeighborStatus status;


    public static NeighborDto fromWithoutLogin(Neighbor neighbor) {
        return new NeighborDto(
            neighbor.getId(),
            neighbor.getFromUserInfo().getId(),
            neighbor.getToUserInfo().getId(),
            neighbor.getRequestedAt(),
            neighbor.getFollowedAt(),
            neighbor.getStatus()
        );
    }
}
