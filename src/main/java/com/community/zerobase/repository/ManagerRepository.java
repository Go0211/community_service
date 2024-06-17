package com.community.zerobase.repository;

import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Users;
import com.community.zerobase.role.Auth;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {
  List<Manager> findByNoticeBoardOrderByAuthAsc
      (NoticeBoard noticeBoard);

  void deleteByNoticeBoard(NoticeBoard noticeBoard);

  Optional<Manager> findByNoticeBoardAndAuth
      (NoticeBoard noticeBoard, Auth auth);

  Optional<Manager> findByNoticeBoardAndUsers
      (NoticeBoard noticeBoard, Users subUsers);

  void deleteByNoticeBoardAndUsers
      (NoticeBoard noticeBoard, Users deleteManagerUsers);

  Optional<Manager> findByNoticeBoardAndUsersAndAuth(NoticeBoard noticeBoard, Users users, Auth auth);
}
