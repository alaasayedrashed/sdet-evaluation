package com.sdet.evaluation.api.steps;

import com.sdet.evaluation.api.context.ScenarioContext;
import io.cucumber.java.en.Then;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Resource-independent response assertions, reusable by any future API feature.
 */
public class CommonApiSteps {

    private final ScenarioContext context;

    public CommonApiSteps(ScenarioContext context) {
        this.context = context;
    }

    @Then("the response status code should be {int}")
    public void responseStatusShouldBe(int expectedStatus) {
        var response = context.lastResponse();

        assertThat(response.statusCode())
                .as("HTTP status of the last response. Body: %s", response.asString())
                .isEqualTo(expectedStatus);
    }

    @Then("the response body should match the schema {string}")
    public void responseShouldMatchSchema(String schemaPath) {
        context.lastResponse()
                .then()
                .assertThat()
                .body(matchesJsonSchemaInClasspath(schemaPath));
    }
}
