package com.playblog.blogservice.postservice.post.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob // TEXT 타입으로 매핑하여 긴 내용을 저장
    @Column(nullable = false)
    private String content;

    @Column(length = 500)
    private String thumbnailImageUrl;
    // 이미지는 content에 저장될 것이므로 별도 엔터티는 필요없고, 썸네일 이미지만 있으면 됨
    // 썸네일 이미지: 파일 업로드 후 반환된 URL을 폼데이터에 포함 → thumbnailImageUrl에 저장.

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private PostVisibility visibility; // 공개 설정 (PUBLIC, PRIVATE)

//    @ElementCollection(fetch = FetchType.EAGER) // 간단한 값들의 컬렉션을 매핑
//    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
//    @Column(name = "tag")
//    private List<String> tags;

    @CreationTimestamp // 엔터티 생성 시 현재 시간 자동 저장
    private LocalDateTime publishedAt;

    @Builder
    public Post(String title, String content, String category, PostVisibility visibility, List<String> tags) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.visibility = visibility;
        // this.tags = tags; // 태그는 게시글 작성 후 사용할 수 있으니 일단 보류
    }
}