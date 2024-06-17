package com.community.zerobase.service;

import com.community.zerobase.converter.ModelToObjectConverter;
import com.community.zerobase.dto.PostDto;
import com.community.zerobase.dto.PostDto.Request;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NotAllowInputValueException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.community.zerobase.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  private final CommonService commonService;
  private final ModelToObjectConverter converter;

  private final PostRepository postRepository;

  public PostDto.Response getPostData(Long boardId, Long postId) {
    Post post = commonService.getPost(postId);
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    if (!post.getNoticeBoard().getId().equals(noticeBoard.getId())) {
      throw new MissMatchedException("not match board");
    }

    return converter.postToResponse(post);
  }

  public Page<PostDto.Response> getPostList(
      Pageable pageable,
      String type,
      String searchText,
      Long boardId
  ) {
    List<Users> usersList = commonService.getUsersList(searchText);
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    return converter.postToResponseConvertPage
        (getPostPage(pageable, type, noticeBoard, searchText, usersList));
  }

  @Transactional
  public PostDto.Response writePost(
      Long boardId,
      PostDto.Request request
  ) {
    Users users = commonService.getUsers(commonService.getUserEmail());

    Post post = Post.builder()
        .users(users)
        .noticeBoard(commonService.getNoticeBoard(boardId))
        .title(request.getTitle())
        .content(request.getContent())
        .build();

    postRepository.save(post);

    return PostDto.Response.PostToDto(post);
  }

  @Transactional
  public PostDto.Response updatePost(
      Long boardId,
      Request request
  ) {
    Users users = commonService.getUsers(commonService.getUserEmail());
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);
    List<String> managerEmailList
        = converter.managerToStringConvertList(commonService.getManagerList(boardId));


    if (request.getPostId() == null) {
      throw new NullException("post not blank");
    }

    Post post = commonService.getPost(request.getPostId());

    if (!post.getNoticeBoard().getId().equals(noticeBoard.getId())) {
      throw new MissMatchedException("not match board");
    }

    if (!post.getUsers().getEmail().equals(users.getEmail())
        && !managerEmailList.contains(post.getUsers().getEmail())) {
      System.out.println(managerEmailList);
      System.out.println(post.getUsers().getEmail());
      System.out.println(!managerEmailList.contains(post.getUsers().getEmail()));
      throw new MissMatchedException("not match user or manager");
    }

    //dirty checking
    post.updatePost(request);

    return PostDto.Response.PostToDto(post);
  }

  @Transactional
  public void deletePost(
      Long postId,
      Long boardId
  ) {
    Users users = commonService.getUsers(commonService.getUserEmail());
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);
    List<String> managerEmailList
        = converter.managerToStringConvertList(commonService.getManagerList(boardId));

    Post post = commonService.getPost(postId);

    if (!post.getNoticeBoard().getId().equals(noticeBoard.getId())) {
      throw new MissMatchedException("not match board");
    }

    if (!post.getUsers().getEmail().equals(users.getEmail())
        && !managerEmailList.contains(post.getUsers().getEmail())) {
      throw new MissMatchedException("not match user or manager");
    }

    postRepository.delete(post);
  }

  private Page<Post> getPostPage(
      Pageable pageable,
      String type,
      NoticeBoard noticeBoard,
      String searchText,
      List<Users> usersList) {
    if (!(searchText == null || searchText.isEmpty())) {
      return
          switch (type) {
            case "title" -> postRepository
                .findByNoticeBoardAndTitleContaining
                    (pageable, noticeBoard, searchText);
            case "content" -> postRepository
                .findAllByNoticeBoardAndContentContaining
                    (pageable, noticeBoard, searchText);
            default -> throw new NotAllowInputValueException("not allow input type");
          };
    }

    if (type.equals("user")) {
      return postRepository
          .findAllByNoticeBoardAndUsersIn
              (pageable, noticeBoard, usersList);
    }

    return postRepository.findAllByNoticeBoard(pageable, noticeBoard);
  }
}
