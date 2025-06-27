package com.playblog.searchservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "article")
@Getter
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    private String category;
    private String bigCategory;
    private String author;
    private String content;
    private String date;
    private String authorImage;
    private String thumbnail;
}
