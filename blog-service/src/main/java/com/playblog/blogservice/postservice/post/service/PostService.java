package com.playblog.blogservice.postservice.post.service;

import com.playblog.blogservice.ftp.common.FtpUploader;
import com.playblog.blogservice.ftp.config.FtpProperties;
import com.playblog.blogservice.postservice.post.dto.PostRequestDto;
import com.playblog.blogservice.postservice.post.dto.PostResponseDto;
import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.entity.PostPolicy;
import com.playblog.blogservice.postservice.post.repository.TestLikeRepository;
import com.playblog.blogservice.postservice.post.repository.PostPolicyRepository;
import com.playblog.blogservice.postservice.post.repository.PostRepository;
import com.playblog.blogservice.search.repository.UserRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    private final UserRepository userRepository;
    private final TestLikeRepository testLikeRepository;
    // FtpProperties 주입
    private final FtpProperties ftpProperties;


    @Transactional
    public PostResponseDto publishPost(PostRequestDto requestDto, MultipartFile thumbnailFile) throws IOException {

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


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
        Post post = postRepository.save(requestDto.toEntity(user));

        // 5. PostPolicy 저장
        PostPolicy policy = requestDto.toPolicyEntity(post);
        postPolicyRepository.save(policy);

        // 6. 작성자 정보 + 상세 응답 형태로 가공
        UserInfo userInfo = post.getUser().getUserInfo();
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo가 연결되지 않았습니다. User ID: " + post.getUser().getId());
        }
        Long likeCount = 0L; // 최초 0
        Boolean isLiked = null;

        return PostResponseDto.from(post, userInfo, policy, likeCount, isLiked);
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글 없음"));

        UserInfo userInfo = post.getUser().getUserInfo(); // 여기선 DB 연관 FK 따라감
        if (userInfo == null) {
            throw new IllegalStateException("UserInfo가 연결되지 않았습니다. User 테이블 user_info_id FK 확인하세요.");
        }
        PostPolicy policy = postPolicyRepository.findByPostId(post.getId())
                .orElseThrow(() -> new EntityNotFoundException("정책 없음"));

        Long likeCount = testLikeRepository.countByPostId(post.getId());;    // 공감 수
        Boolean isLiked = null; // (현재) 로그인 사용자 없으면 판단 불가
//        Boolean isLiked = likeRepository.existsByPostIdAndUserId((post.getId(), currentUserId);   // 사용자가 눌렀는지

        return PostResponseDto.from(post, userInfo, policy, likeCount, isLiked);
    }

    @Transactional
    public PostResponseDto updatePost(Long postId, @Valid PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다. id=" + postId));

        // JPA의 영속성 컨텍스트와 변경 감지(dirty checking) 기능
        post.update(
                dto.getTitle(),
                dto.getContent(),
                dto.getVisibility(),
                dto.getAllowComment(),
                dto.getAllowLike(),
                dto.getAllowSearch(),
                dto.getThumbnailImageUrl(),
                dto.getMainTopic(),    // enum 필드
                dto.getSubTopic()      // enum 필드
        );

        return PostResponseDto.fromEntity(post);
    }

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


}


