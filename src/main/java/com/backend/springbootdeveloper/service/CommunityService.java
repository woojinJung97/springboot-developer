package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.PostsRequestDto;
import com.backend.springbootdeveloper.dto.PostsResponseDto;
import com.backend.springbootdeveloper.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostsMapper postsMapper;

    public List<PostsResponseDto> getPostsList(PostsRequestDto dto) {
        List<PostsResponseDto> result = postsMapper.getPostsList(dto);

        return result;
    }

    public void createPosts(CustomUserDetails user, PostsResponseDto dto) {
        dto.setUserId(user.getUserId());

        postsMapper.createPosts(dto);

    }

    public PostsResponseDto getPostsDetails(Long postId) {
        PostsResponseDto result = postsMapper.getPostsDetails(postId);

        return result;

    }
}
