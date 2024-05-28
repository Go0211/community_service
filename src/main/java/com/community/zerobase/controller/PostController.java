package com.community.zerobase.controller;

import com.community.zerobase.dto.PostDto;
import com.community.zerobase.service.AuthService;
import com.community.zerobase.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/post")
@RestController
@RequiredArgsConstructor
public class PostController {
  private final PostService postService;
  private final AuthService authService;

  @GetMapping("")
  public ResponseEntity<?> getPost(
      @RequestParam(name = "postId") Long postId) {
    return ResponseEntity.ok(
        // return 값 변경
        postService.getPost(postId));
  }

  @PostMapping("/write")
  public ResponseEntity<?> writePost(
      @Valid @RequestBody PostDto.Request request) {
    return ResponseEntity.ok(
        postService.writePost(authService.getUserName(), request));
  }

  @PostMapping("/update")
  public ResponseEntity<?> updatePost(
      @Valid @RequestBody PostDto.Request request,
      @RequestParam(name = "postId") Long postId) {
    return ResponseEntity.ok(
        postService.updatePost(authService.getUserName(), postId, request));
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deletePost(
      @RequestParam(name = "postId") Long postId) {
    postService.deletePost(authService.getUserName(), postId);
    return ResponseEntity.ok(HttpStatus.OK);
  }
}
