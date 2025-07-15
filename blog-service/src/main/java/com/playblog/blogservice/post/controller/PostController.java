package com.playblog.blogservice.post.controller;

import com.playblog.blogservice.common.ApiResponse;
import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.ftp.controller.FtpUploadController;
import com.playblog.blogservice.post.dto.PostRequestDto;
//import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.entity.PostPolicy;
import com.playblog.blogservice.post.repository.PostPolicyRepository;
import com.playblog.blogservice.post.repository.PostRepository;
import com.playblog.blogservice.post.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final FtpUploader ftpUploader;
    private final PostPolicyRepository postPolicyRepository;

    /* 게시글 발행 */
    /**
     * 게시글 발행 API
     * @param requestDto JSON 형태의 게시글 데이터
     * @param thumbnailFile 선택적 썸네일 이미지 파일
     * @return 생성된 게시글의 응답 DTO와 Location 헤더
     */
    // 스프링이 기본 컨버터를 통해 multipart/form-data 처리도 자동으로 지원
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

    /* 내 블로그 조회 */
    /**
     * 사용자의 블로그 게시글 상세 정보를 조회하는 API
     *
     * @param authentication 인증 객체로부터 사용자 ID를 추출
     * @param postId 조회할 게시글의 ID
     * @return 게시글 정보를 담은 응답 객체
     */
    @GetMapping("/myblog/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getMyPostDetail(
            Authentication authentication,
            @PathVariable Long postId
    ) {
        // 1. 인증된 사용자 ID 추출
        Long userId = Long.parseLong(authentication.getName());

        // 2. 게시글 ID와 사용자 ID로 상세 정보 조회
        PostResponseDto dto = postService.getMyPostDetail(postId, userId);

        // 3. 응답 반환
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /* 다른 사람 블로그 조회 */
    /**
     * 다른 사용자의 블로그 게시글 상세 정보를 조회하는 API
     *
     * @param postId 조회할 게시글의 ID
     * @return 게시글 정보를 담은 응답 객체
     */
    @GetMapping("/otherblog/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getOtherPostDetail(
            @PathVariable Long postId
    ) {
        // 주어진 게시글 ID를 기반으로 다른 사용자의 게시글 상세 정보를 조회
        PostResponseDto dto = postService.getOtherPostDetail(postId);

        // 성공 응답과 함께 게시글 정보를 반환
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    /* 게시글 수정 */
    @PutMapping(
            value = "/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public PostResponseDto updatePost(@PathVariable("postId") Long postId, PostRequestDto requestDto, MultipartFile thumbnailFile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        // DTO에서 유저를 받아오는 경우
        Long requestUserId = requestDto.getUserId();
        if (requestUserId == null) {
            log.warn("※ 테스트용: 사용자 ID가 없어 기본값 1L 사용");
            requestUserId = 1L; // 테스트용
        }

        // 테스트 환경에서는 작성자 검증 생략
        if (post.getUser().getId() != requestUserId) {
            log.warn("※ 테스트 모드: 작성자 아님에도 수정 허용 (userId={}, postOwnerId={})", requestUserId, post.getUser().getId());
            // throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        // 썸네일 수정
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String newUrl = ftpUploader.upload(thumbnailFile);
            requestDto.setThumbnailImageUrl(newUrl);
        }

        // 게시글 수정
        post.update(requestDto); // 제목, 내용, 썸네일 등 갱신

        // 정책 조회 후 없으면 테스트용 생성
        PostPolicy policy = postPolicyRepository.findByPostId(postId)
                .orElseGet(() -> {
                    log.warn("※ 테스트용: 정책 정보가 없어 기본 정책 생성");
                    PostPolicy testPolicy = PostPolicy.defaultPublicPolicy(post);
                    // PostVisibility visibility = post.getVisibility();
                    return postPolicyRepository.save(testPolicy);
                });

        policy.update(
                requestDto.getAllowComment(),
                requestDto.getAllowLike(),
                requestDto.getAllowSearch()
        );

        return PostResponseDto.from(post, policy, post.getUser().getUserInfo(), null, null);
    }

    /* 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        String resultMessage = postService.deletePost(postId, userId);
        return ResponseEntity.ok(ApiResponse.success(resultMessage));
    }

//    /* (기본형) 게시글 상세 조회 */
//    @GetMapping("/main/{postId}")
//    public ResponseEntity<PostResponseDto> PostDetailResponse(@PathVariable Long postId) {
//        PostResponseDto response = postService.getPostDetail(postId);
//        return ResponseEntity.ok(response);
//    }

}

