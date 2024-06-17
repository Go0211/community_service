package com.community.zerobase.service;

import com.community.zerobase.converter.ModelToObjectConverter;
import com.community.zerobase.dto.ManagerDto;
import com.community.zerobase.dto.ManagerDto.Response;
import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.AlreadyExistException;
import com.community.zerobase.exception.ErrorException.NotAllowInputValueException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.ManagerRepository;
import com.community.zerobase.role.Auth;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {
  private final CommonService commonService;
  private final ModelToObjectConverter converter;

  private final ManagerRepository managerRepository;

  @Transactional
  public List<String> addManager(Long boardId, String email) {
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);
    Users users = commonService.getUsers(email);

    if (managerRepository.findByNoticeBoardAndUsersAndAuth(
        noticeBoard, users, Auth.SUB).isPresent()) {
      throw new AlreadyExistException("already exist manager");
    }

    Manager manager = Manager.builder()
        .noticeBoard(noticeBoard)
        .users(users)
        .auth(Auth.SUB)
        .build();

    managerRepository.save(manager);

    return converter.managerToStringConvertList(commonService.getManagerList(boardId));
  }

  @Transactional
  public Map<String, String> changeMainManager(Long boardId, String subEmail) {
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    Manager subManager = getManagerUseUsersNoticeBoard(
        commonService.getUsers(subEmail),
        noticeBoard);
    Manager mainManager = getManagerUseUsersNoticeBoard(
        commonService.getUsers(commonService.getUserEmail()),
        noticeBoard);

    subManager.setAuth(Auth.MAIN);
    mainManager.setAuth(Auth.SUB);

    Map<String, String> map = new HashMap<>();
    map.put("main", subManager.getUsers().getEmail());
    map.put("sub", mainManager.getUsers().getEmail());

    return map;
  }

  @Transactional
  public List<String> deleteManagers(
      Long boardId,
      String deleteEmail
  ) {
    NoticeBoard noticeBoard = commonService.getNoticeBoard(boardId);

    managerRepository.deleteByNoticeBoardAndUsers(
        noticeBoard, commonService.getUsers(deleteEmail));

    return converter.managerToStringConvertList
        (commonService.getManagerList(boardId));
  }

  public void checkNotAllowInput(String email, Long boardId) {
    Users users = commonService.getUsers(email);
    Manager mainManager
        = commonService.getMainManager(commonService.getNoticeBoard(boardId));

    if (mainManager.getUsers().equals(users)) {
      throw new NotAllowInputValueException("not found main manager");
    }
  }

  public Manager getManagerUseUsersNoticeBoard(Users users, NoticeBoard noticeBoard) {
    return managerRepository.findByNoticeBoardAndUsers(noticeBoard, users)
        .orElseThrow(() -> new NotFoundException("not found manager"));
  }

  public List<String> getManager(Long boardId) {
    return converter.managerToStringConvertList
        (commonService.getManagerList(boardId));
  }
}
