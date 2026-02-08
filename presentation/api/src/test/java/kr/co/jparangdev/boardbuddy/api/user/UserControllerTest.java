package kr.co.jparangdev.boardbuddy.api.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.user.usecase.UserQueryUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;

@WebMvcTest(UserController.class)
@AutoConfigureJson
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserQueryUseCase userQueryUseCase;

    @MockitoBean
    private UserDtoMapper userDtoMapper;

    @Test
    @DisplayName("Get Current User Success")
    @WithMockUser
    void getCurrentUserSuccess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .build();
        UserDto.Response response = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .userTag("tester#1234")
                .build();

        given(userQueryUseCase.getCurrentUser()).willReturn(user);
        given(userDtoMapper.toResponse(any(User.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.userTag").value("tester#1234"));
    }

    @Test
    @DisplayName("Get User By ID Success")
    @WithMockUser
    void getUserByIdSuccess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .build();
        UserDto.Response response = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .userTag("tester#1234")
                .build();

        given(userQueryUseCase.getUserById(1L)).willReturn(Optional.of(user));
        given(userDtoMapper.toResponse(any(User.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/users/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Get User By ID Failed - Not Found")
    @WithMockUser
    void getUserByIdNotFound() throws Exception {
        // given
        given(userQueryUseCase.getUserById(999L)).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/v1/users/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Search Users Success")
    @WithMockUser
    void searchUsersSuccess() throws Exception {
        // given
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .build();
        UserDto.Response response = UserDto.Response.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("tester")
                .discriminator("1234")
                .userTag("tester#1234")
                .build();
        UserDto.SearchResponse searchResponse = UserDto.SearchResponse.builder()
                .users(List.of(response))
                .build();

        given(userQueryUseCase.searchUsers("test")).willReturn(List.of(user));
        given(userDtoMapper.toSearchResponse(anyList())).willReturn(searchResponse);

        // when & then
        mockMvc.perform(get("/api/v1/users/search").param("keyword", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users[0].nickname").value("tester"));
    }

    @Test
    @DisplayName("Search Users - Empty Keyword Returns Empty List")
    @WithMockUser
    void searchUsersEmptyKeyword() throws Exception {
        // given
        UserDto.SearchResponse searchResponse = UserDto.SearchResponse.builder()
                .users(List.of())
                .build();

        given(userQueryUseCase.searchUsers("")).willReturn(List.of());
        given(userDtoMapper.toSearchResponse(anyList())).willReturn(searchResponse);

        // when & then
        mockMvc.perform(get("/api/v1/users/search").param("keyword", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users").isEmpty());
    }
}
