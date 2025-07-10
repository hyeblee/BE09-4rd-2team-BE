package com.playblog.blogservice.search.controller;

import com.playblog.blogservice.common.ApiResponse;
import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.search.dto.AllTopicResponseDto;
import com.playblog.blogservice.search.dto.BlogSearchDto;
import com.playblog.blogservice.search.dto.PostRequest;
import com.playblog.blogservice.search.dto.PostSummaryDto;
import com.playblog.blogservice.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class SearchController {
    private final SearchService searchService;

    /**
     * 테스트용 게시글 작성 API
     * @param request 게시글 작성 요청 DTO
     * @return ResponseEntity<ApiResponse<String>> 작성 완료 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createPost(@RequestBody PostRequest request) {
        searchService.createPost(request);
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success("게시글 작성 완료!"));
    }

    /**
     * 모든 게시글 조회 API
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity<ApiResponse<Page<PostSummaryDto>>> 게시글 목록
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<PostSummaryDto>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<PostSummaryDto> posts = searchService.getAllPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    /**
     * 게시글 제목 또는 내용 검색 API
     * @param keyword 검색 키워드
     * @return ResponseEntity<ApiResponse<List<PostSummaryDto>>> 검색 결과 목록
     */
    @GetMapping("/search/title")
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> searchTitleOrContent(
            @RequestParam String keyword) {
        List<PostSummaryDto> results = searchService.findByTitleOrContent(keyword);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 블로그 제목 또는 소개글 검색 API
     * @param blogTitle 블로그 제목 또는 프로필 소개 키워드
     * @return ResponseEntity<ApiResponse<List<BlogSearchDto>>> 검색 결과 목록
     */
    @GetMapping("/search/blogtitle")
    public ResponseEntity<ApiResponse<List<BlogSearchDto>>> searchByBlogTitle(
        @RequestParam String blogTitle
    ){
        List<BlogSearchDto> results = searchService.searchByBlogTitleOrProfileIntro(blogTitle);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 블로그 닉네임 또는 블로그 ID 검색 API
     * @param nickname 블로그 닉네임 또는 블로그 ID
     * @return ResponseEntity<ApiResponse<List<BlogSearchDto>>> 검색 결과 목록
     */
    @GetMapping("/search/nickname")
    public ResponseEntity<ApiResponse<List<BlogSearchDto>>> searchByNicknameOrBlogId(
            @RequestParam String nickname
    ){
        List<BlogSearchDto> results = searchService.searchByNicknameOrBlogId(nickname);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 모든 주제 정보 조회 API
     * @return ResponseEntity<ApiResponse<List<AllTopicResponseDto>>> 주제 목록
     */
    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<AllTopicResponseDto>>> getAllTopics() {
        List<AllTopicResponseDto> topics = searchService.getAllTopics();
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    /**
     * 특정 주제에 해당하는 게시글 조회 API
     * @param subTopic 조회할 주제
     * @return ResponseEntity<ApiResponse<List<Post>>> 해당 주제의 게시글 목록
     */
    @GetMapping("/subtopics")
    public ResponseEntity<ApiResponse<List<PostSummaryDto>>> findBySubTopics(
            @RequestParam SubTopic subTopic
    ){
        List<PostSummaryDto> subTopics = searchService.findBySubTopic(subTopic);
        return ResponseEntity.ok(ApiResponse.success(subTopics));
    }

    /**
     * 특정 사용자의 이웃 게시글 조회 API
     * @param userId 사용자 ID
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return ResponseEntity<ApiResponse<Page<PostSummaryDto>>> 이웃 게시글 목록
     */
    @GetMapping("/neighbors")
    public ResponseEntity<ApiResponse<Page<PostSummaryDto>>> getNeighborPosts(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostSummaryDto> neighborPosts = searchService.getNeighborPosts(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(neighborPosts));
    }
}
