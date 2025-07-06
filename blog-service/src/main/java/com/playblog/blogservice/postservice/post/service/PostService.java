package com.playblog.blogservice.postservice.post.service;

import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Post

    createPost(PostRequestDto requestDto) {
        // 1. DTO를 Entity로 변환
        Post newPost = requestDto.toEntity();

        // 2. Repository를 통해 데이터베이스에 저장
        //    publishedAt은 @CreationTimestamp에 의해 자동으로 현재 시간으로 설정됨
        return postRepository.save(newPost);
    }
}