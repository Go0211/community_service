package com.community.zerobase.repository;

import com.community.zerobase.entity.NoticeBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Long> {
  boolean existsByName(String name);

  Page<NoticeBoard> findByNameContaining(Pageable pageable, String searchText);
}
