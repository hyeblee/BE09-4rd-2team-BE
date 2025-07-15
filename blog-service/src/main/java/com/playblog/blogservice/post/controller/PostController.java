package com.playblog.blogservice.post.controller;

import com.playblog.blogservice.post.dto.PostRequestDto;
//import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/posts")
public class PostController {

    private final PostService postService;


    /* 게시글 발행 */
    /**
     * 게시글 발행 API
     * @param requestDto JSON 형태의 게시글 데이터
     * @param thumbnailFile 선택적 썸네일 이미지 파일
     * @return 생성된 게시글의 응답 DTO와 Location 헤더
     */
    // 스프링이 기본 컨버터를 통해 multipart/form-data 처리도 자동으로 지원
    @PostMapping/*(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)*/
    public ResponseEntity<PostResponseDto> publishPost(
            @Valid @RequestPart("requestDto") PostRequestDto requestDto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
            // 썸네일 파일은 multipart 요청의 또 다른 부분이며, 선택 사항입니다.

    ) throws Exception {
        // 로그로 게시물 제목을 출력하여 요청이 들어왔음을 기록합니다
        log.info("[POST] /api/posts - publishPost called with title='{}'", requestDto.getTitle());

        // 게시물 작성 로직을 서비스 레이어에 위임하고 결과를 받아옵니다.
        PostResponseDto response = postService.publishPost(requestDto, thumbnailFile);

        // 응답 Location 헤더에 사용할 URL을 생성합니다. (/api/posts/{id} 형태)
        String location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getPostId())
                .toUriString();

        // 게시물이 성공적으로 생성되었음을 로그로 기록합니다.
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, location);

        // 게시물 응답 데이터와 Location 헤더를 포함하여 201(CREATED) 상태코드로 반환합니다.
        log.info("[POST] /api/posts - created post id={} at {}", response.getPostId(), location);
        return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
    }

    /* 게시글 상세 조회 */
    @GetMapping("/main/{postId}")
    public ResponseEntity<PostResponseDto> PostDetailResponse(@PathVariable Long postId) {
        PostResponseDto response = postService.getPostDetail(postId);
        return ResponseEntity.ok(response);
    }

//    /* 게시글 수정 */
//    @PutMapping("/{postId}")
//    public ResponseEntity<PostResponseDto> updatePost(
//            @PathVariable Long postId,
//            @RequestBody @Valid PostRequestDto requestDto) {
//        PostResponseDto response = postService.updatePost(postId, requestDto);
//        return ResponseEntity.ok(response);
//    }

    /* 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

}

