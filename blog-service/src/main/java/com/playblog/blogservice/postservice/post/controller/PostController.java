package com.playblog.blogservice.postservice.post.controller;

import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // Valid를 사용하여 유효성 검사
    @PostMapping
    public ResponseEntity<Post> publishPost(
            @Valid
            @RequestBody PostRequestDto requestDto
    ) {
        Post createdPost = postService.createPost(requestDto);
        // 생성 성공 시 HTTP 201 Created 상태 코드와 함께 생성된 게시글 정보 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }
}
