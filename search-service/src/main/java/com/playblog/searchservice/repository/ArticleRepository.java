package com.playblog.searchservice.repository;

import com.playblog.searchservice.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository implements JpaRepository<Article, Long> {
    @Override
    List<Article> searchByKeyword(@Param("keyword") String keyword);
}
