package com.playblog.blogservice.search.entity;

import com.playblog.blogservice.common.entity.SubTopic;
import com.playblog.blogservice.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(name = "published_at", nullable = false)
    private LocalDateTime PublishedAt;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    @ElementCollection
    @CollectionTable(name = "test_post_images", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_urls")
    private List<String> imageUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_topic", nullable = false)
    private SubTopic subTopic;
}