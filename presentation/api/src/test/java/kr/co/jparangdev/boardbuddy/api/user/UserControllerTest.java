package kr.co.jparangdev.boardbuddy.api.user;

import kr.co.jparangdev.boardbuddy.api.user.dto.UserDto;
import kr.co.jparangdev.boardbuddy.application.user.usecase.UserManagementUseCase;
import kr.co.jparangdev.boardbuddy.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureJson
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserManagementUseCase userManagementUseCase;

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

        given(userManagementUseCase.getCurrentUser()).willReturn(user);
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

        given(userManagementUseCase.getUserById(1L)).willReturn(Optional.of(user));
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
        given(userManagementUseCase.getUserById(999L)).willReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/api/v1/users/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
