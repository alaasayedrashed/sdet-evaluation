package com.sdet.evaluation.api.context;

import com.sdet.evaluation.api.models.CreateUserRequest;
import com.sdet.evaluation.api.models.User;
import io.restassured.response.Response;
import java.util.Optional;

/**
 * Per-scenario state shared between step classes (API chaining).
 *
 * <p>PicoContainer creates one instance per scenario and injects it into every step class that asks
 * for it, so data never leaks between scenarios and no static state is needed. Typed fields (rather
 * than a {@code Map<String, Object>}) keep the steps compile-time safe.
 */
public class ScenarioContext {

  private Response lastResponse;
  private User selectedUser;
  private CreateUserRequest createUserRequest;

  /** The most recent HTTP response, used by generic status/schema assertions. */
  public Response lastResponse() {
    if (lastResponse == null) {
      throw new IllegalStateException("No request has been sent yet in this scenario");
    }
    return lastResponse;
  }

  /** The most recent response if any (used by hooks, which must not throw). */
  public Optional<Response> findLastResponse() {
    return Optional.ofNullable(lastResponse);
  }

  public void setLastResponse(Response response) {
    this.lastResponse = response;
  }

  /** The user picked from a GET response to feed a later request. */
  public User selectedUser() {
    if (selectedUser == null) {
      throw new IllegalStateException("No user has been selected in this scenario");
    }
    return selectedUser;
  }

  public void setSelectedUser(User user) {
    this.selectedUser = user;
  }

  /** The body sent in the last create-user request, kept to verify the echo. */
  public CreateUserRequest createUserRequest() {
    if (createUserRequest == null) {
      throw new IllegalStateException("No create-user request has been sent in this scenario");
    }
    return createUserRequest;
  }

  public void setCreateUserRequest(CreateUserRequest request) {
    this.createUserRequest = request;
  }
}
