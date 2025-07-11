package com.playblog.userservice.neighbor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class SentMutualNeighborDto{
    private String blogId;
    protected LocalDate requestedAt;
}
