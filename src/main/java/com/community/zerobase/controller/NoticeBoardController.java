package com.community.zerobase.controller;

import com.community.zerobase.exception.ErrorException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.community.zerobase.service.NoticeBoardService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class NoticeBoardController {

  private final NoticeBoardService noticeBoardService;

  @PostMapping
  public ResponseEntity<?> createNoticeBoard(@RequestBody Map<String, Object> noticeBoardMap) {
    String noticeBoardName = (String) noticeBoardMap.get("name");

    return ResponseEntity.ok(
        noticeBoardService.createBoard(noticeBoardName));
  }

  @PutMapping
  public ResponseEntity<?> updateNoticeBoard(@RequestBody Map<String, Object> noticeBoardMap) {
    String str = (String) noticeBoardMap.get("id");

    if (str.isEmpty() || str == null) {
      throw new NullException("not have board");
    }

    Long noticeBoardId = Long.valueOf(str);
    String noticeBoardName = (String) noticeBoardMap.get("name");

    noticeBoardService.checkMainManager(noticeBoardId);

    return ResponseEntity.ok(
        noticeBoardService.updateBoard(noticeBoardId, noticeBoardName));
  }


  @DeleteMapping
  public ResponseEntity<?> deleteNoticeBoard(@RequestBody Map<String, Object> noticeBoardMap) {
    String str = (String) noticeBoardMap.get("id");

    if (str.isEmpty() || str == null) {
      throw new NullException("not have board");
    }

    Long noticeBoardId = Long.valueOf(str);

    noticeBoardService.checkMainManager(noticeBoardId);

    noticeBoardService.deleteBoard(noticeBoardId);

    return ResponseEntity.ok(HttpStatus.OK);
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
