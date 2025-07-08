package com.playblog.userservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyAddedToMeNeighborDto {
    private String blogTitle;
    private String nickname;
    protected LocalDateTime followedAt;
}
