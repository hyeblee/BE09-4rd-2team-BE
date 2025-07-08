package com.playblog.userservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyAddedForMeNeighborDto{
    private String blogTitle;
    private String nickname;
//    private LocalDateTime createdAt;
    protected LocalDateTime followedAt;
}
