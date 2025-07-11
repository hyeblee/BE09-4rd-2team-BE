package com.playblog.blogservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyAddedToMeNeighborDto {
    private Long Id;
    private String nickname;
    private String profileIntro;
    //    private LocalDate createdAt;
    protected LocalDate requestedAt;
    private String status;
    private boolean isMutual;
}
