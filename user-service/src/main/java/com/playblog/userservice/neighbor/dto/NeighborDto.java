package com.playblog.userservice.neighbor.dto;


import com.playblog.userservice.neighbor.Entity.Neighbor;
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

    private String fromUserInfo;

    private String toUserInfo;

    LocalDateTime followedAt;

    LocalDateTime requestedAt;

    private boolean isMutual;

    public NeighborDto(String fromUserInfo,String toUserInfo,boolean isMutual){
        this.fromUserInfo = fromUserInfo;
        this.toUserInfo = toUserInfo;
        this.isMutual = isMutual;
    }

    public static NeighborDto fromWithoutLogin(Neighbor neighbor) {
        return new NeighborDto(
            neighbor.getFromUserInfo().getNickname(),
            neighbor.getToUserInfo().getNickname(),
            neighbor.isMutual()
        );
    }
}
