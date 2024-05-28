package com.community.zerobase.controller;

import com.community.zerobase.service.AuthService;
import com.community.zerobase.service.NoticeBoardService;
import com.community.zerobase.service.PostService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class NoticeBoardController {
  private final NoticeBoardService noticeBoardService;
  private final PostService postService;

  @PostMapping("/create")
  public ResponseEntity<?> noticeBoardCreate(@RequestBody Map<String, String> noticeBoardName) {
    try {
      return ResponseEntity.ok(
          noticeBoardService.createBoard(noticeBoardName.get("name")));
    } catch (IllegalAccessException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("")
  public ResponseEntity<?> getNoticeBoardList(
      @RequestParam(name = "searchText", defaultValue = "") String searchText,
      @PageableDefault(
          page = 0, size = 10, sort = "createDate",
          direction = Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(noticeBoardService.getAllBoard(pageable, searchText));
  }

  @GetMapping("/list")
  public ResponseEntity<?> getNoticeBoard(
      @RequestParam(name = "searchText", defaultValue = "") String searchText,
      @RequestParam(name = "boardId") Long boardId,
      @PageableDefault(
          page = 0, size = 10, sort = "writeDate",
          direction = Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(postService.getPostList(pageable, searchText, boardId));
  }
}
