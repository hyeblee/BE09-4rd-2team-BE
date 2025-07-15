package com.playblog.blogservice.search.service;

import com.playblog.blogservice.comment.repository.CommentLikeRepository;
import com.playblog.blogservice.comment.repository.CommentRepository;
import com.playblog.blogservice.comment.service.CommentService;
import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.common.exception.ErrorCode;
import com.playblog.blogservice.common.exception.SearchException;
import com.playblog.blogservice.neighbor.Entity.Neighbor;
import com.playblog.blogservice.neighbor.Service.NeighborService;
import com.playblog.blogservice.post.entity.Post;

import com.playblog.blogservice.post.repository.PostRepository;
import com.playblog.blogservice.postlike.repository.PostLikeRepository;
import com.playblog.blogservice.postlike.service.PostLikeService;
import com.playblog.blogservice.search.dto.*;
import com.playblog.blogservice.user.User;
import com.playblog.blogservice.userInfo.UserInfo;
import com.playblog.blogservice.userInfo.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final NeighborService neighborService;

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public Page<PostSummaryDto> getAllPosts(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll(pageable);
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
        List<Post> posts = postRepository.findByTitleOrContent(keyword);
        // 검색 결과가 없을 경우 예외 처리
        if (posts == null || posts.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        return convertToPostSummaryDtos(posts);
    }

    // 모든 주제 정보 가져오기
    public List<AllTopicResponseDto> getAllTopics() {
        // SubTopic들을 TopicType별로 그룹화
        Map<TopicType, List<SubTopic>> groupedTopics = Arrays.stream(SubTopic.values())
                .collect(Collectors.groupingBy(SubTopic::getMainTopic));

        // TopicType 기준으로 AllTopicResponseDto 리스트 만들기
        return Arrays.stream(TopicType.values())
                .map(topicType -> {
                    // 이 TopicType에 해당하는 SubTopic 리스트를 꺼내서, 각 subTopic에 대해 Dto 생성
                    List<SubTopicResponseDto> subTopicDtos = groupedTopics.getOrDefault(topicType, List.of())
                            .stream()
                            .map(sub -> new SubTopicResponseDto(
                                    sub.name(),        // "subTopic" : Enum 이름
                                    sub.getSubtopicName() // "subTopicName" : 한글명칭
                            ))
                            .toList();

                    // AllTopicResponseDto 생성 (각 topicType마다)
                    return new AllTopicResponseDto(
                            topicType.name(),           // "topicType": "ENTERTAIN" 등
                            topicType.getTopicTypeName(), // "topicName": "엔터테인먼트.예술" 등
                            subTopicDtos                  // "subTopics"
                    );
                })
                .toList();
    }


    // 특정 주제에 해당하는 게시글 조회
    @Transactional(readOnly = true)
    public List<PostSummaryDto> findBySubTopic(SubTopic subTopic) {
        List<Post> posts = postRepository.findBySubTopic(subTopic);
        return convertToPostSummaryDtos(posts);
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
        // 1. 내가 추가한 이웃(ACCEPTED, REQUESTED) 리스트 조회
        List<Neighbor> neighbors = neighborService.getAddedForMeNeighbors(myUserId);

        // 2. 이웃 UserInfo id 추출
        List<Long> neighborUserInfoIds = neighbors.stream()
                .map(neighbor -> neighbor.getToUserInfo().getId())
                .toList();

        if (neighborUserInfoIds.isEmpty()) {
            throw new SearchException(ErrorCode.EMPTY_RESULT);
        }
        // 3. 이웃의 userInfoId로 게시글 조회
        Page<Post> posts = postRepository.findByUser_UserInfo_IdInOrderByPublishedAtDesc(neighborUserInfoIds, pageable);
        List<PostSummaryDto> result = convertToPostSummaryDtos(posts.getContent());
        return new PageImpl<>(result, pageable, posts.getTotalElements());
    }


    // 좋아요 수, 댓글 수 집계 후 PostSummaryDto로 변환하는 공통 메서드
    private List<PostSummaryDto> convertToPostSummaryDtos(List<Post> posts) {
        return posts.stream()
                .map(post -> {
                    User user = post.getUser();
                    UserInfo info = userInfoRepository
                            .findByUser(user)
                            .orElseThrow(() -> new SearchException(ErrorCode.USER_NOT_FOUND));
                    SubTopic subTopic = post.getSubTopic();
                    String subTopicName = subTopic != null ? subTopic.getSubtopicName() : null;
                    long likeCount = postLikeService.getPostLikeCount(post.getId());
                    long commentCount = commentService.getCommentCount(post.getId());

                    return PostSummaryDto.builder()
                            .postId(post.getId())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .nickname(info.getNickname())
                            .blogTitle(info.getBlogTitle())
                            .thumbnailImageUrl(post.getThumbnailImageUrl())
                            .profileImageUrl(info.getProfileImageUrl())
                            .likeCount(likeCount)
                            .commentCount(commentCount)
                            .publishedAt(post.getPublishedAt())
                            .subTopic(subTopic)
                            .subTopicName(subTopicName)
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