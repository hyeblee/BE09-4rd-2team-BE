package com.playblog.blogservice.neighbor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginUserDto {
    private Long userId;
    private String nickname;
    private String profileImageUrl;
}
