package com.community.zerobase.controller;

import com.community.zerobase.dto.CommentDto;
import com.community.zerobase.service.CommentService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/comment")
public class CommentController {
  private final CommentService commentService;

  @GetMapping("/list")
  public ResponseEntity<?> getCommentList(
      @RequestParam(name = "postId") Long postId,
      @PathVariable(name = "boardId") Long boardId,
      @PageableDefault(
          page = 0, size = 10, sort = "writeDate",
          direction = Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(commentService.getCommentListUsePost(pageable, boardId, postId));
  }

  @PostMapping
  public ResponseEntity<?> writeComment(
      @RequestParam(name = "postId") Long postId,
      @PathVariable(name = "boardId") Long boardId,
      @RequestBody CommentDto.Request request
  ) {
    return ResponseEntity.ok(
        commentService.writeComment(postId, boardId, request));
  }

  @PutMapping
  public ResponseEntity<?> updateComment(
      @RequestParam(name = "commentId") Long commentId,
      @PathVariable(name = "boardId") Long boardId,
      @RequestBody CommentDto.Request request
  ) {
    return ResponseEntity.ok(
        commentService.updateComment(commentId, boardId, request));
  }

  @DeleteMapping
  public ResponseEntity<?> deleteComment(
      @RequestParam(name = "commentId") Long commentId,
      @PathVariable(name = "boardId") Long boardId
  ) {
    commentService.deleteComment(commentId, boardId);
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
