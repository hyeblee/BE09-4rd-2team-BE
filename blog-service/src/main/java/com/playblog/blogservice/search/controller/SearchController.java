package com.playblog.blogservice.search.controller;

import com.playblog.blogservice.common.ApiResponse;
import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.search.dto.AllTopicResponseDto;
import com.playblog.blogservice.search.dto.BlogSearchDto;
import com.playblog.blogservice.search.dto.PostRequest;
import com.playblog.blogservice.search.dto.PostSummaryDto;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class SearchController {
    private final SearchService searchService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createPost(@RequestBody PostRequest request) {
        searchService.createPost(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("게시글 작성 완료!"));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<PostSummaryDto>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDto> posts = searchService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> searchTitleOrContent(
            @RequestParam String keyword) {
        List<PostSummaryDto> results = searchService.findByTitleOrContent(keyword);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BlogSearchDto>>> searchByBlogTitle(
        @RequestParam String blogTitle
    ){
        List<BlogSearchDto> results = searchService.searchByBlogTitle(blogTitle);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<AllTopicResponseDto>>> getAllTopics() {
        List<AllTopicResponseDto> topics = searchService.getAllTopics();
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    @GetMapping("/subtopics")
    public ResponseEntity<ApiResponse<List<Post>>> findBySubTopics(
            @RequestParam SubTopic subTopic
    ){
        List<Post> subTopics = searchService.findBySubTopic(subTopic);
        return ResponseEntity.ok(ApiResponse.success(subTopics));
    }
}
