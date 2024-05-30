package com.community.zerobase.service;

import com.community.zerobase.dto.PostDto;
import com.community.zerobase.dto.PostDto.Request;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.NoticeBoardRepository;
import com.community.zerobase.repository.PostRepository;
import com.community.zerobase.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final UsersRepository usersRepository;
  private final NoticeBoardRepository noticeBoardRepository;

  public Page<PostDto.Response> getPostList(Pageable pageable, String searchText, Long boardId) {
    NoticeBoard noticeBoard = getNoticeBoard(boardId);

    if (!searchText.isEmpty()) {
      return convertPage(
          postRepository.findByNoticeBoardAndTitleContaining(
              pageable, noticeBoard, searchText));
    }

    return convertPage(
        postRepository.findAllByNoticeBoard(pageable, noticeBoard));
  }

  @Transactional
  public PostDto.Response writePost(String email, Long boardId, PostDto.Request request) {
    Post post = Post.builder()
        .users(getUsers(email))
        .noticeBoard(getNoticeBoard(boardId))
        .title(request.getTitle())
        .content(request.getContent())
        .build();

    postRepository.save(post);

    return PostDto.Response.PostToDto(post);
  }

  @Transactional
  public PostDto.Response updatePost
      (String userName, Long postId, Long boardId, Request request) {
    Post post = getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match board");
    }

    if (!post.getUsers().getEmail().equals(userName)) {
      throw new MissMatchedException("not match user");
    }

    //dirty checking
    post.updatePost(request);

    return PostDto.Response.PostToDto(post);
  }

  @Transactional
  public void deletePost(String userName, Long postId, Long boardId) {
    Post post = getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match board");
    }

    if (!post.getUsers().getEmail().equals(userName)) {
      throw new MissMatchedException("not match user");
    }

    postRepository.delete(post);
  }

  public PostDto.Response getPostData(Long boardId, Long postId) {
    Post post = getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match board");
    }

    return postToResponse(post);
  }

  private NoticeBoard getNoticeBoard(Long boardId) {
    return noticeBoardRepository.findById(boardId)
        .orElseThrow(() -> new NotFoundException("not have board"));
  }

  private Users getUsers(String email) {
    return usersRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("not have user"));
  }

  private Post getPost(Long postId) {
    return postRepository.findById(postId).orElseThrow(
        () -> new NotFoundException("not have post"));
  }

  public PostDto.Response postToResponse(Post post) {
    return PostDto.Response.PostToDto(post);
  }

  Page<PostDto.Response> convertPage(Page<Post> postPage) {
    return postPage.map(this::postToResponse);
  }
}
