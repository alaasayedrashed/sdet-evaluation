package com.sdet.evaluation.api.client;

import com.sdet.evaluation.api.models.CreateUserRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Client for the {@code /users} resource. Returns raw {@link Response}s so callers can assert
 * on status codes and headers before deserializing the body into models.
 */
public class UsersClient {

    private static final String USERS = "/users";

    private final RequestSpecification spec;

    public UsersClient() {
        this.spec = RequestSpecFactory.defaultSpec();
    }

    /** {@code GET /users?page={page}} */
    @Step("GET /users?page={page}")
    public Response getUsers(int page) {
        return given(spec)
                .queryParam("page", page)
                .when()
                .get(USERS);
    }

    /** {@code POST /users} with a JSON body serialized from the request model. */
    @Step("POST /users")
    public Response createUser(CreateUserRequest request) {
        return given(spec)
                .body(request)
                .when()
                .post(USERS);
    }
}
