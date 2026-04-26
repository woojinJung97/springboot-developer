package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.post.Comment;
import com.backend.springbootdeveloper.dto.CommentRequestDto;
import com.backend.springbootdeveloper.dto.CommentResponseDto;
import com.backend.springbootdeveloper.dto.PostsRequestDto;
import com.backend.springbootdeveloper.dto.PostsResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostsMapper {


    List<PostsResponseDto> getPostsList(PostsRequestDto dto);

    void createPosts(PostsResponseDto dto);

    PostsResponseDto getPostsDetails(Long postId);

    void increaseViewCount(Long postId);

    int createComment(Comment comment);

    List<CommentResponseDto> getComment(Long postId);

    void increaseComment(Long postId);
}
