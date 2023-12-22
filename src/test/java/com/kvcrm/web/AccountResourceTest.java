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
import com.kvcrm.entity.Account;
import com.kvcrm.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@WebMvcTest(value = AccountResource.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class AccountResourceTest {
  @MockBean
  private AccountRepository accountRepository;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  @Autowired
  public AccountResourceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  void shouldCreateAccount() throws Exception {
    Account account = new Account(1L, "spring@example.com");

    mockMvc.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnAccount() throws Exception {
    long id = 1L;
    Account account = new Account(id, "i@example.com");

    when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    mockMvc.perform(get("/api/v1/accounts/{id}", id)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.email").value(account.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundAccount() throws Exception {
    long id = 1L;

    when(accountRepository.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/v1/accounts/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldReturnListOfAccounts() throws Exception {
    List<Account> accounts = new ArrayList<>(
        Arrays.asList(new Account(1L, "spring@example.com 1"),
            new Account(2L, "spring@example.com 2"),
            new Account(3L, "spring@example.com 3")));

    when(accountRepository.findAll()).thenReturn(accounts);
    mockMvc.perform(get("/api/v1/accounts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(accounts.size()))
        .andDo(print());
  }

  @Test
  void shouldReturnListOfAccountsWithFilter() throws Exception {
    List<Account> accounts = new ArrayList<>(
        Arrays.asList(new Account(1L, "spring@example.com"),
            new Account(3L, "demo@example.com")));

    String email = "example";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("email", email);

    when(accountRepository.findByEmailContaining(email)).thenReturn(accounts);
    mockMvc.perform(get("/api/v1/accounts").params(paramsMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(accounts.size()))
        .andDo(print());
  }

  @Test
  void shouldUpdateAccount() throws Exception {
    long id = 1L;

    Account account = new Account(id, "spring@example.com");
    Account updatedAccount = new Account(id, "updated@example.com");

    when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

    mockMvc.perform(put("/api/v1/accounts/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedAccount)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(updatedAccount.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUpdateAccount() throws Exception {
    long id = 1L;

    Account updatedAccount = new Account(id, "updated@example.com");

    when(accountRepository.findById(id)).thenReturn(Optional.empty());
    when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

    mockMvc.perform(put("/api/v1/accounts/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedAccount)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldDeleteAccount() throws Exception {
    long id = 1L;

    doNothing().when(accountRepository).deleteById(id);
    mockMvc.perform(delete("/api/v1/accounts/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

}
