package com.playblog.blogservice.post.controller;

import com.playblog.blogservice.common.repository.UserRepository;
import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.post.dto.PostRequestDto;
//import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.entity.PostPolicy;
import com.playblog.blogservice.post.repository.PostPolicyRepository;
import com.playblog.blogservice.post.repository.PostRepository;
import com.playblog.blogservice.post.service.PostService;
import com.playblog.blogservice.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(path = "/posts")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FtpUploader ftpUploader;    // FTP 업로더 주입
    private final PostPolicyRepository postPolicyRepository;

    /* 게시글 발행 */
    /**
     * 게시글 발행 API
     * @param requestDto JSON 형태의 게시글 데이터
     * @param thumbnailFile 선택적 썸네일 이미지 파일
     * @return 생성된 게시글의 응답 DTO와 Location 헤더
     */
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
// 스프링이 기본 컨버터를 통해 multipart/form-data 처리도 자동으로 지원
    /*@PostMapping
    public ResponseEntity<PostResponseDto> publishPost(
            @Valid @RequestPart("requestDto") PostRequestDto requestDto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
        // 썸네일 파일은 multipart 요청의 또 다른 부분이며, 선택 사항입니다.
    ){
        // 1) 유저 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("유저 없음"));

        // 2) 썸네일 FTP 저장 (Optional)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String thumbUrl = ftpUploader.upload(thumbnailFile);
            requestDto.setThumbnailImageUrl(thumbUrl);
        }

        // 3) Post + Policy 생성
        Post post = postRepository.save(requestDto.toEntity(user));
        PostPolicy policy = requestDto.toPolicyEntity(post);
        postRepository.save(policy);

        // 4) 응답
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.toResponse(post, policy, *//*currentUserId=*//* null));
        }*/

    /* 게시글 생성(발행) */
    @PostMapping
    public ResponseEntity<PostResponseDto> publishPost(
            @RequestBody @Valid PostRequestDto dto
    ) {
        // 1) 유저 조회
        User user = userRepository.getReferenceById(dto.getUserId());

        // 2) Post + Policy 저장
        Post post = postRepository.save(dto.toEntity(user));
        PostPolicy policy = postPolicyRepository.save(dto.toPolicyEntity(post));

        // 3) 응답 DTO 반환
        // Location 헤더에 사용할 URI를 생성합니다.
        // 현재 요청 URI(/api/posts)에 "/{id}" 경로를 붙이고, 실제 생성된 postId로 치환합니다.
        PostResponseDto response = postService.toResponse(post, policy, /* currentUserId */ null);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getPostId())
                .toUri();
        return ResponseEntity.created(location).body(response);
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

