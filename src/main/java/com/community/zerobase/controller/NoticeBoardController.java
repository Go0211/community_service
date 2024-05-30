package com.community.zerobase.controller;

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

  @PostMapping("")
  public ResponseEntity<?> createNoticeBoard(@RequestBody Map<String, String> noticeBoardName) {
      return ResponseEntity.ok(
          noticeBoardService.createBoard(noticeBoardName.get("name")));
  }

  @GetMapping("/list")
  public ResponseEntity<?> getNoticeBoardList(
      @RequestParam(name = "searchText", defaultValue = "") String searchText,
      @PageableDefault(
          page = 0, size = 10, sort = "createDate",
          direction = Direction.DESC)
      Pageable pageable
  ) {
    return ResponseEntity.ok(noticeBoardService.getAllBoard(pageable, searchText));
  }
}
