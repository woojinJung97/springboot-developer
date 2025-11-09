package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.domain.Article;
import com.backend.springbootdeveloper.dto.AddArticleRequest;
import com.backend.springbootdeveloper.dto.UpdateArticleRequest;
import com.backend.springbootdeveloper.mapper.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    // 글 저장
    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    // 전체 조회
    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    // 글 조회
    public Article findById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("id not found" + id));
    }

    // 글 삭제
    public void delete(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(Long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found" + id));

        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
