package com.community.zerobase.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.community.zerobase.config.SecurityConfigTest;
import com.community.zerobase.dto.NoticeBoardDto;
import com.community.zerobase.dto.NoticeBoardDto.Response;
import com.community.zerobase.exception.ErrorException.AlreadyExistException;
import com.community.zerobase.exception.ErrorException.InvalidJwtTokenException;
import com.community.zerobase.exception.ErrorException.NullException;
import com.community.zerobase.jwt.JwtFilter;
import com.community.zerobase.jwt.TokenProvider;
import com.community.zerobase.service.NoticeBoardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NoticeBoardController.class)
@ActiveProfiles("test")
@Import({SecurityConfigTest.class, JwtFilter.class, TokenProvider.class})
class NoticeBoardControllerTest {

  @MockBean
  private NoticeBoardService noticeBoardService;
  @MockBean
  private TokenProvider tokenProvider;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("게시판 생성 성공")
  void successCreateNoticeBoard() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");

    ArrayList<String> noticeBoardDtoList = new ArrayList<>();
    noticeBoardDtoList.add("1@1");

    NoticeBoardDto.Response noticeBoardDto = Response.builder()
        .name(noticeBoardMap.get("name").toString())
        .createDate(LocalDateTime.now())
        .managerList(noticeBoardDtoList)
        .build();

    // when
    when(tokenProvider.validateToken("Bearer token"))
        .thenReturn(true);
    when(noticeBoardService.createBoard(noticeBoardMap.get("name").toString()))
        .thenReturn(noticeBoardDto);

    // then
    mockMvc.perform(post("/board")
            .header("Authorization", "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name")
            .value(noticeBoardMap.get("name").toString()))
        .andExpect(jsonPath("$.managerList[0]")
            .value("1@1"));
  }

  @Test
  @DisplayName("로그인하지 않았을 시 게시판 생성 실패")
  void failCreateNoticeBoardWithOutLogin() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");

    // when
    when(noticeBoardService.createBoard(noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("null jwt token"));

    // then
    mockMvc.perform(post("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("null jwt token"))
        .andExpect(jsonPath("$.state").
            value(400));
  }

  @Test
  @DisplayName("이름이 없을 시 게시판 생성 실패")
  void failCreateNoticeBoardWithOutName() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");

    // when
    when(noticeBoardService.createBoard(noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("notice board name empty"));

    // then
    mockMvc.perform(post("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("notice board name empty"))
        .andExpect(jsonPath("$.state").
            value(400));
  }

  @Test
  @DisplayName("해당 이름의 게시판이 존재 시 게시판 생성 실패")
  void failCreateNoticeBoardExistName() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");

    // when
    when(noticeBoardService.createBoard(noticeBoardMap.get("name").toString()))
        .thenThrow(new AlreadyExistException("notice board exist"));

    // then
    mockMvc.perform(post("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message")
            .value("notice board exist"))
        .andExpect(jsonPath("$.state").
            value(409));
  }

  @Test
  @DisplayName("게시판 수정 성공")
  void successUpdateNoticeBoard() throws Exception {
    //given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");
    noticeBoardMap.put("id", "1");

    ArrayList<String> noticeBoardDtoList = new ArrayList<>();
    noticeBoardDtoList.add("1@1");

    NoticeBoardDto.Response noticeBoardDto = Response.builder()
        .name(noticeBoardMap.get("name").toString())
        .createDate(LocalDateTime.now())
        .managerList(noticeBoardDtoList)
        .build();

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenReturn(noticeBoardDto);

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name")
            .value(noticeBoardMap.get("name").toString()))
        .andExpect(jsonPath("$.managerList[0]")
            .value("1@1"));
  }

  @Test
  @DisplayName("이름이 입력하지 아닐 시 게시판 수정 실패")
  void failUpdateNoticeBoardWithOutName() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "");
    noticeBoardMap.put("id", "1");

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("notice board name empty"));

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("notice board name empty"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

  @Test
  @DisplayName("수정 할 게시판 id가 없을 시 게시판 수정 실패")
  void failUpdateNoticeBoardWithOutId() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");
    noticeBoardMap.put("id", "1");

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("not have board"));

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("not have board"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

  @Test
  @DisplayName("게시판 이름이 이미 존재할 시 게시판 수정 실패")
  void failUpdateNoticeBoardExistName() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");
    noticeBoardMap.put("id", "1");

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenThrow(new AlreadyExistException("already use notice board name"));

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message")
            .value("already use notice board name"))
        .andExpect(jsonPath("$.state")
            .value(409));
  }

  @Test
  @DisplayName("총관리자가 아닐 시 게시판 수정 실패")
  void failUpdateNoticeBoardNotMatchMainManager() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");
    noticeBoardMap.put("id", "1");

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("not match manager"));

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("not match manager"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

  @Test
  @DisplayName("로그인 하지 않았을 시 게시판 수정 실패")
  void failUpdateNoticeBoardWithOutLogin() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("name", "123");
    noticeBoardMap.put("id", "1");

    // when
    when(noticeBoardService.updateBoard(
        Long.parseLong(noticeBoardMap.get("id").toString()),
        noticeBoardMap.get("name").toString()))
        .thenThrow(new NullException("null jwt token"));

    // then
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("null jwt token"))
        .andExpect(jsonPath("$.state").
            value(400));
  }

  @Test
  @DisplayName("게시판 삭제 성공")
  void successDeleteNoticeBoard() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("id", "1");
    // when
    // then
    mockMvc.perform(delete("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("게시판 아이디가 없으면 삭제 실패")
  void failDeleteNoticeBoardWithOutId() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("id", "");

    // when
    // then
    mockMvc.perform(delete("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("not have board"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

  @Test
  @DisplayName("총관리자 아닐 시 삭제 실패")
  void failDeleteNoticeBoardNotMatchMainManager() throws Exception {
    // given
    Map<String, Object> noticeBoardMap = new HashMap<>();
    noticeBoardMap.put("id", "123");

    // when
    when(tokenProvider.validateToken("invalid_token"))
        .thenThrow(new InvalidJwtTokenException("invalid jwt token"));

    // then
    mockMvc.perform(delete("/board")
            .header("Authorization", "Bearer invalid_token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(noticeBoardMap)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("invalid jwt token"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

}