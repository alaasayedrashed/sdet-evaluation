package com.sdet.evaluation.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Paginated response of {@code GET /users?page=n}. */
@Data
@NoArgsConstructor
public class UserListResponse {

  private int page;

  @JsonProperty("per_page")
  private int perPage;

  private int total;

  @JsonProperty("total_pages")
  private int totalPages;

  private List<User> data = List.of();

  /** Finds a user on this page by id. */
  public Optional<User> findUserById(int id) {
    return data.stream().filter(user -> user.getId() == id).findFirst();
  }
}
