package com.playblog.blogservice.post.service;

import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.ftp.common.config.FtpProperties;
import com.playblog.blogservice.post.dto.PostRequestDto;
import com.playblog.blogservice.post.dto.PostResponseDto;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.entity.PostPolicy;
import com.playblog.blogservice.post.entity.PostVisibility;
import com.playblog.blogservice.post.repository.PostPolicyRepository;
import com.playblog.blogservice.post.repository.PostRepository;
import com.playblog.blogservice.postlike.repository.PostLikeRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.user.UserRepository;
import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostPolicyRepository postPolicyRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    // FtpProperties 주입
    private final FtpProperties ftpProperties;
    private final FtpUploader ftpUploader;


    @Transactional
    public PostResponseDto publishPost(PostRequestDto requestDto, MultipartFile thumbnailFile) {
        // 1. 사용자 조회
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        /// 2. 썸네일 파일 처리 (있을 경우)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            try {
                String savedFileName = ftpUploader.uploadFile(
                        ftpProperties.getServer(),
                        ftpProperties.getPort(),
                        ftpProperties.getUser(),
                        ftpProperties.getPass(),
                        "/images/2/thumb",
                        thumbnailFile
                );

                String thumbnailUrl = "https://cdn.example.com/images/" + savedFileName;
                requestDto.setThumbnailImageUrl(thumbnailUrl);

            } catch (IOException e) {
                throw new RuntimeException("썸네일 이미지 업로드 실패", e);
            }
        }

        // 3. Post 저장
        Post post = postRepository.save(requestDto.toEntity(user));

        // 4. PostPolicy 저장
        PostPolicy policy = requestDto.toPolicyEntity(post);
        postPolicyRepository.save(policy);

        // 5. UserInfo 조회 및 응답 생성
        UserInfo userInfo = user.getUserInfo();
        return PostResponseDto.from(post, policy, userInfo, 0L, null);
    }


    @Transactional
    public PostResponseDto updatePost(Long postId, PostRequestDto requestDto, MultipartFile thumbnailFile) throws IOException {

        // 1. 게시글 조회 (존재하지 않으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 2. 작성자 검증 (다른 유저의 글을 수정하려 할 경우 예외 발생)
        if (!Objects.equals(post.getUser().getId(), requestDto.getUserId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }
        // 🌤️ 토큰 비교 🌤️
        // 🌤SecurityContextHolder에서 꺼낸 currentUserId를 requestDto.getUserId()와 비교
        /*
        if (!Objects.equals(post.getUser().getId(), currentUserId)) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }*/


        // 3. 썸네일 파일 처리 (있을 경우만 FTP 업로드)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String savedFileName = ftpUploader.uploadFile(
                    ftpProperties.getServer(),
                    ftpProperties.getPort(),
                    ftpProperties.getUser(),
                    ftpProperties.getPass(),
                    "/images/2/thumb",
                    thumbnailFile
            );
            String thumbnailUrl = "https://cdn.example.com/images/" + savedFileName;
            requestDto.setThumbnailImageUrl(thumbnailUrl); // DTO에 URL 저장
        }

        // 4. 게시글 업데이트 (JPA Dirty Checking 활용)
        post.update(
                requestDto.getTitle(),
                requestDto.getContent(),
                requestDto.getVisibility(),
                requestDto.getAllowComment(),
                requestDto.getAllowLike(),
                requestDto.getAllowSearch(),
                requestDto.getThumbnailImageUrl(),
                requestDto.getMainTopic(),
                requestDto.getSubTopic()
        );

        // 5. 정책 업데이트 (정책이 없을 경우 예외 발생)
        PostPolicy policy = postPolicyRepository.findByPostId(postId)
                .orElseThrow(() -> new EntityNotFoundException("정책이 존재하지 않습니다."));
        policy.update(
                requestDto.getAllowComment(),
                requestDto.getAllowLike(),
                requestDto.getAllowSearch()
        );

        // 6. 응답 생성 (작성자 정보 + 공감 수 포함)
        UserInfo userInfo = post.getUser().getUserInfo();
        Long likeCount = postLikeRepository.countByPost_Id(postId);
        Boolean isLiked = null; // 로그인 유저가 공감했는지 여부는 외부에서 처리 필요

        return PostResponseDto.from(post, policy, userInfo, likeCount, isLiked);
    }

//    @Transactional(readOnly = true)
//    public PostResponseDto getMyPostDetail(Long userId) {
//        // 게시글 조회
//        Post post = postRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));
//
//        // 본인 확인
//        if (!Objects.equals(post.getUser().getId(), userId)) {
//            throw new AccessDeniedException("본인의 게시글만 조회할 수 있습니다.");
//        }
//
//        // 작성자 유저 정보
//        UserInfo userInfo = post.getUser().getUserInfo();
//        if (userInfo == null) {
//            throw new IllegalStateException("작성자의 UserInfo가 존재하지 않습니다.");
//        }
//
//        // 정책 조회
//        PostPolicy policy = postPolicyRepository.findByPostId(post.getId())
//                .orElseThrow(() -> new EntityNotFoundException("게시글 정책이 존재하지 않습니다."));
//
//        // 공감 수
//        Long likeCount = postLikeRepository.countByPost_Id(post.getId());
//
//        // 본인이므로 공감 여부 확인 불필요 또는 직접 조회 가능
//        Boolean isLiked = postLikeRepository.existsByPostIdAndUserId(post.getId(), userId);
//
//        return PostResponseDto.from(post, policy, userInfo, likeCount, isLiked);
//    }


//    @Transactional
//    public PostResponseDto updatePost(Long postId, @Valid PostRequestDto dto) {
//        Post post = postRepository.findById(postId)
//                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. id=" + postId));
//
//        // JPA의 영속성 컨텍스트와 변경 감지(dirty checking) 기능
//        post.update(
//                dto.getTitle(),
//                dto.getContent(),
//                dto.getVisibility(),
//                dto.getAllowComment(),
//                dto.getAllowLike(),
//                dto.getAllowSearch(),
//                dto.getThumbnailImageUrl(),
//                dto.getMainTopic(),    // enum 필드
//                dto.getSubTopic()      // enum 필드
//        );
//
//        return PostResponseDto.from(post);
//    }

    @Transactional
    public String deletePost(Long postId, Long userId) {

        // 1. Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 2. 작성자 검증
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new AccessDeniedException("작성자만 삭제할 수 있습니다.");
        }

        // 3. PostPolicy 먼저 삭제 (단방향 관계에서는 이 방법이 안전함)
        postPolicyRepository.deleteByPostId(postId);

        // 4) Post 엔티티 삭제   ( 부모 엔터티 삭제 )
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("삭제할 게시글이 없습니다. id=" + postId);
        }
        postRepository.delete(post);

        // 5. 결과 메시지 반환
        return "게시글이 성공적으로 삭제되었습니다.";
    }

    /**
     * 내 블로그 게시글 상세 조회 (본인 확인 포함)
     */
    @Transactional(readOnly = true)
    public PostResponseDto getMyPostDetail(Long postId, Long userId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 2. 게시글 작성자와 요청자 일치 여부 확인
        if (!Objects.equals(post.getUser().getId(), userId)) {
            throw new AccessDeniedException("본인의 게시글만 조회할 수 있습니다.");
        }

        // 3. 작성자의 UserInfo 조회
        UserInfo userInfo = post.getUser().getUserInfo();
        if (userInfo == null) {
            throw new IllegalStateException("작성자의 UserInfo가 존재하지 않습니다.");
        }

        // 4. 정책(PostPolicy) 조회
        PostPolicy policy = postPolicyRepository.findByPostId(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 정책 정보가 존재하지 않습니다."));

        // 5. 공감 수 조회
        Long likeCount = postLikeRepository.countByPost_Id(postId);

        // 6. 본인 글이므로 isLiked는 null로 처리 (또는 false로)
        Boolean isLiked = null;

        // 7. DTO 변환 및 반환
        return PostResponseDto.from(post, policy, userInfo, likeCount, isLiked);
    }

    /**
     * 다른 사람 블로그 게시글 상세 조회 (공개 여부 확인 포함)
     */
    @Transactional(readOnly = true)
    public PostResponseDto getOtherPostDetail(Long postId) {
        // 1. 게시글 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        // 2. 비공개 게시글은 차단
        if (post.getVisibility() != PostVisibility.PUBLIC) {
            throw new AccessDeniedException("공개된 게시글만 조회할 수 있습니다.");
        }

        // 3. 추가 데이터 조회
        PostPolicy policy = postPolicyRepository.findByPostId(postId)
                .orElse(PostPolicy.defaultPublicPolicy(post)); // ← 여기 핵심
        // PostVisibility visibility = post.getVisibility();

        UserInfo userInfo = post.getUser().getUserInfo();
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo가 연결되지 않았습니다.");
        }

        Long likeCount = postLikeRepository.countByPost_Id(post.getId());
        Boolean isLiked = null; // 비로그인 사용자라면 null로 둠 (현재 로그인 기능 연동 여부에 따라 수정)

        // 4. DTO 변환 및 반환
        return PostResponseDto.from(post, policy, userInfo, likeCount, isLiked);
    }


//    public static PostResponseDto fromEntity(Post post, PostPolicy policy, UserInfo userInfo) {
//        return PostResponseDto.builder()
//                .postId(post.getId())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .visibility(post.getVisibility())
//                .allowComment(policy.getAllowComment())
//                .allowLike(policy.getAllowLike())
//                .allowSearch(policy.getAllowSearch())
//                .blogTitle(userInfo.getBlogTitle())
//                .nickname(userInfo.getNickname())
//                .profileImageUrl(userInfo.getProfileImageUrl())
//                .likeCount(0)
//                .isLiked(null)
//                .build();
//    }



}


