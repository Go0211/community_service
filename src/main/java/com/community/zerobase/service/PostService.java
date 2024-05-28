package com.community.zerobase.service;

import com.community.zerobase.dto.PostDto;
import com.community.zerobase.dto.PostDto.Request;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import com.community.zerobase.repository.NoticeBoardRepository;
import com.community.zerobase.repository.PostRepository;
import com.community.zerobase.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
  private final PostRepository postRepository;
  private final UsersRepository usersRepository;
  private final NoticeBoardRepository noticeBoardRepository;

  public Page<PostDto.Response> getPostList(Pageable pageable, String searchText, Long boardId) {
    NoticeBoard noticeBoard = noticeBoardRepository.findById(boardId)
        .orElseThrow(() -> new RuntimeException("error"));

    if (!searchText.isEmpty()) {
      return convertPage(
          postRepository.findByNoticeBoardAndTitleContaining(
              pageable, noticeBoard, searchText));
    }

    return convertPage(
        postRepository.findAllByNoticeBoard(pageable, noticeBoard));
  }

  public PostDto.Response postToResponse(Post post) {
    return PostDto.Response.PostToDto(post);
  }

  Page<PostDto.Response> convertPage(Page<Post> postPage) {
    return postPage.map(this::postToResponse);
  }

  public PostDto.Response getPost(Long postId) {
    return postToResponse(postRepository.findById(postId).orElseThrow(
        () -> new RuntimeException("error")));
  }

  public PostDto.Response writePost(String email, PostDto.Request request) {
    Post post = Post.builder()
        .users(getUsersData(email))
        .noticeBoard(getNoticeBoardData(request.getBoardId()))
        .title(request.getTitle())
        .content(request.getContent())
        .build();

    postRepository.save(post);

    return PostDto.Response.PostToDto(post);
  }

  private NoticeBoard getNoticeBoardData(Long boardId) {
    return noticeBoardRepository.findById(boardId)
        .orElseThrow(() -> new RuntimeException("error"));
  }

  private Users getUsersData(String email) {
    return usersRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("error"));
  }

  public PostDto.Response updatePost(String userName, Long postId, Request request) {
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new RuntimeException("error")
    );

    if (!post.getUsers().getEmail().equals(userName)) {
      throw new RuntimeException("error");
    }

    post.updatePost(request);

    postRepository.save(post);

    return PostDto.Response.PostToDto(post);
  }

  public void deletePost(String userName, Long postId) {
    Post post = postRepository.findById(postId).orElseThrow(
        () -> new RuntimeException("error")
    );

    if (!post.getUsers().getEmail().equals(userName)) {
      throw new RuntimeException("error");
    }

    postRepository.delete(post);
  }
}
