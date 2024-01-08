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
import com.kvcrm.entity.Organization;
import com.kvcrm.repository.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = OrganizationResource.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
class OrganizationResourceTest {
  @MockBean
  private OrganizationRepository organizationRepository;

  private final MockMvc mockMvc;

  private final ObjectMapper objectMapper;

  @Autowired
  public OrganizationResourceTest(MockMvc mockMvc, ObjectMapper objectMapper) {
    this.mockMvc = mockMvc;
    this.objectMapper = objectMapper;
  }

  @Test
  void shouldCreateOrganization() throws Exception {
    Organization organization = Organization.builder().id(1L).email("spring@example.com").build();

    mockMvc.perform(post("/v1/organizations").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(organization)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnOrganization() throws Exception {
    long id = 1L;
    Organization organization =  Organization.builder().id(id).email("i@example.com").build();

    when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));
    mockMvc.perform(get("/v1/organizations/{id}", id)).andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id))
        .andExpect(jsonPath("$.email").value(organization.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundOrganization() throws Exception {
    long id = 1L;

    when(organizationRepository.findById(id)).thenReturn(Optional.empty());
    mockMvc.perform(get("/v1/organizations/{id}", id))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldReturnListOfOrganizations() throws Exception {
    List<Organization> organizations = new ArrayList<>(
        Arrays.asList(Organization.builder().id(1L).email("spring@example.com 1").build(),
             Organization.builder().id(2L).email("spring@example.com 2").build(),
            Organization.builder().id(3L).email("spring@example.com 3").build()));

    when(organizationRepository.findAll()).thenReturn(organizations);
    mockMvc.perform(get("/v1/organizations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(organizations.size()))
        .andDo(print());
  }

  @Test
  void shouldUpdateOrganization() throws Exception {
    long id = 1L;

    Organization organization = Organization.builder().id(id).email("spring@example.com").build();
    Organization updatedOrganization = Organization.builder().id(id).email("updated@example.com").build();

    when(organizationRepository.findById(id)).thenReturn(Optional.of(organization));
    when(organizationRepository.save(any(Organization.class))).thenReturn(updatedOrganization);

    mockMvc.perform(put("/v1/organizations/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedOrganization)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(updatedOrganization.getEmail()))
        .andDo(print());
  }

  @Test
  void shouldReturnNotFoundUpdateOrganization() throws Exception {
    long id = 1L;

    Organization updatedOrganization = Organization.builder().id(id).email("updated@example.com").build();

    when(organizationRepository.findById(id)).thenReturn(Optional.empty());
    when(organizationRepository.save(any(Organization.class))).thenReturn(updatedOrganization);

    mockMvc.perform(put("/v1/organizations/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedOrganization)))
        .andExpect(status().isNotFound())
        .andDo(print());
  }

  @Test
  void shouldDeleteOrganization() throws Exception {
    long id = 1L;

    doNothing().when(organizationRepository).deleteById(id);
    mockMvc.perform(delete("/v1/organizations/{id}", id))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

}
