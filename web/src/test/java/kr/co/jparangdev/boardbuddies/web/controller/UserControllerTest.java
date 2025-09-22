package kr.co.jparangdev.boardbuddies.web.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.jparangdev.boardbuddies.application.usecases.UserManagementUseCase;
import kr.co.jparangdev.boardbuddies.domain.entity.User;
import kr.co.jparangdev.boardbuddies.web.dto.UserDto;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserManagementUseCase userManagementUseCase;

    private User sampleUser(Long id) {
        return User.builder()
                .id(id)
                .username("john")
                .email("john@example.com")
                .nickname("Johnny")
                .build();
    }

    @Test
    @DisplayName("Create user returns 201 and body")
    void createUser() throws Exception {
        User toSave = User.builder().username("john").email("john@example.com").nickname("Johnny").build();
        User saved = sampleUser(1L);
        Mockito.when(userManagementUseCase.createUser(Mockito.any(User.class))).thenReturn(saved);

        UserDto.CreateRequest req = UserDto.CreateRequest.builder()
                .username("john").email("john@example.com").nickname("Johnny").build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("john")));
    }

    @Test
    @DisplayName("Get user by id - found")
    void getUserById_found() throws Exception {
        Mockito.when(userManagementUseCase.getUserById(1L)).thenReturn(Optional.of(sampleUser(1L)));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }

    @Test
    @DisplayName("Get user by id - not found")
    void getUserById_notFound() throws Exception {
        Mockito.when(userManagementUseCase.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get all users")
    void getAllUsers() throws Exception {
        Mockito.when(userManagementUseCase.getAllUsers()).thenReturn(List.of(sampleUser(1L), sampleUser(2L)));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    @DisplayName("Update user")
    void updateUser() throws Exception {
        User updated = User.builder().id(1L).username("johnny").email("johnny@example.com").nickname("J").build();
        Mockito.when(userManagementUseCase.updateUser(Mockito.eq(1L), Mockito.any(User.class))).thenReturn(updated);

        UserDto.UpdateRequest req = UserDto.UpdateRequest.builder()
                .username("johnny").email("johnny@example.com").nickname("J").build();

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("johnny")))
                .andExpect(jsonPath("$.email", is("johnny@example.com")));
    }

    @Test
    @DisplayName("Delete user")
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userManagementUseCase).deleteUser(1L);
    }

    @Test
    @DisplayName("Search by username")
    void searchByUsername() throws Exception {
        Mockito.when(userManagementUseCase.findByUsername("john")).thenReturn(Optional.of(sampleUser(1L)));

        mockMvc.perform(get("/api/users/search").param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("Search by email")
    void searchByEmail() throws Exception {
        Mockito.when(userManagementUseCase.findByEmail("john@example.com")).thenReturn(Optional.of(sampleUser(1L)));

        mockMvc.perform(get("/api/users/search").param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("john")));
    }

    @Test
    @DisplayName("Search bad request when both or none provided")
    void searchBadRequest() throws Exception {
        mockMvc.perform(get("/api/users/search"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/users/search").param("username", "u").param("email", "e@e.com"))
                .andExpect(status().isBadRequest());
    }
}
