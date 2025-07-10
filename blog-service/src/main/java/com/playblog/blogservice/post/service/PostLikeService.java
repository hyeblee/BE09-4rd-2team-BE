package com.playblog.blogservice.post.service;

import com.playblog.blogservice.post.dto.PostLikeResponse;
import com.playblog.blogservice.post.dto.PostLikeUserResponse;
import com.playblog.blogservice.post.dto.PostLikesResponse;
import com.playblog.blogservice.post.entity.PostLike;
import com.playblog.blogservice.post.repository.PostLikeRepository;
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

    private final PostLikeRepository postLikeRepository;

    // 1. 게시글 공감 토글 (있으면 취소, 없으면 추가)
    @Transactional
    public PostLikeResponse togglePostLike(Long postId, Long userId) {
        Optional<PostLike> existingLike = postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (existingLike.isPresent()) {
            // 이미 공감했으면 공감 취소
            postLikeRepository.deleteByPostIdAndUserId(postId, userId);

            long likeCount = postLikeRepository.countByPostId(postId);
            return new PostLikeResponse(false, likeCount);
        } else {
            // 공감하지 않았으면 공감 추가
            PostLike newLike = PostLike.builder()
                    .postId(postId)
                    .userId(userId)
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
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        long likeCount = postLikeRepository.countByPostId(postId);
        return new PostLikeResponse(isLiked, likeCount);
    }

    // 4. 게시글에 공감한 사용자 목록 조회
    public PostLikesResponse getPostLikeUsers(Long postId) {
        List<PostLike> postLikes = postLikeRepository.findByPostIdOrderByCreatedAtDesc(postId);

        // PostLike들을 PostLikeUserResponse로 변환
        List<PostLikeUserResponse> likedUsers = postLikes.stream()
                .map(postLike -> {
                    // UserDto 생성 (임시 데이터)
                    PostLikeUserResponse.UserDto user = new PostLikeUserResponse.UserDto(
                            postLike.getUserId(),
                            "임시닉네임",  // TODO: User Service 연동
                            "임시프로필URL",
                            "한 줄 소개"
                    );

                    return new PostLikeUserResponse(
                            user,
                            true,  // TODO: 실제 이웃 여부 확인
                            postLike.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());

        return new PostLikesResponse(likedUsers, (long) likedUsers.size());
    }

}
