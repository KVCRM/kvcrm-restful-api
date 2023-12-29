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
import com.kvcrm.entity.Contact;
import com.kvcrm.repository.ContactRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = ContactResource.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class ContactResourceTest {
  @MockBean
  private ContactRepository contactRepository;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  @Autowired
  public ContactResourceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  void shouldCreateContact() throws Exception {
    Contact contact = Contact.builder().id(1L).email("spring@example.com").build();

    mockMvc.perform(post("/api/v1/contacts").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(contact)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnContact() throws Exception {
    long id = 1L;
    Contact contact =  Contact.builder().id(id).email("i@example.com").build();

    when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
    mockMvc.perform(get("/api/v1/contacts/{id}", id)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.email").value(contact.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundContact() throws Exception {
    long id = 1L;

    when(contactRepository.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/api/v1/contacts/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldReturnListOfContacts() throws Exception {
    List<Contact> contacts = new ArrayList<>(
        Arrays.asList(Contact.builder().id(1L).email("spring@example.com 1").build(),
             Contact.builder().id(2L).email("spring@example.com 2").build(),
            Contact.builder().id(3L).email("spring@example.com 3").build()));

    when(contactRepository.findAll()).thenReturn(contacts);
    mockMvc.perform(get("/api/v1/contacts"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(contacts.size()))
        .andDo(print());
  }

  @Test
  void shouldUpdateContact() throws Exception {
    long id = 1L;

    Contact contact = Contact.builder().id(id).email("spring@example.com").build();
    Contact updatedContact = Contact.builder().id(id).email("updated@example.com").build();

    when(contactRepository.findById(id)).thenReturn(Optional.of(contact));
    when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

    mockMvc.perform(put("/api/v1/contacts/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedContact)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(updatedContact.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUpdateContact() throws Exception {
    long id = 1L;

    Contact updatedContact = Contact.builder().id(id).email("updated@example.com").build();

    when(contactRepository.findById(id)).thenReturn(Optional.empty());
    when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

    mockMvc.perform(put("/api/v1/contacts/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedContact)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldDeleteContact() throws Exception {
    long id = 1L;

    doNothing().when(contactRepository).deleteById(id);
    mockMvc.perform(delete("/api/v1/contacts/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

}
