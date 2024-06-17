package com.community.zerobase.controller;

import static com.community.zerobase.dto.TemporaryDto.Request;

import com.community.zerobase.dto.PostDto;
import com.community.zerobase.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/board/{boardId}/post")
@RestController
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;

  @GetMapping("/list")
  public ResponseEntity<?> getPostList(
      @RequestParam(name = "type", defaultValue = "title") String type,
      @RequestParam(name = "searchText", defaultValue = "") String searchText,
      @PathVariable(name = "boardId") Long boardId,
      @PageableDefault(
          page = 0, size = 10, sort = "writeDate",
          direction = Direction.DESC) Pageable pageable
  ) {
    return ResponseEntity.ok(
        postService.getPostList(pageable, type, searchText, boardId));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<?> getPost(
      @PathVariable(name = "postId") Long postId,
      @PathVariable(name = "boardId") Long boardId
  ) {
    return ResponseEntity.ok(
        postService.getPostData(boardId, postId));
  }

  @PostMapping
  public ResponseEntity<?> writePost(
      @Valid @RequestBody PostDto.Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    return ResponseEntity.ok(
        postService.writePost(boardId, request));
  }

  @PutMapping
  public ResponseEntity<?> updatePost(
      @Valid @RequestBody PostDto.Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    return ResponseEntity.ok(
        postService.updatePost(boardId, request));
  }

  @DeleteMapping
  public ResponseEntity<?> deletePost(
      @Valid @RequestBody PostDto.Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    postService.deletePost(request.getPostId(), boardId);

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @GetMapping("/temporary")
  public ResponseEntity<?> getTemporaryPost(
      @Valid @RequestBody Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    return ResponseEntity.ok(
        postService.getTemporaryPost(request, boardId));
  }

  @PostMapping("/temporary")
  public ResponseEntity<?> setTemporaryPost(
      @Valid @RequestBody Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    postService.saveTemporaryPost(request, boardId);

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @DeleteMapping("/temporary")
  public ResponseEntity<?> deleteTemporaryPost(
      @Valid @RequestBody Request request,
      @PathVariable(name = "boardId") Long boardId
  ) {
    postService.deleteTemporaryPost(request, boardId);

    return ResponseEntity.ok(HttpStatus.OK);
  }

  @PutMapping("/{postId}/likes/up")
  public ResponseEntity<?> upLikes(
      @PathVariable(name = "postId") Long postId,
      @PathVariable(name = "boardId") Long boardId
  ) {
    postService.upLikesCount(postId);

    return ResponseEntity.ok(HttpStatus.OK);
  }
}
