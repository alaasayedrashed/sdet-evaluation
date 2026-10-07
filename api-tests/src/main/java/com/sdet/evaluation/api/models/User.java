package com.sdet.evaluation.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A user as returned by {@code GET /users}.
 */
@Data
@NoArgsConstructor
public class User {

    private int id;
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String avatar;

    /** First and last name separated by a space, e.g. {@code Byron Fields}. */
    public String fullName() {
        return firstName + " " + lastName;
    }
}
