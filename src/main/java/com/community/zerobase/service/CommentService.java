package com.community.zerobase.service;

import com.community.zerobase.converter.ModelToObjectConverter;
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
  private final CommonService commonService;
  private final ModelToObjectConverter converter;

  private final CommentRepository commentRepository;

  public Page<CommentDto.Response> getCommentListUsePost
      (Pageable pageable, Long boardId, Long postId) {
    Post post = commonService.getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    return converter.commentToResponseConvertPage
        (commentRepository.findAllByPost(pageable, post));
  }

  @Transactional
  public CommentDto.Response writeComment
      (Long postId, Long boardId, CommentDto.Request request) {
    Users users
        = commonService.getUsers(commonService.getUserEmail());

    Post post = commonService.getPost(postId);

    if (!post.getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match board");
    }

    Comment comment = Comment.builder()
        .users(users)
        .post(post)
        .content(request.getContent())
        .build();

    commentRepository.save(comment);

    return converter.commentToDto(comment);
  }

  @Transactional
  public CommentDto.Response updateComment
      (Long commentId, Long boardId, Request request) {
    Users users
        = commonService.getUsers(commonService.getUserEmail());

    Comment comment = commonService.getComment(commentId);

    if (!comment.getPost().getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    if (!users.equals(comment.getUsers())) {
      throw new MissMatchedException("not match user");
    }

    //dirty checking
    comment.updateComment(request);

    return converter.commentToDto(comment);
  }

  @Transactional
  public void deleteComment(Long commentId, Long boardId) {
    Users users
        = commonService.getUsers(commonService.getUserEmail());

    Comment comment = commonService.getComment(commentId);

    if (!comment.getPost().getNoticeBoard().getId().equals(boardId)) {
      throw new MissMatchedException("not match post");
    }

    if (!users.equals(comment.getUsers())) {
      throw new MissMatchedException("not match user");
    }

    commentRepository.delete(comment);
  }
}
