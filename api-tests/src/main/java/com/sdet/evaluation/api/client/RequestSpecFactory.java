package com.sdet.evaluation.api.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdet.evaluation.api.config.ApiConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Builds the {@link RequestSpecification} shared by every API client.
 *
 * <p>Centralises everything a test should never repeat: base URI/path, JSON content type,
 * the API-key header, timeouts, lenient deserialization, SLF4J logging and the Allure filter
 * that attaches every request/response to the report.
 */
public final class RequestSpecFactory {

    private RequestSpecFactory() {
    }

    /** Creates a fresh specification; specs are mutable, so each client gets its own. */
    public static RequestSpecification defaultSpec() {
        var builder = new RequestSpecBuilder()
                .setBaseUri(ApiConfig.baseUri())
                .setBasePath(ApiConfig.basePath())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .setConfig(restAssuredConfig())
                .addFilter(new Slf4jLoggingFilter())
                .addFilter(new AllureRestAssured());

        ApiConfig.apiKey().ifPresent(key -> builder.addHeader(ApiConfig.apiKeyHeader(), key));
        return builder.build();
    }

    private static RestAssuredConfig restAssuredConfig() {
        return RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", (int) ApiConfig.connectTimeout().toMillis())
                        .setParam("http.socket.timeout", (int) ApiConfig.readTimeout().toMillis()))
                // The API adds fields over time (e.g. "_meta"); tests assert only what they care about
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .jackson2ObjectMapperFactory((type, charset) -> new ObjectMapper()
                                .findAndRegisterModules()
                                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)));
    }
}
