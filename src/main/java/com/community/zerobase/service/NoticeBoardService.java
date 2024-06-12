package com.community.zerobase.service;

import com.community.zerobase.converter.ModelToObjectConverter;
import com.community.zerobase.dto.ManagerDto;
import com.community.zerobase.dto.ManagerDto.Response;
import com.community.zerobase.dto.NoticeBoardDto;
import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException;
import com.community.zerobase.exception.ErrorException.AlreadyExistException;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.community.zerobase.repository.ManagerRepository;
import com.community.zerobase.repository.NoticeBoardRepository;
import com.community.zerobase.role.Auth;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeBoardService {
  private final CommonService commonService;
  private final ModelToObjectConverter converter;

  private final NoticeBoardRepository noticeBoardRepository;
  private final ManagerRepository managerRepository;

  @Transactional
  public NoticeBoardDto.Response createBoard(String name) {
    if (existNoticeBoard(name)) {
      throw new AlreadyExistException("notice board exist");
    }
    if (name.isEmpty()) {
      throw new NullException("notice board name empty");
    }

    NoticeBoard noticeBoard = NoticeBoard.builder()
        .name(name)
        .createDate(LocalDateTime.now())
        .build();

    noticeBoardRepository.save(noticeBoard);

    ManagerDto.Response response = setMainManager(
        noticeBoard,
        commonService.getUsers(commonService.getUserEmail()));

    return NoticeBoardDto.Response.toDto(noticeBoard, response);
  }

  @Transactional
  public NoticeBoardDto.Response updateBoard(Long boardId, String name) {
    if (name == null || name.isEmpty()) {
      throw new NullException("notice board name empty");
    }
    if (boardId == null || boardId <= 0) {
      throw new NullException("notice board id empty");
    }
    if (existNoticeBoard(name)) {
      throw new AlreadyExistException("already use notice board name");
    }

    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);
    noticeBoard.setName(name);

    List<String> managerList
        = converter.managerToStringConvertList(commonService.getManagerList(boardId));

    return NoticeBoardDto.Response.toDto(noticeBoard, managerList);
  }

  @Transactional
  public void deleteBoard(Long boardId) {
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    deleteAllManagers(noticeBoard);

    noticeBoardRepository.delete(noticeBoard);
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

  public void checkMainManager(Long boardId) {
    Users users = commonService.getUsers(commonService.getUserEmail());
    Manager mainManager = commonService.getMainManager(commonService.getNoticeBoard(boardId));

    if (!mainManager.getUsers().equals(users)) {
      throw new MissMatchedException("not match manager");
    }
  }

  @Transactional
  public ManagerDto.Response setMainManager(NoticeBoard noticeBoard, Users users) {
    Manager manager = Manager.builder()
        .users(users)
        .noticeBoard(noticeBoard)
        .auth(Auth.MAIN)
        .build();

    managerRepository.save(manager);

    return Response.ManagerToDto(noticeBoard, manager);
  }

  // 모든 매니져 삭제하기
  @Transactional
  public void deleteAllManagers(NoticeBoard noticeBoard) {
    managerRepository.deleteByNoticeBoard(noticeBoard);
  }
}

