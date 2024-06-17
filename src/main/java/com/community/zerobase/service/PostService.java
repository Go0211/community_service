package com.community.zerobase.service;

import com.community.zerobase.converter.ModelToObjectConverter;
import com.community.zerobase.dto.PostDto;
import com.community.zerobase.dto.PostDto.Request;
import com.community.zerobase.dto.TemporaryDto;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.TemporaryPost;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NotAllowInputValueException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.community.zerobase.repository.PostRepository;
import com.community.zerobase.repository.RedisRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
  private final RedissonClient redissonClient;
  private final CommonService commonService;
  private final ModelToObjectConverter converter;

  private final PostRepository postRepository;
  private final RedisRepository redisRepository;

  public PostDto.Response getPostData(Long boardId, Long postId) {
    Post post = commonService.getPost(postId);
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    if (!post.getNoticeBoard().getId().equals(noticeBoard.getId())) {
      throw new MissMatchedException("not match board");
    }

    upViewCount(postId);

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

  @Transactional
  public void saveTemporaryPost(TemporaryDto.Request request, Long boardId) {
    TemporaryPost temporaryPost = TemporaryPost.toTemporaryPost(
        commonService.getUsers(commonService.getUserEmail()).getId(),
        boardId,
        request
    );

    redisRepository.save(temporaryPost);
  }

  public TemporaryPost getTemporaryPost(TemporaryDto.Request request, Long boardId) {
    String id = setTemporaryPostId(request, boardId);

    return redisRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("not found temporary post"));
  }

  @Transactional
  public void deleteTemporaryPost(TemporaryDto.Request request, Long boardId) {
    String id = setTemporaryPostId(request, boardId);

    redisRepository.deleteById(id);
  }

  private void upViewCount(Long postId) {
    String lockKey = "post:viewCount:lock:"+ postId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 10초 동안 락을 시도하며, 락이 걸리면 1초 동안 유지
      if (lock.tryLock(10, 1, TimeUnit.SECONDS)) {
        try {
          String redisKey = "post:viewCount:"+ postId;
          Integer viewsCount = (Integer) redissonClient.getBucket(redisKey).get();
          viewsCount = (viewsCount == null) ? 1 : viewsCount + 1;
          redissonClient.getBucket(redisKey).set(viewsCount);

          // Optional: MySQL 동기화
          Post post = postRepository.findById(postId)
              .orElseThrow(() -> new RuntimeException("Post not found"));
          post.setViews(viewsCount);
          postRepository.save(post);
        } finally {
          lock.unlock();
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void upLikesCount(Long postId) {
    String lockKey = "post:likesCount:lock:"+ postId;
    RLock lock = redissonClient.getLock(lockKey);

    try {
      // 10초 동안 락을 시도하며, 락이 걸리면 1초 동안 유지
      if (lock.tryLock(10, 1, TimeUnit.SECONDS)) {
        try {
          String redisKey = "post:likesCount:" + postId;
          Integer likesCount = (Integer) redissonClient.getBucket(redisKey).get();
          likesCount = (likesCount == null) ? 1 : likesCount + 1;
          redissonClient.getBucket(redisKey).set(likesCount);

          // Optional: MySQL 동기화
          Post post = postRepository.findById(postId)
              .orElseThrow(() -> new RuntimeException("Post not found"));
          post.setLikes(likesCount);
          postRepository.save(post);
        } finally {
          lock.unlock();
        }
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // 주기적으로 Redis 데이터를 MySQL로 동기화하는 메서드
  @Scheduled(fixedRate = 60000) // 1분마다 동기화
  public void syncCountsToDatabase() {
    Iterable<String> keysIterable = redissonClient.getKeys().getKeysByPattern("post:*Count:*");
    Set<String> keys = new HashSet<>();
    keysIterable.forEach(keys::add);

    for (String key : keys) {
      Long postId = Long.parseLong(key.split(":")[2]);
      Integer count = (Integer) redissonClient.getBucket(key).get();

      Post post = postRepository.findById(postId)
          .orElseThrow(() -> new RuntimeException("Post not found"));
      if (key.contains("viewCount")) {
        post.setViews(count);
      } else if (key.contains("likesCount")) {
        post.setLikes(count);
      }
      postRepository.save(post);
    }
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

  private String setTemporaryPostId(TemporaryDto.Request request, Long boardId) {
    return commonService.getUsers(commonService.getUserEmail()).getId()
        + "_" + boardId + "_" + request.getTitle();

  }

  public Integer getViewCount(Long postId) {
    String redisKey = "post:viewCount:" + postId;
    return (Integer) redissonClient.getBucket(redisKey).get();
  }

  public Integer getLikesCount(Long postId) {
    String redisKey = "post:likesCount:" + postId;
    return (Integer) redissonClient.getBucket(redisKey).get();
  }
}
