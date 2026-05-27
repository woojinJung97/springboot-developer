package com.backend.springbootdeveloper.service;

import com.backend.springbootdeveloper.config.auth.CustomUserDetails;
import com.backend.springbootdeveloper.domain.post.Comment;
import com.backend.springbootdeveloper.dto.*;
import com.backend.springbootdeveloper.mapper.PostsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostsMapper postsMapper;

    // 게시글 목록 반환 — 페이지네이션·키워드 검색 조건 적용
    public List<PostsResponseDto> getPostsList(PostsRequestDto dto) {
        return postsMapper.getPostsList(dto);
    }

    // 로그인 유저 ID를 게시글 DTO에 설정 후 저장
    public void createPosts(CustomUserDetails user, PostsResponseDto dto) {
        dto.setUserId(user.getUserId());
        postsMapper.createPosts(dto);
    }

    // 조회수 1 증가 후 게시글 상세 정보 반환
    public PostsResponseDto getPostsDetails(Long postId) {
        postsMapper.increaseViewCount(postId);
        return postsMapper.getPostsDetails(postId);
    }

    // 댓글 저장 + 해당 게시글 댓글 수 1 증가 (트랜잭션으로 묶음)
    @Transactional
    public CommentResponseDto createComment(CustomUserDetails user, Long postId, CommentRequestDto dto) {
        Comment comment = new Comment();
        comment.setUserId(user.getUserId());
        comment.setPostId(postId);
        comment.setContent(dto.getContent());
        comment.setCreatedAt(new Date());

        postsMapper.createComment(comment);
        postsMapper.increaseComment(postId);

        CommentResponseDto result = new CommentResponseDto();
        result.setCommentId(comment.getCommentId());
        result.setUserId(comment.getUserId());
        result.setPostId(comment.getPostId());
        result.setContent(comment.getContent());
        result.setCreatedAt(comment.getCreatedAt());

        return result;
    }

    // 특정 게시글의 댓글 목록 반환 (최신순)
    public List<CommentResponseDto> getComment(Long postId) {
        return postsMapper.getComment(postId);
    }

    public LikesResponseDto getLikes(CustomUserDetails user, Long postId) {
        // 현재 좋아요 정보 조회
        LikesResponseDto dto = new LikesResponseDto();
        dto.setUserId(user.getUserId());
        dto.setPostId(postId);

        return postsMapper.getLikes(dto);
    }
}
