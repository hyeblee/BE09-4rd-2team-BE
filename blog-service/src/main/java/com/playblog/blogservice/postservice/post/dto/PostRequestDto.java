package com.playblog.blogservice.postservice.post.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.postservice.post.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {

    /**
     * 게시글 제목 (필수)
     * NotBlank: null, "", " " 모두 허용 안 함
     */
    @NotBlank
    private String title;

    /**
     * 게시글 내용 (필수)
     * 긴 내용 허용
     */
    @NotBlank
    private String content;

    /**
     * 썸네일 이미지 URL (선택)
     * 파일 업로드 후 Service 단에서 URL로 세팅
     */
    private String thumbnailImageUrl;

    /**
     * 카테고리명 (게시글로 고정)
     * 추가로 mainTopic/subTopic과 함께 사용
     */
    private String category;

    /**
     * 메인 주제 (필수)
     * Enum: ENTERTAINMENT_ARTS, LIFESTYLE 등
     */
    private TopicType topicType;

    /**
     * 서브 주제 (필수)
     * Enum: LITERATURE_BOOK, MOVIE 등
     */
    private SubTopic subTopic;

    /**
     * 공개 여부 (필수)
     * Enum: PUBLIC, PRIVATE
     */
    @NotNull
    private PostVisibility visibility;

    /**
     * 댓글 허용 여부 (선택)
     * PostPolicy에 전달됨
     */
    private Boolean allowComment;

    /**
     * 좋아요 허용 여부 (선택)
     * PostPolicy에 전달됨
     */
    private Boolean allowLike;

    /**
     * 검색 허용 여부 (선택)
     * PostPolicy에 전달됨
     */
    private Boolean allowSearch;

    // 필요시 태그 추가
    // private List<String> tags;

    /**
     * PostRequestDto → Post Entity 변환
     * 빌더 사용: 엔티티와 동일한 필드만 매핑
     */
    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .thumbnailImageUrl(thumbnailImageUrl)
                .category(category)
                .topicType(topicType)
                .subTopic(subTopic)
                .visibility(visibility)
                // .tags(tags)
                .build();
    }

    /**
     * PostRequestDto → PostPolicy Entity 변환
     * 연관된 Post Entity 전달 필수
     */
    public PostPolicy toPolicyEntity(Post post) {
        return PostPolicy.builder()
                .post(post)
                .allowComment(allowComment)
                .allowLike(allowLike)
                .allowSearch(allowSearch)
                .build();
    }
}
