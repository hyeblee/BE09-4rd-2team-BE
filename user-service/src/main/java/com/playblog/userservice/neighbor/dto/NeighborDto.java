package com.playblog.userservice.neighbor.dto;


import com.playblog.userservice.neighbor.Entity.Neighbor;
import com.playblog.userservice.neighbor.Entity.NeighborStatus;
import com.playblog.userservice.neighbor.Entity.User;
import com.playblog.userservice.neighbor.Entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.playblog.userservice.neighbor.Entity.NeighborStatus.ACCEPTED;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NeighborDto {

    private Long id;

    private Long fromUserInfo;

    private Long toUserInfo;

    LocalDate followedAt;

    LocalDate requestedAt;

    private boolean isMutual;

    private NeighborStatus status;
    public static NeighborDto from(Neighbor neighbor, Neighbor reverse) {
        boolean isMutual = neighbor.getStatus() == ACCEPTED &&
                reverse != null &&
                reverse.getStatus() == ACCEPTED;

        return NeighborDto.builder()
                .id(neighbor.getId())
                .fromUserInfo(neighbor.getFromUserInfo().getId())
                .toUserInfo(neighbor.getToUserInfo().getId())
                .followedAt(neighbor.getFollowedAt())
                .requestedAt(neighbor.getRequestedAt())
                .status(neighbor.getStatus())
                .isMutual(isMutual)
                .build();
    }

}
