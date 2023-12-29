package com.kvcrm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kvcrm.entity.Contact;
import com.kvcrm.repository.ContactRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.iqkv.boot.restful.web.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Contact resource", description = "API endpoints for managing contact entity.")
@Validated
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
class ContactResource {

  private final ContactRepository contactRepository;

  @PostMapping
  @Operation(
      description = "Create a new contact.",
      responses = {
          @ApiResponse(responseCode = "201",
                       description = "Created"),
          @ApiResponse(responseCode = "500",
                       description = "Internal error",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
          @ApiResponse(responseCode = "400",
                       description = "Bad request",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
      })
  ResponseEntity<Contact> create(@RequestBody ContactRequest contactRequest) {
    Contact contact = contactRepository.save(Contact.builder()
        .email(contactRequest.email())
        .build());
    return new ResponseEntity<>(contact, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(
      description = "Retrieve contact by id.",
      responses = {
          @ApiResponse(responseCode = "200",
                       description = "Success"),
          @ApiResponse(responseCode = "500",
                       description = "Internal error",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
          @ApiResponse(responseCode = "400",
                       description = "Bad request",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
          @ApiResponse(responseCode = "404",
                       description = "Not found",
                       content = @Content(schema = @Schema(implementation = ApiError.class)))
      })
  ResponseEntity<Contact> findById(@PathVariable("id") long id) {
    Optional<Contact> contactData = contactRepository.findById(id);
    return contactData.map(contact -> new ResponseEntity<>(contact, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  @Operation(
      description = "Get all the contacts.",
      responses = {
          @ApiResponse(responseCode = "200",
                       description = "Success"),
          @ApiResponse(responseCode = "500",
                       description = "Internal error",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
          @ApiResponse(responseCode = "400",
                       description = "Bad request",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
      })
  ResponseEntity<List<Contact>> findAll() {
    List<Contact> contacts = new ArrayList<>(contactRepository.findAll());
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  @Operation(
      description = "Updates an existing contact.",
      responses = {
          @ApiResponse(responseCode = "200",
                       description = "Success"),
          @ApiResponse(responseCode = "500",
                       description = "Internal error",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
          @ApiResponse(responseCode = "400",
                       description = "Bad request",
                       content = @Content(schema = @Schema(implementation = ApiError.class))),
      })
  ResponseEntity<Contact> update(@PathVariable("id") long id, @RequestBody ContactRequest contactRequest) {
    Optional<Contact> contactData = contactRepository.findById(id);

    if (contactData.isPresent()) {
      Contact contact = contactData.get();
      contact.setEmail(contactRequest.email());
      return new ResponseEntity<>(contactRepository.save(contact), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
    contactRepository.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  record ContactRequest(String email) {
  }
}
