package com.sdet.evaluation.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of {@code POST /users}. {@code createdAt} is kept as the raw string so tests can assert
 * that the server returned a valid ISO-8601 timestamp.
 */
@Data
@NoArgsConstructor
public class CreateUserResponse {

  private String id;
  private String name;
  private String job;
  private String createdAt;
}
