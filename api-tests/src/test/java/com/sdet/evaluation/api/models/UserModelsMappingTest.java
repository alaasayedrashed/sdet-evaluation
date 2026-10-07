package com.sdet.evaluation.api.models;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Offline contract test for the JSON <-> model mapping (no network): proves the snake_case
 * fields map onto the POJOs, unknown fields are tolerated, and the request record serializes to
 * the body reqres.in expects.
 */
public class UserModelsMappingTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    public void deserializesPaginatedUsersAndFindsUserById() throws Exception {
        String json = """
                {
                  "page": 2,
                  "per_page": 6,
                  "total": 12,
                  "total_pages": 2,
                  "data": [
                    {"id": 9,  "email": "tobias.funke@reqres.in", "first_name": "Tobias", "last_name": "Funke", "avatar": "a.jpg"},
                    {"id": 10, "email": "byron.fields@reqres.in", "first_name": "Byron",  "last_name": "Fields", "avatar": "b.jpg"}
                  ],
                  "_meta": {"powered_by": "ReqRes"}
                }
                """;

        UserListResponse page = mapper.readValue(json, UserListResponse.class);

        assertThat(page.getPerPage()).as("per_page -> perPage").isEqualTo(6);
        assertThat(page.getTotalPages()).as("total_pages -> totalPages").isEqualTo(2);
        assertThat(page.findUserById(10))
                .as("user 10 on the page")
                .hasValueSatisfying(user -> {
                    assertThat(user.getFirstName()).as("first_name -> firstName").isEqualTo("Byron");
                    assertThat(user.fullName()).isEqualTo("Byron Fields");
                });
        assertThat(page.findUserById(99)).as("missing user").isEmpty();
    }

    @Test
    public void serializesCreateUserRequestBuiltFromAUser() throws Exception {
        var user = new User();
        user.setFirstName("Byron");
        user.setLastName("Fields");

        String body = mapper.writeValueAsString(CreateUserRequest.fromUser(user, "QA Automation Engineer"));

        assertThat(mapper.readTree(body)).isEqualTo(mapper.readTree("""
                {"name": "Byron Fields", "job": "QA Automation Engineer"}
                """));
    }
}
