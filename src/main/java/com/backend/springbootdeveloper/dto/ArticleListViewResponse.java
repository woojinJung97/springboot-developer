package com.backend.springbootdeveloper.dto;

import com.backend.springbootdeveloper.domain.Article;
import lombok.Getter;

import java.util.List;

@Getter
public class ArticleListViewResponse {

    private final Long id;
    private final String title;
    private final String content;

    public ArticleListViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
    }

}
