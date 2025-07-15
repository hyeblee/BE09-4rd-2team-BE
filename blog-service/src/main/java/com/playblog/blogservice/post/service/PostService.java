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
import com.playblog.blogservice.user.UserRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.EntityNotFoundException;
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
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    // FtpProperties 주입
    private final FtpProperties ftpProperties;

    @Transactional
    public PostResponseDto publishPost(PostRequestDto requestDto, MultipartFile thumbnailFile) throws IOException {

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = postRepository.save(requestDto.toEntity(user));


        // 1. FTP 업로드
        String savedFileName = FtpUploader.uploadFile(
                "dev.macacolabs.site",
                21,
                "team2",
                "1234qwer",
                "/images/2/thumb",
                thumbnailFile
        );

        // 2. URL 생성
        String thumbnailUrl = "https://cdn.example.com/images/" + savedFileName;

        // 3. DTO에 URL 세팅 + 카테고리 고정
        requestDto.setThumbnailImageUrl(thumbnailUrl);
        requestDto.setCategory("게시글");

        // 4. Post 저장
        Post post1 = postRepository.save(requestDto.toEntity(user));

        // 5. PostPolicy 저장
        PostPolicy policy = requestDto.toPolicyEntity(post1);
        postPolicyRepository.save(policy);

        // 6. 작성자 정보 + 상세 응답 형태로 가공
        UserInfo userInfo = post.getUser().getUserInfo();
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo가 연결되지 않았습니다. User ID: " + post.getUser().getId());
        }
        Long likeCount = 0L; // 최초 0
        Boolean isLiked = null;

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
    public void deletePost(Long postId) {

        // 1) PostPolicy 엔티티 조회 ( 우선 삭제 )
        PostPolicy policy = postPolicyRepository.findById(postId)
                .orElse(null);
        if (policy != null) {
            postPolicyRepository.delete(policy);
        }

        // 2) Post 엔티티 삭제   ( 부모 엔터티 삭제 )
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("삭제할 게시글이 없습니다. id=" + postId);
        }
        postRepository.deleteById(postId);
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
        PostPolicy policy = postPolicyRepository.findByPostId(post.getId())
                .orElseThrow(() -> new EntityNotFoundException("정책 정보가 없습니다."));

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


