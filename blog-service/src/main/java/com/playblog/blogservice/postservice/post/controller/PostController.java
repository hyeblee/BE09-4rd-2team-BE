package com.playblog.blogservice.postservice.post.controller;

import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
//import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
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

    /* 게시글 발행 */
    @PostMapping
    public ResponseEntity<PostResponseDto> publishPost(
            @Valid @RequestPart PostRequestDto requestDto,
            @RequestPart MultipartFile thumbnailFile
    ) throws Exception {
        // 요청 DTO → 서비스에 넘김 (포스트맨에서 다르게 보여주기 위해서)
        PostResponseDto response = postService.publishPost(requestDto, thumbnailFile);
        // 응답 DTO 리턴
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 게시글 상세 조회 */
    @GetMapping("/main/{postId}")
    public ResponseEntity<PostResponseDto> PostDetailResponse(@PathVariable Long postId) {
        PostResponseDto response = postService.getPostDetail(postId);
        return ResponseEntity.ok(response);
    }

    /* 게시글 수정 */
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestBody @Valid PostRequestDto requestDto) {
        PostResponseDto response = postService.updatePost(postId, requestDto);
        return ResponseEntity.ok(response);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}
