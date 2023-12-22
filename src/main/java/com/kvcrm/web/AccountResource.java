package com.kvcrm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kvcrm.entity.Account;
import com.kvcrm.repository.AccountRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Account resource", description = "API endpoints for managing account entity.")
@Validated
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
class AccountResource {

  private final AccountRepository accountRepository;

  @PostMapping
  @Operation(
      description = "Create a new account.",
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
  ResponseEntity<Account> create(@RequestBody AccountRequest accountRequest) {
    Account account = accountRepository.save(new Account(accountRequest.email()));
    return new ResponseEntity<>(account, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(
      description = "Retrieve account by id.",
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
  ResponseEntity<Account> findById(@PathVariable("id") long id) {
    Optional<Account> accountData = accountRepository.findById(id);
    return accountData.map(account -> new ResponseEntity<>(account, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  @Operation(
      description = "Get all the accounts.",
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
  ResponseEntity<List<Account>> findAll(@RequestParam(required = false) String email) {
    List<Account> accounts = new ArrayList<>();

    if (email == null) {
      accounts.addAll(accountRepository.findAll());
    } else {
      accounts.addAll(accountRepository.findByEmailContaining(email));
    }

    return new ResponseEntity<>(accounts, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  @Operation(
      description = "Updates an existing account.",
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
  ResponseEntity<Account> update(@PathVariable("id") long id, @RequestBody AccountRequest accountRequest) {
    Optional<Account> accountData = accountRepository.findById(id);

    if (accountData.isPresent()) {
      Account account = accountData.get();
      account.setEmail(accountRequest.email());
      return new ResponseEntity<>(accountRepository.save(account), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
    accountRepository.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  record AccountRequest(String email) {
  }
}
