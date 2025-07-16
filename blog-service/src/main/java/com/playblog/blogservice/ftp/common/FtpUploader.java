package com.playblog.blogservice.ftp.common;

import com.playblog.blogservice.ftp.common.config.FtpProperties;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Configuration
public class FtpUploader {

    private final FtpProperties ftpProperties;

    public FtpUploader(FtpProperties ftpProperties) {
        this.ftpProperties = ftpProperties;
    }


    public String upload(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            String ext = extractExtension(file.getOriginalFilename());

            String fileName = FtpUploader.uploadStream(
                    ftpProperties.getServer(),
                    ftpProperties.getPort(),
                    ftpProperties.getUser(),
                    ftpProperties.getPass(),
                    ftpProperties.getEditorDir(),
                    is,
                    ext
            );

            return ftpProperties.getBaseUrl() + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("FTP 업로드 실패", e);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }

    /* 일반 파일 전송할 때 */
    /**
     * FTP 서버에 파일을 UUID 파일명으로 업로드하고,
     * 성공하면 저장된 파일명(UUID.확장자)을 반환한다.
     *
     * @param server FTP 서버 주소 (예: "ftp.example.com")
     * @param port FTP 포트 (기본: 21)
     * @param user FTP 사용자 이름
     * @param pass FTP 비밀번호
     * @param remoteDir FTP 서버 내 저장할 디렉토리 경로 (예: "/upload/images")
     * @param file 업로드할 파일 (스프링에서 받아온 MultipartFile)
     * @return 실제 저장된 UUID 파일명 (확장자 포함)
     * @throws IOException 업로드 중 문제 발생 시 예외 발생
     */
    public static String uploadFile(
            String server, int port, String user, String pass,
            String remoteDir, MultipartFile file) throws IOException {

        // FTPClient 객체 생성
        FTPClient ftpClient = new FTPClient();

        // 업로드 성공 시 반환할 파일명 (URL 아님!)
        String savedFileName = null;

        // 1) 업로드할 파일의 원본 이름에서 확장자만 추출
        String originalName = file.getOriginalFilename();
        String ext = "";

        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }

        // 2) UUID 생성해서 새 파일명 구성 (예: uuid.png)
        String uuidFileName = UUID.randomUUID().toString() + ext;

        try (InputStream inputStream = file.getInputStream()) {

            // 3) FTP 서버 연결
            ftpClient.connect(server, port);

            // 4) FTP 서버 로그인
            boolean login = ftpClient.login(user, pass);
            if (!login) {
                throw new IOException("FTP 로그인 실패");
            }

            // 5) Passive 모드 설정 ( 서버는 열려 있는 포트만 알려줌, 방화벽에 안전 )
            ftpClient.enterLocalPassiveMode();

            // 6) 바이너리 파일 모드 설정 (이미지 손상 방지)
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 7) 업로드 디렉토리 이동
            boolean changedDir = ftpClient.changeWorkingDirectory(remoteDir);
            if (!changedDir) {
                throw new IOException("디렉토리 이동 실패: " + remoteDir);
            }

            // 8) UUID 파일명으로 파일 저장
            boolean stored = ftpClient.storeFile(uuidFileName, inputStream);
            if (!stored) {
                throw new IOException("파일 업로드 실패: " + uuidFileName);
            }

            // 9) 저장된 파일명 리턴 준비
            savedFileName = uuidFileName;

            // 10) 로그아웃
            ftpClient.logout();

        } finally {
            // 11) FTP 연결 닫기
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        }

        return savedFileName;
    }

    /* 썸네일 이미지로 전송할 때 인풋 스트림으로 */
    /**
     * 바이트 스트림(InputStream)을 FTP 서버에 업로드하고,
     * UUID + 확장자 형태의 파일명을 반환합니다.
     *
     * @param server    FTP 서버 주소
     * @param port      FTP 포트
     * @param user      FTP 사용자 이름
     * @param pass      FTP 비밀번호
     * @param remoteDir FTP 서버 내 저장 디렉토리 (예: "/images/2/thumb")
     * @param stream    업로드할 데이터를 담은 InputStream
     * @param ext       파일 확장자(예: ".png", ".jpg")
     * @return 저장된 파일명(UUID + ext)
     * @throws IOException 업로드 실패 시 예외 발생
     */
    public static String uploadStream(
            String server,
            int port,
            String user,
            String pass,
            String remoteDir,
            InputStream stream,
            String ext
    ) throws IOException {
        // 1) UUID 기반 파일명 생성
        String fileName = UUID.randomUUID().toString() + ext;

        FTPClient ftp = new FTPClient();
        try {
            // 2) 서버 접속 및 로그인
            ftp.connect(server, port);
            if (!ftp.login(user, pass)) {
                throw new IOException("FTP 로그인 실패");
            }

            // 3) Passive 모드 & 바이너리 모드 설정
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // 4) 디렉토리 이동
            if (!ftp.changeWorkingDirectory(remoteDir)) {
                throw new IOException("디렉토리 이동 실패: " + remoteDir);
            }

            // 5) 스트림 업로드
            if (!ftp.storeFile(fileName, stream)) {
                throw new IOException("스트림 업로드 실패: " + fileName);
            }

            // 6) 로그아웃
            ftp.logout();
        } finally {
            // 7) 연결 해제
            if (ftp.isConnected()) {
                ftp.disconnect();
            }
        }

        return fileName;
    }
}
