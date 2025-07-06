package com.playblog.blogservice.postservice.post.dto;

import com.playblog.blogservice.postservice.post.entity.Post;
import com.playblog.blogservice.postservice.post.entity.PostVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostRequestDto {

    // Validation으로 안정성 확보 (NotBlank, NotNull, NotEmpty)
    // null 값이나 비어 있는 문자열("") 등 원치 않는 데이터가 데이터베이스에 저장되는 것을 막아 데이터의 일관성과 품질을 유지
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;

    private String category;

    @NotNull(message = "공개 설정은 필수입니다.")
    private PostVisibility visibility;

    // DTO를 Entity로 변환하는 메서드
    public Post toEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .category(category)
                .visibility(visibility)
                .build();
    }
}