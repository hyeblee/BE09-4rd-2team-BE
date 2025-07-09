package com.playblog.blogservice.search.service;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TestUserInfo;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.common.entity.User;
import com.playblog.blogservice.common.exception.ErrorCode;
import com.playblog.blogservice.common.exception.SearchException;
import com.playblog.blogservice.search.dto.*;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NeighborRepository neighborRepository;

    // 조회 테스트용 게시글 생성
    public void createPost(PostRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new SearchException(ErrorCode.USER_NOT_FOUND));
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .imageUrls(request.getImageUrls())
                .subTopic(request.getSubTopic())
                .user(user)
                .build();
        searchRepository.save(post);
    }

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        Page<Post> postsPage = searchRepository.findAll(pageable);
        List<PostSummaryDto> result = convertToPostSummaryDtos(postsPage.getContent());
        // PageImpl을 사용하여 페이지 정보와 함께 반환
        return new PageImpl<>(result, pageable, postsPage.getTotalElements());
    }

    // 글 제목 또는 내용으로 검색
    @Transactional(readOnly = true)
    public List<PostSummaryDto> findByTitleOrContent(String keyword) {
        // 키워드에 아무것도 입력하지 않은 경우 예외 처리
        if (keyword == null || keyword.isBlank()) {
            throw new SearchException(ErrorCode.INVALID_PARAMETER);
        }
        List<Post> posts = searchRepository.findByTitleOrContent(keyword);
        // 검색 결과가 없을 경우 예외 처리
        if (posts == null || posts.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        return convertToPostSummaryDtos(posts);
    }

    // 모든 주제 정보 가져오기
    public List<AllTopicResponseDto> getAllTopics() {
        Map<TopicType, List<SubTopic>> groupedTopics = Arrays.stream(SubTopic.values())
                .collect(Collectors.groupingBy(SubTopic::getTopicType));

        return Arrays.stream(TopicType.values())
                .map(topicType -> {
                    List<SubTopicResponseDto> subTopicResponses =  groupedTopics.getOrDefault(topicType, List.of())
                            .stream()
                            .map(sub -> new SubTopicResponseDto(sub.name(), sub.getTopicName()))
                            .toList();

                    return new AllTopicResponseDto(
                            topicType.name(),
                            topicType.getMainTopic(),
                            subTopicResponses
                    );
                })
                .collect(Collectors.toList());
    }

    // 특정 주제에 해당하는 게시글 조회
    @Transactional(readOnly = true)
    public List<PostSummaryDto> findBySubTopic(SubTopic subTopic) {
        List<Post> posts = searchRepository.findBySubTopic(subTopic);
        return convertToPostSummaryDtos(posts);
    }

    // 블로그 제목 또는 소개글로 게시글 검색
    @Transactional(readOnly = true)
    public List<BlogSearchDto> searchByBlogTitleOrProfileIntro(String blogTitle) {
        if (blogTitle == null || blogTitle.isBlank()) {
            throw new SearchException(ErrorCode.INVALID_PARAMETER);
        }
        List<BlogSearchDto> result = userRepository.findByBlogTitleOrProfileIntro(blogTitle).stream()
                .map(u -> toBlogSearchDto(u, false, true))
                .toList();

        if (result.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        return result;
    }

    // 별명 또는 블로그 아이디로 사용자 검색
    @Transactional(readOnly = true)
    public List<BlogSearchDto> searchByNicknameOrBlogId(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new SearchException(ErrorCode.INVALID_PARAMETER);
        }
        List<BlogSearchDto> result = userRepository.findByNicknameOrBlogId(nickname).stream()
                .map(u -> toBlogSearchDto(u, true, false))
                .toList();

        if (result.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        return result;
    }

    // 이웃 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getNeighborPosts(Long myUserId, Pageable pageable) {
        if (myUserId == null) {
            throw new SearchException(ErrorCode.INVALID_PARAMETER);
        }
        // 1. 이웃 userId 리스트 조회
        List<Long> neighborUserIds = neighborRepository.findFollowingUserIdsByUserId(myUserId);
        if (neighborUserIds == null || neighborUserIds.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT); // 이웃이 없습니다
        }
        // 2. 이웃 userId로 최신 게시글 목록 조회 (페이징)
        Page<Post> posts = searchRepository.findByUserIdInOrderByCreatedAtDesc(neighborUserIds, pageable);
        // 3. DTO로 변환 (공통 메소드 활용)
        List<PostSummaryDto> result = convertToPostSummaryDtos(posts.getContent());
        // 4. PageImpl로 래핑해서 반환
        return new PageImpl<>(result, pageable, posts.getTotalElements());
    }


    // 좋아요 수, 댓글 수 집계 후 PostSummaryDto로 변환하는 공통 메서드
    private List<PostSummaryDto> convertToPostSummaryDtos(List<Post> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        Map<Long, Long> likeCounts = postLikeRepository.countLikesByPostIds(postIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
        Map<Long, Long> commentCounts = commentRepository.countCommentsByPostIds(postIds).stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
        return posts.stream()
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .nickname(post.getUser().getUserInfo().getNickname())
                        .blogTitle(post.getUser().getUserInfo().getBlogTitle())
                        .thumbnailImageUrl(post.getThumbnailImageUrl())
                        .profileImageUrl(post.getUser().getUserInfo().getProfileImageUrl())
                        .likeCount(likeCounts.getOrDefault(post.getId(), 0L))
                        .commentCount(commentCounts.getOrDefault(post.getId(), 0L))
                        .createdAt(post.getCreatedAt())
                        .subTopic(post.getSubTopic())
                        .build())
                .toList();
    }

    private BlogSearchDto toBlogSearchDto(User user, boolean includeBlogId, boolean includeBlogTitle) {
        TestUserInfo info = user.getUserInfo();
        BlogSearchDto.BlogSearchDtoBuilder builder = BlogSearchDto.builder()
                .profileIntro(info.getProfileIntro())
                .nickname(info.getNickname());
        if (includeBlogTitle) builder.blogTitle(info.getBlogTitle());
        if (includeBlogId)    builder.blogId(info.getBlogId());
        return builder.build();
    }
}