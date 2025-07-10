package com.playblog.blogservice.postservice.post.service;

import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.entity.PostPolicy;
import com.playblog.blogservice.postservice.post.repository.PostPolicyRepository;
import com.playblog.blogservice.postservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostPolicyRepository postPolicyRepository;

    @Transactional
    public Post publishPost(PostRequestDto requestDto, MultipartFile thumbnailFile) throws IOException {
        // 1. FTP 업로드
        String savedFileName = FtpUploader.uploadFile(
                "dev.macacolabs.site",
                21,
                "team1",
                "1234qwer",
                "/images/1",
                thumbnailFile
        );

        // 2. URL 생성
        String thumbnailUrl = "https://cdn.example.com/images/" + savedFileName;

        // 3. DTO에 URL 세팅 + 카테고리 고정
        requestDto.setThumbnailImageUrl(thumbnailUrl);
        requestDto.setCategory("게시글"); // 고정값

        // 4. Post 저장
        Post post = postRepository.save(requestDto.toEntity());

        // 5. PostPolicy 저장
        PostPolicy policy = requestDto.toPolicyEntity(post);
        postPolicyRepository.save(policy);

        return post;
    }
}
