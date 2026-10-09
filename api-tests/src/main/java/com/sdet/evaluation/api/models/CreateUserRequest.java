package com.sdet.evaluation.api.models;

import lombok.Builder;

/**
 * Body of {@code POST /users}. An immutable record: a request is built once and never changes.
 *
 * <pre>{@code
 * var request = CreateUserRequest.builder().name("Byron").job("BA").build();
 * }</pre>
 */
@Builder
public record CreateUserRequest(String name, String job) {

  /** Builds a request from an existing user (API chaining): {@code name} is the first name. */
  public static CreateUserRequest fromUser(User user, String job) {
    return builder().name(user.getFirstName()).job(job).build();
  }
}
