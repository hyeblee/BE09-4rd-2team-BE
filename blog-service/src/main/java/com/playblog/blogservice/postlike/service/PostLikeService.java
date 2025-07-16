package com.playblog.blogservice.postlike.service;

import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.repository.PostRepository;
import com.playblog.blogservice.user.UserRepository;
import com.playblog.blogservice.postlike.dto.PostLikeResponse;
import com.playblog.blogservice.postlike.dto.PostLikeUserResponse;
import com.playblog.blogservice.postlike.dto.PostLikesResponse;
import com.playblog.blogservice.postlike.entity.PostLike;
import com.playblog.blogservice.postlike.repository.PostLikeRepository;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
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
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    // 1. 게시글 공감 토글 (있으면 취소, 없으면 추가)
    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        // 1. 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

//        // 2. 공감 허용 여부 확인
//        if (post.getAllowLike() != null && !post.getAllowLike()) {
//            throw new IllegalArgumentException("이 게시글은 공감을 허용하지 않습니다.");
//        }

        // 3. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 4. 공감 여부 확인
        Optional<PostLike> existingLike = postLikeRepository.findByPost_IdAndUser_Id(postId, userId);

        if (existingLike.isPresent()) {
            // 공감 취소
            postLikeRepository.deleteByPost_IdAndUser_Id(postId, userId);
            long newCount = postLikeRepository.countByPost_Id(postId);
            return new PostLikeResponse(false, newCount);  // ✅ DTO 사용
        } else {
            // 공감 추가
            PostLike postLike = PostLike.builder()
                    .post(post)
                    .user(user)
                    .build();

            postLikeRepository.save(postLike);
            long newCount = postLikeRepository.countByPost_Id(postId);
            return new PostLikeResponse(true, newCount);   // ✅ DTO 사용
        }
    }

    // 2. 게시글의 공감 수 조회
    public long getPostLikeCount(Long postId) {
        return postLikeRepository.countByPost_Id(postId);
    }

    // 3. 게시글 공감 여부 확인
    public PostLikeResponse isPostLikedByUser(Long postId, Long userId) {
        boolean isLiked = postLikeRepository.findByPost_IdAndUser_Id(postId, userId).isPresent();
        long likeCount = postLikeRepository.countByPost_Id(postId);
        return new PostLikeResponse(isLiked, likeCount);
    }

    // 4. 게시글에 공감한 사용자 목록 조회
    public PostLikesResponse getPostLikeUsers(Long postId) {
        List<PostLike> postLikes = postLikeRepository.findByPost_IdOrderByCreatedAtDesc(postId);

        // PostLike들을 PostLikeUserResponse로 변환
        List<PostLikeUserResponse> likedUsers = postLikes.stream()
                .map(postLike -> {
                    Long userId = postLike.getUserId();
                    try {
                        User user = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                        UserInfo userInfo = user.getUserInfo();
                        String nickname = userInfo != null ? userInfo.getNickname() : "사용자";
                        String profileIntro = userInfo != null ? userInfo.getProfileIntro() : "";

                        PostLikeUserResponse.UserDto userDto = new PostLikeUserResponse.UserDto(
                                userId,
                                nickname,
                                "https://api.pravatar.cc/150?img=" + (userId % 50),
                                profileIntro
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