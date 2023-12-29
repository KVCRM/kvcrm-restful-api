package com.kvcrm.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.kvcrm.entity.User;
import com.kvcrm.repository.UserRepository;
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
@Tag(name = "User resource", description = "API endpoints for managing user entity.")
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserResource {

  private final UserRepository userRepository;

  @PostMapping
  @Operation(
      description = "Create a new user.",
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
  ResponseEntity<User> create(@RequestBody UserRequest userRequest) {
    User user = userRepository.save(User.builder()
        .email(userRequest.email())
        .build());
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(
      description = "Retrieve user by id.",
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
  ResponseEntity<User> findById(@PathVariable("id") long id) {
    Optional<User> userData = userRepository.findById(id);
    return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @GetMapping
  @Operation(
      description = "Get all the users.",
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
  ResponseEntity<List<User>> findAll() {
    List<User> users = new ArrayList<>(userRepository.findAll());
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @PutMapping("/{id}")
  @Operation(
      description = "Updates an existing user.",
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
  ResponseEntity<User> update(@PathVariable("id") long id, @RequestBody UserRequest userRequest) {
    Optional<User> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      User user = userData.get();
      user.setEmail(userRequest.email());
      return new ResponseEntity<>(userRepository.save(user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/{id}")
  ResponseEntity<HttpStatus> delete(@PathVariable("id") long id) {
    userRepository.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);

  }

  record UserRequest(String email) {
  }
}
