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
    Account account = Account.builder().id(1L).name("spring@example.com").build();

    mockMvc.perform(post("/api/v1/accounts").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(account)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnAccount() throws Exception {
    long id = 1L;
    Account account =  Account.builder().id(id).name("i@example.com").build();

    when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    mockMvc.perform(get("/api/v1/accounts/{id}", id)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.name").value(account.getName()))
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
        Arrays.asList(Account.builder().id(1L).name("spring@example.com 1").build(),
             Account.builder().id(2L).name("spring@example.com 2").build(),
            Account.builder().id(3L).name("spring@example.com 3").build()));

    when(accountRepository.findAll()).thenReturn(accounts);
    mockMvc.perform(get("/api/v1/accounts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(accounts.size()))
        .andDo(print());
  }

  @Test
  void shouldReturnListOfAccountsWithFilter() throws Exception {
    List<Account> accounts = new ArrayList<>(
        Arrays.asList(Account.builder().id(1L).name("spring@example.com").build(),
            Account.builder().id(3L).name("demo@example.com").build()));

    String name = "example";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("name", name);

    when(accountRepository.findByNameContaining(name)).thenReturn(accounts);
    mockMvc.perform(get("/api/v1/accounts").params(paramsMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(accounts.size()))
        .andDo(print());
  }

  @Test
  void shouldUpdateAccount() throws Exception {
    long id = 1L;

    Account account = Account.builder().id(id).name("spring@example.com").build();
    Account updatedAccount = Account.builder().id(id).name("updated@example.com").build();

    when(accountRepository.findById(id)).thenReturn(Optional.of(account));
    when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

    mockMvc.perform(put("/api/v1/accounts/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedAccount)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(updatedAccount.getName()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUpdateAccount() throws Exception {
    long id = 1L;

    Account updatedAccount = Account.builder().id(id).name("updated@example.com").build();

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
