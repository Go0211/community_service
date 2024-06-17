package com.community.zerobase.controller;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.community.zerobase.config.SecurityConfigTest;
import com.community.zerobase.dto.LoginDto;
import com.community.zerobase.dto.LoginDto.Request;
import com.community.zerobase.dto.TokenDto;
import com.community.zerobase.dto.UsersDto;
import com.community.zerobase.dto.UsersDto.Info;
import com.community.zerobase.dto.UsersDto.Join;
import com.community.zerobase.exception.ErrorException;
import com.community.zerobase.exception.ErrorException.InvalidJwtTokenException;
import com.community.zerobase.exception.ErrorException.MissMatchedException;
import com.community.zerobase.exception.ErrorException.NotFoundException;
import com.community.zerobase.jwt.JwtFilter;
import com.community.zerobase.jwt.TokenProvider;
import com.community.zerobase.service.AuthService;
import com.community.zerobase.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
@Import({SecurityConfigTest.class, JwtFilter.class, TokenProvider.class})
class UsersControllerTest {

  @MockBean
  private UsersService usersService;
  @MockBean
  private AuthService authService;
  @MockBean
  private TokenProvider tokenProvider;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  private final UsersDto.Join joinDto = Join.builder()
      .email("1@1")
      .password("123")
      .name("123")
      .birthDate("1234.12.12")
      .phoneNumber("01011112222")
      .build();
  private final Info info = Info.builder()
      .email("1@1")
      .name("123")
      .birthDate("1234.56.78")
      .phoneNumber("01011112222")
      .build();

  @Test
  @DisplayName("회원가입 성공")
  void successJoin() throws Exception {
    // given
    // when
    when(authService.join(any(Join.class))).thenReturn(joinDto.getEmail());

    // then
    String responseValue = mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn()
        .getResponse().getContentAsString();

    assertEquals(joinDto.getEmail(), responseValue);
  }

  @Test
  @DisplayName("이미 존재하는 이메일로 회원가입 실패")
  void failJoin() throws Exception {
    // given
    // when
    when(authService.join(any(Join.class)))
        .thenThrow(new ErrorException.AlreadyExistException("user exist"));

    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andExpect(status().isConflict())
        .andDo(print())
        .andExpect(jsonPath("$.message")
            .value("user exist"))
        .andExpect(jsonPath("$.state")
            .value(409));
  }

  @Test
  @DisplayName("이메일 형식이 아닐 시 회원가입 실패")
  void failJoinWithInvalidEmailFormat() throws Exception {
    // given
    joinDto.setEmail("123214_1283989njdf");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("잘못된 이메일 형식입니다."));
  }

  @Test
  @DisplayName("이메일이 없을 시 회원가입 실패")
  void failJoinWithOutEmail() throws Exception {
    // given
    joinDto.setEmail("");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("이메일은 꼭 입력해야 합니다."));
  }

  @Test
  @DisplayName("비밀번호가 없을 시 회원가입 실패")
  void failJoinWithOutPassword() throws Exception {
    // given
    joinDto.setPassword("");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.password").value("비밀번호는 꼭 입력해야 합니다."));
  }

  @Test
  @DisplayName("이름이 없을 시 회원가입 실패")
  void failJoinWithOutName() throws Exception {
    // given
    joinDto.setName("");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name").value("이름은 꼭 입력해야 합니다."));
  }

  @Test
  @DisplayName("생년월일이 없을 시 회원가입 실패")
  void failJoinWithOutBirthDate() throws Exception {
    // given
    joinDto.setBirthDate("");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.birthDate")
            .value("생년월일은 yyyy.mm.dd형식으로 입력 되야합니다."));
  }

  @Test
  @DisplayName("생년월일 형식이 다를 시 회원가입 실패")
  void failJoinWithInvalidBirthDateFormat() throws Exception {
    // given
    joinDto.setBirthDate("123-12-123");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.birthDate")
            .value("생년월일은 yyyy.mm.dd형식으로 입력 되야합니다."));
  }

  @Test
  @DisplayName("전화번호가 없을 시 회원가입 실패")
  void failJoinWithOutPhoneNumber() throws Exception {
    // given
    joinDto.setPhoneNumber("");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.phoneNumber")
            .value("입력은 11자리여야 합니다."));
  }

  @Test
  @DisplayName("전화번호가 자릿 수가 맞지 않을 때 회원가입 실패")
  void failJoinPhoneNumberLengthIsWrong() throws Exception {
    // given
    joinDto.setPhoneNumber("11232424242121");
    // when
    // then
    mockMvc.perform(post("/join")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(joinDto)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.phoneNumber")
            .value("입력은 11자리여야 합니다."));
  }

  @Test
  @DisplayName("로그인 성공")
  void successLogin() throws Exception {
    LoginDto.Request request
        = new Request("1@1", "123");
    TokenDto tokenDto = TokenDto.builder()
        .grantType("Bearer")
        .accessToken(
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxQDIiLCJhdXRoIjoiTiIsImV4cCI6MTcxODEwMDQ2MH0.BqODmmiDmzFacjL46OxoLuK9srs01eMTiaQau-n4X73Hj_zXbn0UwSD49zjnBqHUU7PpDM8eHDyvPzE-zZkMKg")
        .accessTokenExpiresIn(1718100460655L)
        .build();

    // when
    when(authService.login(any(LoginDto.Request.class))).thenReturn(tokenDto);

    // then
    String responseValue = mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.grantType")
            .value("Bearer"))
        .andExpect(jsonPath("$.accessToken").exists())
        .andExpect(jsonPath("$.accessTokenExpiresIn").exists())
        .andReturn()
        .getResponse().getContentAsString();

    String accessToken = JsonPath.parse(responseValue).read("$.accessToken");
    assertThat(accessToken,
        matchesPattern("^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$"));
    System.out.println(accessToken);

    Long accessTokenExpiresIn = JsonPath.parse(responseValue)
        .read("$.accessTokenExpiresIn", Long.class);
    assertThat(accessTokenExpiresIn, isA(Long.class));
    System.out.println(accessTokenExpiresIn);
  }

  @Test
  @DisplayName("이메일 오류로 로그인 실패")
  void failLoginNotMatchEmail() throws Exception {
    //given
    LoginDto.Request request
        = new Request("1@1", "123");
    // when
    when(authService.login(any(LoginDto.Request.class)))
        .thenThrow(new NotFoundException("not found user"));

    // then
    mockMvc.perform(post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message")
            .value("not found user"))
        .andExpect(jsonPath("$.state").
            value(404));
  }

  @Test
  @DisplayName("비밀번호 오류로 로그인 실패 (구현 예정)")
  void failLoginNotMatchPassword() throws Exception {
    //given
    // when
    // then
  }

  @Test
  @DisplayName("내 정보보기 성공")
  void successGetMyInfo() throws Exception {
    // given
    Info info = Info.builder()
        .email("1@1")
        .name("123")
        .birthDate("1234.56.78")
        .phoneNumber("01011112222")
        .build();

    // when
    when(usersService.getInfo()).thenReturn(info);

    // then
    mockMvc.perform(get("/info")
            .header("Authorization", "Bearer token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email")
            .value(info.getEmail()))
        .andExpect(jsonPath("$.name")
            .value(info.getName()))
        .andExpect(jsonPath("$.birthDate")
            .value(info.getBirthDate()))
        .andExpect(jsonPath("$.phoneNumber")
            .value(info.getPhoneNumber()));
  }

  @Test
  @DisplayName("내 정보 수정 성공")
  void successPutMyInfo() throws Exception {
    // given
    // when
    when(usersService.updateInfo(any(Info.class))).thenReturn(info);

    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email")
            .value(info.getEmail()))
        .andExpect(jsonPath("$.name")
            .value(info.getName()))
        .andExpect(jsonPath("$.birthDate")
            .value(info.getBirthDate()))
        .andExpect(jsonPath("$.phoneNumber")
            .value(info.getPhoneNumber()));
  }

  @Test
  @DisplayName("입력된 아이디와 로그인된 정보가 다를 시 정보 수정 실패")
  void failPutMyInfoNotMatchedInfoEmailLoginEmail() throws Exception {
    // given
    // when
    when(usersService.updateInfo(any(Info.class)))
        .thenThrow(new MissMatchedException("not match users"));

    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message")
            .value("not match users"))
        .andExpect(jsonPath("$.state")
            .value(400));
  }

  @Test
  @DisplayName("입력된 아이디와 로그인된 정보가 다를 시 정보 수정 실패")
  void failPutMyInfoWithOutName() throws Exception {
    // given
    info.setName("");
    // when
    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.name")
            .value("이름은 꼭 입력해야 합니다."));
  }

  @Test
  @DisplayName("생년월일이 없을 시 정보 수정 실패")
  void failPutMyInfoWithOutBirthDate() throws Exception {
    // given
    info.setBirthDate("");
    // when
    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.birthDate")
            .value("생년월일은 yyyy.mm.dd형식으로 입력 되야합니다."));
  }

  @Test
  @DisplayName("생년월일 형식 안맞을 시 정보 수정 실패")
  void failPutMyInfoInvalidBirthDateFormat() throws Exception {
    // given
    info.setBirthDate("132-424-12");
    // when
    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.birthDate")
            .value("생년월일은 yyyy.mm.dd형식으로 입력 되야합니다."));
  }

  @Test
  @DisplayName("번호가 없을 시 정보 수정 실패")
  void failPutMyInfoWithOutPhoneNumber() throws Exception {
    // given
    info.setPhoneNumber("");
    // when
    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.phoneNumber")
            .value("입력은 11자리여야 합니다."));
  }

  @Test
  @DisplayName("번호 사이즈가 안맞을 시 정보 수정 실패")
  void failPutMyInfoPhoneNumberLengthIsWrong() throws Exception {
    // given
    info.setPhoneNumber("132-424-12");
    // when
    // then
    mockMvc.perform(put("/info")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(info)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.phoneNumber")
            .value("입력은 11자리여야 합니다."));
  }

  private String localDateTimeToString(LocalDateTime localDateTime) {
    return localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}