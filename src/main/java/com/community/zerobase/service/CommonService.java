package com.community.zerobase.service;

import com.community.zerobase.entity.Comment;
import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.NoticeBoard;
import com.community.zerobase.entity.Post;
import com.community.zerobase.entity.Users;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.repository.CommentRepository;
import com.community.zerobase.repository.ManagerRepository;
import com.community.zerobase.repository.NoticeBoardRepository;
import com.community.zerobase.repository.PostRepository;
import com.community.zerobase.repository.UsersRepository;
import com.community.zerobase.role.Auth;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonService {
  private final UsersRepository usersRepository;
  private final ManagerRepository managerRepository;
  private final PostRepository postRepository;
  private final NoticeBoardRepository noticeBoardRepository;
  private final CommentRepository commentRepository;

  // 댓글 가져오기
  public Comment getComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new NotFoundException("not have comment"));
  }
  
  // 게시물 가져오기
  public Post getPost(Long postId) {
    return postRepository.findById(postId).orElseThrow(
        () -> new NotFoundException("not have post"));
  }

  // 메인 관리자 가져오기
  public Manager getMainManager(NoticeBoard noticeBoard) {
    return managerRepository.findByNoticeBoardAndAuth(noticeBoard, Auth.MAIN).orElseThrow(
        () -> new NotFoundException("not found main manager"));
  }

  // 관리자 리스트 가져오기
  public List<Manager> getManagerList(Long boardId) {
    return managerRepository.findByNoticeBoardOrderByAuthAsc(getNoticeBoard(boardId));
  }
  
  //게시판 가져오기
  public NoticeBoard getNoticeBoard(Long boardId) {
    return noticeBoardRepository.findById(boardId)
        .orElseThrow(() -> new NotFoundException("not have board"));
  }

  // 유저 가져오기
  public Users getUsers(String email) {
    return usersRepository.findByEmail(email).orElseThrow(
        () -> new NotFoundException("email not found"));
  }

  // 유저 리스트 가져오기
  public List<Users> getUsersList(String email) {
    return usersRepository.findByEmailContaining(email);
  }

  // 현재 로그인된 아이디 가져오기
  public String getUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.isAuthenticated()) {
      Object principal = authentication.getPrincipal();

      if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername();
      } else {
        return principal.toString();
      }
    }
    return null;
  }
}
