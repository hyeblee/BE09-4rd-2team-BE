package com.playblog.blogservice.neighbor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMutualNeighborDto {
    private String blogId;
    protected LocalDate requestedAt;
}
