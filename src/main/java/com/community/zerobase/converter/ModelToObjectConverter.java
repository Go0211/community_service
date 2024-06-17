package com.community.zerobase.converter;

import com.community.zerobase.dto.CommentDto;
import com.community.zerobase.dto.PostDto;
import com.community.zerobase.dto.PostDto.Response;
import com.community.zerobase.entity.Comment;
import com.community.zerobase.entity.Manager;
import com.community.zerobase.entity.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ModelToObjectConverter {
  public Page<CommentDto.Response> commentToResponseConvertPage(Page<Comment> commentPage) {
    return commentPage.map(this::commentToDto);
  }

  public CommentDto.Response commentToDto(Comment comment) {
    return CommentDto.Response.commentToDto(comment);
  }

  public List<String> managerToStringConvertList(List<Manager> managerList) {
    return managerList.stream().map(this::managerToResponse).toList();
  }

  public String managerToResponse(Manager manager) {
    return manager.getUsers().getEmail();
  }

  public Page<Response> postToResponseConvertPage(Page<Post> postPage) {
    return postPage.map(this::postToResponse);
  }

  public PostDto.Response postToResponse(Post post) {
    return PostDto.Response.PostToDto(post);
  }
}
