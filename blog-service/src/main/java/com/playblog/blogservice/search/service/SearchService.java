package com.playblog.blogservice.search.service;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.search.dto.AllTopicResponseDto;
import com.playblog.blogservice.search.dto.PostRequest;
import com.playblog.blogservice.search.dto.SearchResponseDto;
import com.playblog.blogservice.search.dto.SubTopicResponseDto;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    private final ModelMapper modelMapper;

    public void createPost(PostRequest request) {
        Post post = modelMapper.map(request, Post.class);
        searchRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<Post> getAllPosts() {
        return searchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<SearchResponseDto> findByTitleOrContent(String keyword) {
        List<Post> posts = searchRepository.findByTitleOrContetnt(keyword);
        return posts.stream()
                .map(post -> modelMapper.map(post, SearchResponseDto.class))
                .toList();
    }

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

}