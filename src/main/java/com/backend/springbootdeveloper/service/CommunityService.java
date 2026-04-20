package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.post.Comment;
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

    public CommentResponseDto   createComment(CustomUserDetails user,Long postId, CommentRequestDto dto) {
        // Entity 생성
        Comment comment = new Comment();
        comment.setUserId(user.getUserId());
        comment.setPostId(postId);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(new Date());

        // DB 저장
        postsMapper.createComment(comment);

        // 응답 DTO 생성
        CommentResponseDto result = new CommentResponseDto();
        result.setCommentId(comment.getCommentId());
        result.setUserId(comment.getUserId());
        result.setPostId(comment.getPostId());
        result.setContent(comment.getContent());
        result.setCreatedAt(comment.getCreatedAt());

        return result;
    }

    public List<CommentResponseDto> getComment(Long postId) {

        return postsMapper.getComment(postId);
    }
}
