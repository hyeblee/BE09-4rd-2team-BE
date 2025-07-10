package com.playblog.blogservice.ftp.controller;

import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.ftp.config.FtpProperties;
import com.playblog.blogservice.ftp.dto.UploadResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ftp")
public class FtpUploadController {

    private final FtpProperties ftpProperties;

    @Autowired
    public FtpUploadController(FtpProperties ftpProperties) {
        this.ftpProperties = ftpProperties;
    }

    /**
     * 단일 파일 업로드 API
     * POST /ftp/upload
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드 결과 JSON (원본 이미지 URL 리스트, 썸네일 이미지 URL)
     */
    @PostMapping("/upload")
    public ResponseEntity<UploadResponseDto> upload(@RequestParam("file") MultipartFile file) throws IOException {

        // 파일 업로더 설정에서 프로퍼티 가져오기
        String savedFileName = FtpUploader.uploadFile(
                ftpProperties.getServer(),
                ftpProperties.getPort(),
                ftpProperties.getUser(),
                ftpProperties.getPass(),
                ftpProperties.getRemoteDir(),
                file
        );

        // null이 아닐 때 해당 이름의 url을 프로퍼티에 가져와서 변수에 저장
        if (savedFileName != null) {
            String savedImageUrls = ftpProperties.getBaseUrl() + "/" + savedFileName;
            String savedThumbnailUrl = ftpProperties.getBaseUrl() + "/thumb/" + savedFileName;

            // 업로드에 성공한 Dto와 메세지를 저장
            UploadResponseDto responseDto = new UploadResponseDto(
                    true,
                    List.of(savedImageUrls),
                    savedThumbnailUrl,
                    "업로드 성공"
            );

            // 반환
            return ResponseEntity.ok(responseDto);
        } else {
            UploadResponseDto responseDto = new UploadResponseDto(
                    false,
                    null,
                    null,
                    "업로드 실패"
            );

            return ResponseEntity.status(500).body(responseDto);
        }
    }
}
