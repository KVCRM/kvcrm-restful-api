package com.kvcrm.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvcrm.entity.User;
import com.kvcrm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = UserResource.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class UserResourceTest {
  @MockBean
  private UserRepository userRepository;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  @Autowired
  public UserResourceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  void shouldCreateUser() throws Exception {
    User user = User.builder().id(1L).email("spring@example.com").build();

    mockMvc.perform(post("/v1/users").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnUser() throws Exception {
    long id = 1L;
    User user =  User.builder().id(id).email("i@example.com").build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    mockMvc.perform(get("/v1/users/{id}", id)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.email").value(user.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUser() throws Exception {
    long id = 1L;

    when(userRepository.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/v1/users/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldReturnListOfUsers() throws Exception {
    List<User> users = new ArrayList<>(
        Arrays.asList(User.builder().id(1L).email("spring@example.com 1").build(),
             User.builder().id(2L).email("spring@example.com 2").build(),
            User.builder().id(3L).email("spring@example.com 3").build()));

    when(userRepository.findAll()).thenReturn(users);
    mockMvc.perform(get("/v1/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(users.size()))
        .andDo(print());
  }

  @Test
  void shouldUpdateUser() throws Exception {
    long id = 1L;

    User user = User.builder().id(id).email("spring@example.com").build();
    User updatedUser = User.builder().id(id).email("updated@example.com").build();

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    mockMvc.perform(put("/v1/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUpdateUser() throws Exception {
    long id = 1L;

    User updatedUser = User.builder().id(id).email("updated@example.com").build();

    when(userRepository.findById(id)).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    mockMvc.perform(put("/v1/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedUser)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldDeleteUser() throws Exception {
    long id = 1L;

    doNothing().when(userRepository).deleteById(id);
    mockMvc.perform(delete("/v1/users/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

}
