package com.community.zerobase.controller;

import com.community.zerobase.dto.ManagerDto.Request;
import com.community.zerobase.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board/{boardId}/manager")
public class ManagerController {
  private final ManagerService managerService;

  @GetMapping("/list")
  public ResponseEntity<?> getManager(@PathVariable("boardId") Long boardId) {
    return ResponseEntity.ok(
        managerService.getManager(boardId));
  }

  @PostMapping
  public ResponseEntity<?> addManager(
      @PathVariable("boardId") Long boardId,
      @Valid @RequestBody Request request
  ) {
    managerService.checkNotAllowInput(request.getEmail(), boardId);

    return ResponseEntity.ok(
        managerService.addManager(boardId, request.getEmail()));
  }

  @PutMapping
  public ResponseEntity<?> changeMainManager(
      @PathVariable("boardId") Long boardId,
      @Valid @RequestBody Request request
  ) {
    managerService.checkNotAllowInput(request.getEmail(), boardId);

    return ResponseEntity.ok(
        managerService.changeMainManager(boardId, request.getEmail())
    );
  }

  @DeleteMapping
  public ResponseEntity<?> deleteManager(
      @PathVariable("boardId") Long boardId,
      @Valid @RequestBody Request request
  ) {
    managerService.checkNotAllowInput(request.getEmail(), boardId);

    return ResponseEntity.ok(
        managerService.deleteManagers(
            boardId,
            request.getEmail()));
  }
}
