package com.playblog.blogservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyAddedForLoginModalNeighborDto {
    private Long UserId;
    private String blogTitle;
    private String profileImageUrl;
}
