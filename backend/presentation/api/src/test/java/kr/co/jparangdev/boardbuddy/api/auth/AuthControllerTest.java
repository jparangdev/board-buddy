package kr.co.jparangdev.boardbuddy.api.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.AuthenticationUseCase;
import tools.jackson.databind.json.JsonMapper;

@ActiveProfiles("local")
@WebMvcTest({AuthController.class, TestAuthController.class})
@AutoConfigureJson
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthenticationUseCase authenticationUseCase;

    @Test
    @DisplayName("Test Login Success")
    @WithMockUser
    void testLoginSuccess() throws Exception {
        // given
        AuthDto.TestLoginRequest request = new AuthDto.TestLoginRequest();
        request.setEmail("test@example.com");
        request.setNickname("tester");

        AuthTokens tokens = AuthTokens.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .build();

        given(authenticationUseCase.authenticate(any(AuthCredentials.class)))
                .willReturn(tokens);

        // when & then
        mockMvc.perform(post("/api/v1/auth/test/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("Refresh Token Success")
    @WithMockUser
    void refreshTokenSuccess() throws Exception {
        // given
        AuthDto.RefreshRequest request = new AuthDto.RefreshRequest();
        request.setRefreshToken("valid-refresh-token");

        AuthTokens tokens = AuthTokens.builder()
                .accessToken("new-access-token")
                .refreshToken("valid-refresh-token")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .build();

        given(authenticationUseCase.refreshAccessToken("valid-refresh-token"))
                .willReturn(tokens);

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access-token"));
    }

    @Test
    @DisplayName("Logout Success")
    @WithMockUser
    void logoutSuccess() throws Exception {
        // given
        AuthDto.LogoutRequest request = new AuthDto.LogoutRequest();
        request.setRefreshToken("valid-refresh-token");

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(authenticationUseCase).logout("valid-refresh-token");
    }
}
