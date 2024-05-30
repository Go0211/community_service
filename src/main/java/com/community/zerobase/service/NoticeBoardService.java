package com.community.zerobase.service;

import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.exception.ErrorException.AlreadyExistException;
import com.community.zerobase.repository.NoticeBoardRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeBoardService {
  private final NoticeBoardRepository noticeBoardRepository;

  @Transactional
  public NoticeBoard createBoard(String name){
    if (existNoticeBoard(name)) {
      throw new AlreadyExistException("notice board exist");
    }

    NoticeBoard noticeBoard = NoticeBoard.builder()
        .name(name)
        .createDate(LocalDateTime.now())
        .build();

    noticeBoardRepository.save(noticeBoard);

    return noticeBoard;
  }

  public boolean existNoticeBoard(String name) {
    return noticeBoardRepository.existsByName(name);
  }

  public Page<NoticeBoard> getAllBoard(Pageable pageable, String searchText) {
    if (!searchText.isEmpty()) {
      return noticeBoardRepository.findByNameContaining(pageable, searchText);
    }

    return noticeBoardRepository.findAll(pageable);
  }
}
