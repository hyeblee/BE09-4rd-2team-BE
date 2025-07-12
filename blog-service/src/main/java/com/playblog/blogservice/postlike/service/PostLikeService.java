package com.playblog.blogservice.postlike.service;

import com.playblog.blogservice.common.repository.UserRepository;
import com.playblog.blogservice.postlike.dto.PostLikeResponse;
import com.playblog.blogservice.postlike.dto.PostLikeUserResponse;
import com.playblog.blogservice.postlike.dto.PostLikesResponse;
import com.playblog.blogservice.postlike.entity.PostLike;
import com.playblog.blogservice.postlike.repository.PostLikeRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfoService;
import com.playblog.blogservice.userInfo.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

    private final UserRepository userRepository;
    private final UserInfoService userInfoService;
    private final PostLikeRepository postLikeRepository;

    // 1. 게시글 공감 토글 (있으면 취소, 없으면 추가)
    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUser_Id(postId, userId);

        if (existingLike.isPresent()) {
            // 이미 공감했으면 공감 취소
            postLikeRepository.deleteByPostIdAndUser_Id(postId, userId);

            long likeCount = postLikeRepository.countByPostId(postId);
            return new PostLikeResponse(false, likeCount);
        } else {
            // 공감하지 않았으면 공감 추가
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            PostLike newLike = PostLike.builder()
                    .postId(postId)
                    .user(user)
                    .build();
            postLikeRepository.save(newLike);

            long likeCount = postLikeRepository.countByPostId(postId);
            return new PostLikeResponse(true, likeCount);
        }
    }

    // 2. 게시글의 공감 수 조회
    public long getPostLikeCount(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    // 3. 게시글 공감 여부 확인
    public PostLikeResponse isPostLikedByUser(Long postId, Long userId) {
        boolean isLiked = postLikeRepository.existsByPostIdAndUser_Id(postId, userId);
        long likeCount = postLikeRepository.countByPostId(postId);
        return new PostLikeResponse(isLiked, likeCount);
    }

    // 4. 게시글에 공감한 사용자 목록 조회
    public PostLikesResponse getPostLikeUsers(Long postId) {
        List<PostLike> postLikes = postLikeRepository.findByPostIdOrderByCreatedAtDesc(postId);

        // PostLike들을 PostLikeUserResponse로 변환
        List<PostLikeUserResponse> likedUsers = postLikes.stream()
                .map(postLike -> {
                    Long userId = postLike.getUserId();
                    try {
                        UserInfoResponse userInfo = userInfoService.getUserInfo(userId);
                        PostLikeUserResponse.UserDto userDto = new PostLikeUserResponse.UserDto(
                                userId,
                                userInfo.getNickname(),
                                "https://api.pravatar.cc/150?img=" + (userId % 50),
                                userInfo.getProfileIntro()
                        );
                        return new PostLikeUserResponse(userDto, false, postLike.getCreatedAt());
                    } catch (Exception e) {
                        // 탈퇴한 사용자 처리
                        PostLikeUserResponse.UserDto userDto = new PostLikeUserResponse.UserDto(
                                userId,
                                "탈퇴한 사용자",
                                "https://api.pravatar.cc/150?img=" + (userId % 50),
                                ""
                        );
                        return new PostLikeUserResponse(userDto, false, postLike.getCreatedAt());
                    }

                })
                .collect(Collectors.toList());

        return new PostLikesResponse(likedUsers, (long) likedUsers.size());
    }

}
