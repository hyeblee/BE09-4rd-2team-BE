package com.playblog.blogservice.post.entity;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.common.entity.TopicType;
import com.playblog.blogservice.post.dto.PostRequestDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import com.playblog.blogservice.user.User;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    /* 유저 참조 */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* 아이디 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* 제목 */
    @Column(nullable = false, length = 200)
    private String title;

    /* 내용 */
    @Lob // TEXT 타입으로 매핑하여 긴 내용을 저장
    @Column(nullable = false)
    private String content;

    /* 썸네일 이미지 */
    // 이미지는 content에 저장될 것이므로 별도 엔터티는 필요없고, 썸네일 이미지만 있으면 됨
    // 썸네일 이미지: 파일 업로드 후 반환된 URL을 폼데이터에 포함 → thumbnailImageUrl에 저장.
    @Column(length = 500)
    private String thumbnailImageUrl;

    /* 카테고리 */
    private String category;

    /* 메인 주제 */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TopicType mainTopic;

    /* 하위(서브) 주제 */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubTopic subTopic;

    /* 공개 설정 */
    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 문자열로 저장
    @Column(nullable = false)
    private PostVisibility visibility; // 공개 설정 (PUBLIC, PRIVATE)

    /* 발행 시각 */
    @CreationTimestamp // 엔터티 생성 시 현재 시간 자동 저장
    private LocalDateTime publishedAt;

//    /* 태그 */
//    @ElementCollection(fetch = FetchType.EAGER) // 간단한 값들의 컬렉션을 매핑
//    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
//    @Column(name = "tag")
//    private List<String> tags;

    /* 빌더 */
    @Builder
    public Post(
            String title,
            String content,
            String thumbnailImageUrl,
            String category,
            TopicType mainTopic,
            SubTopic subTopic,
            PostVisibility visibility,
            User user,
            LocalDateTime publishedAt
//            List<String> tags
    ) {
        this.title = title;
        this.content = content;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.category = category;
        this.mainTopic = mainTopic;
        this.subTopic = subTopic;
        this.visibility = visibility;
        this.user = user;
        this.publishedAt = publishedAt;
        // this.tags = tags;
    }

    public void update(@NotBlank String title, @NotBlank String content, @NotNull PostVisibility visibility, Boolean allowComment, Boolean allowLike, Boolean allowSearch, String thumbnailImageUrl, @NotNull TopicType mainTopic, @NotNull SubTopic subTopic) {
    }

    public void update(PostRequestDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.thumbnailImageUrl = dto.getThumbnailImageUrl();
        this.mainTopic = dto.getMainTopic();
        this.subTopic = dto.getSubTopic();
//        this.updatedAt = LocalDateTime.now(); 작성일시로 기본 고정
    }
}