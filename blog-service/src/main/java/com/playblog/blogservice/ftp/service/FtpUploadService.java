package com.playblog.blogservice.ftp.service;

import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.ftp.common.config.FtpProperties;
import com.playblog.blogservice.ftp.dto.UploadResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;


/*
빈 파일 검증 → 400 응답
에디터용 원본 업로드 → editorDir 에 저장 → baseUrl + "/" + editorFileName 조합
썸네일 생성 & 업로드 → thumbDir 에 저장 → thumbBaseUrl + "/" + thumbFileName 조합
DTO 반환 → { success, List.of(editorUrl), thumbUrl, message }
이제 프론트엔드에서 이 API를 한 번 호출하면
editorUrl 은 에디터 내용에 <img> 로 바로 삽입
thumbUrl 은 상태에 보관해 뒀다가 발행 시 thumbnailUrl 필드로 전송
*/

@Slf4j
@Service
@RequiredArgsConstructor
public class FtpUploadService {
    private final FtpProperties ftpProps;

    public UploadResponseDto upload(MultipartFile file) {
        // 1) 빈 파일 검증
        if (file.isEmpty()) {
            return new UploadResponseDto(false, null, null, "파일이 선택되지 않았습니다.");
        }

        try {
            // 2) 에디터용 원본 업로드
            String editorName = FtpUploader.uploadFile(
                    ftpProps.getServer(), ftpProps.getPort(),
                    ftpProps.getUser(), ftpProps.getPass(),
                    ftpProps.getEditorDir(), file
            );
            String editorUrl = ftpProps.getBaseUrl() + "/" + editorName;

            // 3) 썸네일 생성 스트림 & 업로드
            InputStream thumbStream = createThumbnailStream(file.getInputStream());
            String thumbExt = getExtension(file.getOriginalFilename());
            String thumbName = FtpUploader.uploadStream(
                    ftpProps.getServer(), ftpProps.getPort(),
                    ftpProps.getUser(), ftpProps.getPass(),
                    ftpProps.getThumbDir(), thumbStream, thumbExt
            );
            String thumbUrl = ftpProps.getThumbBaseUrl() + "/" + thumbName;

            return new UploadResponseDto(true, List.of(editorUrl), thumbUrl, "업로드 성공");

        } catch (IOException e) {
            // 여기서 자세한 원인과 stacktrace를 찍습니다
            log.error("FTP 업로드 중 오류 발생: editorDir={}, thumbDir={}, fileName={}",
                    ftpProps.getEditorDir(),
                    ftpProps.getThumbDir(),
                    file.getOriginalFilename(), e);
            // 기존처럼 unchecked로 넘기거나, DTO로 실패를 반환할 수 있습니다
            throw new UncheckedIOException("FTP 업로드 실패", e);
        }
    }

    /* InpustStream을 통함 썸네일 반환
    /**
     * 입력 스트림을 리사이징하여 썸네일용 InputStream으로 반환합니다.
     */
    private InputStream createThumbnailStream(InputStream original) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Thumbnailator 라이브러리로 200px 너비 비율 유지 리사이징
        Thumbnails.of(original)
                .width(200)
                .keepAspectRatio(true)
                .toOutputStream(baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /* 확장자 추출 */
    /**
     * 파일명에서 확장자(예: ".png")만 추출합니다.
     */
    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        // lastIndexOf('.') 이후 문자 전부, 예: ".png", ".jpg", ".jpeg"…
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }

}
