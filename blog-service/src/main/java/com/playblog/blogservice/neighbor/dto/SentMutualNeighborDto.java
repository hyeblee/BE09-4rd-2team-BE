package com.playblog.blogservice.neighbor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class SentMutualNeighborDto{
    private Long id;
    private String nickname;
    protected LocalDate requestedAt;
}
