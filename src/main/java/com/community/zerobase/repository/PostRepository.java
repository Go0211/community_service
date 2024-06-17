package com.community.zerobase.repository;

import com.community.zerobase.dto.PostDto;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  Page<Post> findByNoticeBoardAndTitleContaining
      (Pageable pageable, NoticeBoard noticeBoard, String searchText);

  Page<Post> findAllByNoticeBoard(Pageable pageable, NoticeBoard noticeBoard);

  Page<Post> findAllByNoticeBoardAndContentContaining
      (Pageable pageable, NoticeBoard noticeBoard, String searchText);

  Page<Post> findAllByNoticeBoardAndUsersIn
      (Pageable pageable, NoticeBoard noticeBoard, List<Users> usersList);

}
