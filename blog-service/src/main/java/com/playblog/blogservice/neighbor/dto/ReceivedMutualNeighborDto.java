package com.playblog.blogservice.neighbor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMutualNeighborDto {
    private Long id;
    private String nickname;
    protected LocalDate requestedAt;
}
