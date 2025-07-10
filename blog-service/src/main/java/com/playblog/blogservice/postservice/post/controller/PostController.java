package com.playblog.blogservice.postservice.post.controller;

import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> publishPost(
            @Valid @RequestPart PostRequestDto requestDto,
            @RequestPart MultipartFile thumbnailFile
    ) throws Exception {
        Post post = postService.publishPost(requestDto, thumbnailFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }
}
