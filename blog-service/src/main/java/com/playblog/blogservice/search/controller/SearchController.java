package com.playblog.blogservice.search.controller;

import com.playblog.blogservice.common.ApiResponse;
import com.playblog.blogservice.search.dto.PostRequest;
import com.playblog.blogservice.search.entity.Post;
import com.playblog.blogservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<List<Post>>> getAllPosts() {
        List<Post> posts = searchService.getAllPosts();
        return ResponseEntity
                .ok(ApiResponse.success(posts));
    }
}
