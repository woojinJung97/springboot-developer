package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.dto.CommentRequestDto;
import com.backend.springbootdeveloper.dto.CommentResponseDto;
import com.backend.springbootdeveloper.dto.PostsRequestDto;
import com.backend.springbootdeveloper.dto.PostsResponseDto;
import com.backend.springbootdeveloper.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
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
        // 조회수 증가
        postsMapper.increaseViewCount(postId);

        PostsResponseDto result = postsMapper.getPostsDetails(postId);

        return result;

    }

    public CommentResponseDto createComment(CustomUserDetails user, CommentRequestDto dto) {
        dto.setUserId(user.getUserId());

        postsMapper.createComment(dto);

        // 프론트엔드에 보낼 dto
        CommentResponseDto result = new CommentResponseDto();
        result.setCommentId(dto.getCommentId());
        result.setUserId(dto.getUserId());
        result.setPostId(dto.getPostId());
        result.setContent(dto.getContent());
        result.setCreatedAt(new Date());

        return result;
    }

    public List<CommentResponseDto> getComment(Long postId) {

        return postsMapper.getComment(postId);
    }
}
