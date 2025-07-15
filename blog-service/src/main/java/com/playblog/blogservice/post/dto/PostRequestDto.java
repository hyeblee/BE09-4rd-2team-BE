package com.playblog.blogservice.post.dto;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.post.entity.Post;
import com.playblog.blogservice.post.entity.PostPolicy;
import com.playblog.blogservice.post.entity.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.playblog.blogservice.user.User;

@Getter
@Setter
@NoArgsConstructor // 역직렬화 구조에 필요해서 (Jackson이 필요로 함)
public class PostRequestDto {

    // 게시글 정보
    private Long userId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String thumbnailImageUrl;
    private String category;

    @NotNull
    private TopicType mainTopic;

    @NotNull
    private SubTopic subTopic;

    @NotNull
    private PostVisibility visibility;

    // 정책 정보
    private Boolean allowComment;
    private Boolean allowLike;
    private Boolean allowSearch;

    // 필요시 태그 추가
    // private List<String> tags;

    // (thumbnail URL이나 파일은 MultipartFile로 컨트롤러에서 분리)


    /**
     * PostRequestDto → Post Entity 변환
     * 빌더 사용: 엔티티와 동일한 필드만 매핑
     */
    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .thumbnailImageUrl(thumbnailImageUrl)
                .category(category)
                .mainTopic(mainTopic)
                .subTopic(subTopic)
                .visibility(visibility)
                .user(user)
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
