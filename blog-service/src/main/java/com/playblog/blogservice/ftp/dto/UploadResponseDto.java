package com.playblog.blogservice.ftp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDto {
    private boolean success;    // 성공 여부
    private List<String> imageUrls;     // 원본 파일 URL
    private String thumbnailImageUrl;    // 썸네일 URL
    private String message;     // 메시지
}

