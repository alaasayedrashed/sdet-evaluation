package com.sdet.evaluation.api.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response of {@code POST /users}. {@code createdAt} is checked by the JSON schema ({@code
 * date-time} format), so it is kept as the raw string.
 */
@Data
@NoArgsConstructor
public class CreateUserResponse {

  private String id;
  private String name;
  private String job;
  private String createdAt;
}
