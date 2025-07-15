package com.playblog.blogservice.ftp.controller;

import com.playblog.blogservice.ftp.dto.UploadResponseDto;
import com.playblog.blogservice.ftp.service.FtpUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

import java.io.UncheckedIOException;

@Slf4j
//@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/ftp")
@RequiredArgsConstructor
public class FtpUploadController {
    private final FtpUploadService uploadService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> upload(@RequestParam MultipartFile file) {
        try {
            UploadResponseDto dto = uploadService.upload(file);
            return ResponseEntity.ok(dto);
        } catch (UncheckedIOException e) {
            // 여기서도 로깅
            log.error("컨트롤러에서 FTP 업로드 실패", e);
            UploadResponseDto dto = new UploadResponseDto(
                    false, null, null, "서버 오류로 업로드에 실패했습니다."
            );
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(dto);
        }
    }

}
