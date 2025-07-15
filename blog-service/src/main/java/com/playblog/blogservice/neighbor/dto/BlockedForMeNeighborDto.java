package com.playblog.blogservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class BlockedForMeNeighborDto {
    private Long id;
    private String nickname;
    private String blogId;
}
