package com.playblog.searchservice.service;

import com.playblog.searchservice.entity.Article;
import com.playblog.searchservice.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
private final ArticleRepository articleRepository;
}
