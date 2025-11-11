package com.backend.springbootdeveloper.controller;

import com.backend.springbootdeveloper.domain.Article;
import com.backend.springbootdeveloper.dto.AddArticleRequest;
import com.backend.springbootdeveloper.dto.UpdateArticleRequest;
import com.backend.springbootdeveloper.mapper.BlogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
        blogRepository.deleteAll();
    }

    @DisplayName("addArticle: 블로그 글 추가에 성공한다.")
    @Test
    public void addArticle()throws Exception {
        // Given 블로그 글 추가에 필요한 요청 객체 생성
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        // 객체 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // when 블로그 글 추가 API에 요청
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then 응답코드 확인
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1);
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
    }

    @DisplayName("findAllArticles: 블로그 글 목록 조회에 성공한다.")
    @Test
    public void findAllArticles()throws Exception {
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";

        blogRepository.save(Article.builder().title(title).content(content).build());

        final ResultActions resultActions = mockMvc.perform(get(url).accept(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));
    }

    @DisplayName("findArticle: 블로그 글 조회에 성공한다.")
    @Test
    public void findArticle()throws Exception {
        final String url = "/api/article/{id}";
        final String title = "title";
        final String content = "content";

        Article saveArticle = blogRepository.save(Article.builder().title(title).content(content).build());

        final ResultActions resultActions = mockMvc.perform(get(url, saveArticle.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제에 성공한다.")
    @Test
    public void deleteArticle()throws Exception {
        final String url = "/api/article/{id}";
        final String title = "title";
        final String content = "content";

        Article saveArticle = blogRepository.save(Article.builder().title(title).content(content).build());

        mockMvc.perform(delete(url, saveArticle.getId())).andExpect(status().isOk());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정에 성공한다.")
    @Test
    public void updateArticle()throws Exception {
        final String url = "/api/articles/{id}";
        final String title = "title";
        final String content = "content";

        Article saveArticle = blogRepository.save(Article.builder().title(title).content(content).build());

        final String newTitle = "newTitle";
        final String newContent = "newContent";

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        ResultActions result = mockMvc.perform(put(url, saveArticle.getId()).contentType(MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(request)));

        result.andExpect(status().isOk());

        Article article = blogRepository.findById(saveArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

}