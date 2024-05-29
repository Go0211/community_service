package com.community.zerobase.entity;

import com.community.zerobase.dto.CommentDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
  @PrePersist
  protected void onCreated() {
    writeDate = LocalDateTime.now();
    modificationDate = LocalDateTime.now();
    likes = 0;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne
  Users users;

  @ManyToOne
  Post post;

  @Column(name = "content")
  String content;

  @Column(name = "likes")
  int likes;

  @Column(name = "write_date")
  LocalDateTime writeDate;

  @Column(name = "modification_date")
  LocalDateTime modificationDate;

  public void updateComment(CommentDto.Request request) {
    this.content = request.getContent();
    this.modificationDate = LocalDateTime.now();
  }
}
