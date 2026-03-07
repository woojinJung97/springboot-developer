package com.backend.springbootdeveloper.mapper;

import com.backend.springbootdeveloper.dto.PostsRequestDto;
import com.backend.springbootdeveloper.dto.PostsResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostsMapper {


    List<PostsResponseDto> getPostsList(PostsRequestDto dto);

    void createPosts(PostsResponseDto dto);

    PostsResponseDto getPostsDetails(Long postId);
}
