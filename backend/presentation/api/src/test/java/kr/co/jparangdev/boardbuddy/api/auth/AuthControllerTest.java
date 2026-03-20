package kr.co.jparangdev.boardbuddy.api.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.co.jparangdev.boardbuddy.api.auth.dto.AuthDto;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthCredentials;
import kr.co.jparangdev.boardbuddy.application.auth.dto.AuthTokens;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.AuthenticationUseCase;
import kr.co.jparangdev.boardbuddy.application.auth.usecase.RegisterUseCase;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.DuplicateEmailException;
import kr.co.jparangdev.boardbuddy.domain.auth.exception.InvalidCredentialsException;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureJson
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthenticationUseCase authenticationUseCase;

    @MockitoBean
    private RegisterUseCase registerUseCase;

    @Test
    @DisplayName("Login Success")
    @WithMockUser
    void loginSuccess() throws Exception {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@example.com", "password1");

        AuthTokens tokens = AuthTokens.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .expiresIn(3600L)
                .tokenType("Bearer")
                .build();

        given(authenticationUseCase.authenticate(any(AuthCredentials.class)))
                .willReturn(tokens);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    @DisplayName("Register Success")
    @WithMockUser
    void registerSuccess() throws Exception {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest("new@example.com", "password1", "NewUser");

        willDoNothing().given(registerUseCase).register(
                eq("new@example.com"), eq("password1"), eq("NewUser"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(registerUseCase).register("new@example.com", "password1", "NewUser");
    }

    @Test
    @DisplayName("Register - Validation Error (password too short)")
    @WithMockUser
    void registerValidationError() throws Exception {
        String body = """
                {"email":"test@example.com","password":"short","nickname":"Tester"}
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    @DisplayName("Register - Duplicate Email returns 409")
    @WithMockUser
    void registerDuplicateEmail() throws Exception {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest("existing@example.com", "password1", "Tester");

        willThrow(new DuplicateEmailException("existing@example.com"))
                .given(registerUseCase).register(any(), any(), any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("Login - Invalid Credentials returns 401")
    @WithMockUser
    void loginInvalidCredentials() throws Exception {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@example.com", "wrongpass1");

        given(authenticationUseCase.authenticate(any(AuthCredentials.class)))
                .willThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("Refresh Token Success")
    @WithMockUser
    void refreshTokenSuccess() throws Exception {
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
        AuthDto.LogoutRequest request = new AuthDto.LogoutRequest();
        request.setRefreshToken("valid-refresh-token");

        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(authenticationUseCase).logout("valid-refresh-token");
    }
}
