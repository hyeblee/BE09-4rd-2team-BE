package com.playblog.blogservice.neighbor.dto;


import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Entity.NeighborStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.playblog.blogservice.neighbor.Entity.NeighborStatus.ACCEPTED;

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
