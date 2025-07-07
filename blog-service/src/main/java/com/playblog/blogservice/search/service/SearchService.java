package com.playblog.blogservice.search.service;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.common.entity.User;
import com.playblog.blogservice.common.entity.UserInfo;
import com.playblog.blogservice.search.dto.*;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    // 조회 테스트용 게시글 생성
    public void createPost(PostRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
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
        List<Post> posts = postsPage.getContent();

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

        List<PostSummaryDto> result = posts.stream()
                .map(post -> PostSummaryDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .nickname(post.getUser().getUserInfo().getNickname())
                        .blogTitle(post.getUser().getUserInfo().getBlogTitle())
                        .likeCount(likeCounts.getOrDefault(post.getId(), 0L))
                        .commentCount(commentCounts.getOrDefault(post.getId(), 0L))
                        .createdAt(post.getCreatedAt())
                        .build())
                .toList();

        return new PageImpl<>(result, pageable, postsPage.getTotalElements());
    }

    // 글 제목 또는 내용으로 검색
    @Transactional(readOnly = true)
    public List<PostSummaryDto> findByTitleOrContent(String keyword) {
        List<Post> posts = searchRepository.findByTitleOrContetnt(keyword);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostSummaryDto.class))
                .toList();
    }

    // 모든 주제 정보 조회
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
    public List<Post> findBySubTopic(SubTopic subTopic) {
        return searchRepository.findBySubTopic(subTopic);
    }

    // 블로그 제목으로 게시글 조회
    @Transactional(readOnly = true)
    public List<BlogSearchDto> searchByBlogTitle(String blogTitle) {
        List<User> users = userRepository.findByUserInfo_BlogTitleContaining(blogTitle);
        return users.stream()
                .map(user -> BlogSearchDto.builder()
                        .blogTitle(user.getUserInfo().getBlogTitle())
                        .profileIntro(user.getUserInfo().getIntroduceText())
                        .nickname(user.getUserInfo().getNickname())
                        .build())
                .toList();
                }
}