package com.playblog.blogservice.search.service;

import com.playblog.blogservice.search.dto.PostRequest;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}