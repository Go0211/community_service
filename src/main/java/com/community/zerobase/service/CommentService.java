package com.community.zerobase.service;

import com.community.zerobase.dto.CommentDto;
import com.community.zerobase.dto.CommentDto.Request;
import com.community.zerobase.entity.Comment;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.CommentRepository;
import com.community.zerobase.repository.PostRepository;
import com.community.zerobase.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UsersRepository usersRepository;

  public Page<CommentDto.Response> getCommentListUsePost
      (Pageable pageable, Long boardId, Long postId) {
    Post post = getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    return convertPage(commentRepository.findAllByPost(pageable, post));
  }

  @Transactional
  public CommentDto.Response writeComment
      (String email, Long postId, Long boardId, CommentDto.Request request) {
    Post post = getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match board");
    }

    Users users = getUsers(email);

    Comment comment = Comment.builder()
        .users(users)
        .post(post)
        .content(request.getContent())
        .build();

    commentRepository.save(comment);

    return commentToDto(comment);
  }

  @Transactional
  public CommentDto.Response updateComment
      (String email, Long commentId, Long boardId, Request request) {
    Comment comment = getComment(commentId);

    if (!comment.getPost().getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    if (!email.equals(comment.getUsers().getEmail())) {
      throw new MissMatchedException("not match user");
    }

    //dirty checking
    comment.updateComment(request);

    return commentToDto(comment);
  }

  @Transactional
  public void deleteComment(String email, Long commentId, Long boardId) {
    Comment comment = getComment(commentId);

    if (!comment.getPost().getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    if (!email.equals(comment.getUsers().getEmail())) {
      throw new MissMatchedException("not match user");
    }

    commentRepository.delete(comment);
  }

  private Users getUsers(String email) {
    return usersRepository.findByEmail(email)
        .orElseThrow(() -> new NotFoundException("not have user"));
  }

  private Post getPost(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new NotFoundException("not have post"));
  }

  private Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException("not have comment"));
  }

  public Page<CommentDto.Response> convertPage(Page<Comment> commentPage) {
    return commentPage.map(this::commentToDto);
  }

  public CommentDto.Response commentToDto(Comment comment) {
    return CommentDto.Response.commentToDto(comment);
  }
}
