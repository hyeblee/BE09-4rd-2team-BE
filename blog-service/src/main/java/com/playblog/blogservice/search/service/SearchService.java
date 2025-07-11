package com.playblog.blogservice.search.service;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.common.exception.ErrorCode;
import com.playblog.blogservice.common.exception.SearchException;
import com.playblog.blogservice.neighbor.Repository.NeighborRepository;
import com.playblog.blogservice.search.dto.*;
import com.playblog.blogservice.search.entity.TestPost;
import com.playblog.blogservice.search.repository.*;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
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
    private final UserInfoRepository userInfoRepository;
    private final NeighborRepository neighborRepository;

    // 조회 테스트용 게시글 생성
    public void createPost(PostRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new SearchException(ErrorCode.USER_NOT_FOUND));
        TestPost testPost = TestPost.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .PublishedAt(LocalDateTime.now())
                .thumbnailImageUrl(request.getThumbnailImageUrl())
                .imageUrls(request.getImageUrls())
                .subTopic(request.getSubTopic())
                .user(user)
                .build();
        searchRepository.save(testPost);
    }

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        Page<TestPost> postsPage = searchRepository.findAll(pageable);
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
        List<TestPost> testPosts = searchRepository.findByTitleOrContent(keyword);
        // 검색 결과가 없을 경우 예외 처리
        if (testPosts == null || testPosts.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        return convertToPostSummaryDtos(testPosts);
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
        List<TestPost> testPosts = searchRepository.findBySubTopic(subTopic);
        return convertToPostSummaryDtos(testPosts);
    }

    // 블로그 제목 또는 소개글로 게시글 검색
    @Transactional(readOnly = true)
    public List<BlogSearchDto> searchByBlogTitleOrProfileIntro(String blogTitle) {
        if (blogTitle == null || blogTitle.isBlank()) {
            throw new SearchException(ErrorCode.INVALID_PARAMETER);
        }
        List<BlogSearchDto> result = userInfoRepository.findByBlogTitleOrProfileIntro(blogTitle).stream()
                .map(info -> toBlogSearchDto(info))
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
        List<BlogSearchDto> result = userInfoRepository.findByNicknameOrBlogId(nickname).stream()
                .map(u -> toBlogSearchDto(u))
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
        List<Long> neighborUserIds = neighborRepository.findFollowingUserIdsById(myUserId);
        if (neighborUserIds == null || neighborUserIds.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT); // 이웃이 없습니다
        }
        // 2. 이웃 userId로 최신 게시글 목록 조회 (페이징)
        Page<TestPost> posts = searchRepository.findByUserIdInOrderByPublishedAtDesc(neighborUserIds, pageable);
        // 3. DTO로 변환 (공통 메소드 활용)
        List<PostSummaryDto> result = convertToPostSummaryDtos(posts.getContent());
        // 4. PageImpl로 래핑해서 반환
        return new PageImpl<>(result, pageable, posts.getTotalElements());
    }


    // 좋아요 수, 댓글 수 집계 후 PostSummaryDto로 변환하는 공통 메서드
    private List<PostSummaryDto> convertToPostSummaryDtos(List<TestPost> testPosts) {
        List<Long> postIds = testPosts.stream().map(TestPost::getId).toList();
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
        return testPosts.stream()
                .map(testPost -> {
                    User user = testPost.getUser();
                    UserInfo info = userInfoRepository
                            .findByUser(user)
                            .orElseThrow(() -> new SearchException(ErrorCode.USER_NOT_FOUND));

                    return PostSummaryDto.builder()
                            .postId(testPost.getId())
                            .title(testPost.getTitle())
                            .content(testPost.getContent())
                            .nickname(info.getNickname())
                            .blogTitle(info.getBlogTitle())
                            .thumbnailImageUrl(testPost.getThumbnailImageUrl())
                            .profileImageUrl(info.getProfileImageUrl())
                            .likeCount(likeCounts.getOrDefault(testPost.getId(), 0L))
                            .commentCount(commentCounts.getOrDefault(testPost.getId(), 0L))
                            .createdAt(testPost.getPublishedAt())
                            .subTopic(testPost.getSubTopic())
                            .build();
                })
                .toList();
    }

    private BlogSearchDto toBlogSearchDto(UserInfo info) {
        BlogSearchDto.BlogSearchDtoBuilder builder = BlogSearchDto.builder()
                .profileIntro(info.getProfileIntro())
                .profileImageUrl(info.getProfileImageUrl())
                .nickname(info.getNickname())
                .blogTitle(info.getBlogTitle())
                .blogId(info.getBlogId());

        return builder.build();
    }

}