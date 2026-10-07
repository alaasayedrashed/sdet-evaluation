package com.sdet.evaluation.api.client;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes request/response logging through SLF4J (instead of REST Assured's default
 * {@code System.out}) so HTTP traffic lands in the same log file as everything else.
 * Summaries go to INFO, bodies to DEBUG.
 */
public class Slf4jLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(Slf4jLoggingFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification request,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        LOG.info("--> {} {}", request.getMethod(), request.getURI());
        if (request.getBody() != null) {
            LOG.debug("Request body: {}", (Object) request.getBody());
        }

        Response response = context.next(request, responseSpec);

        LOG.info("<-- {} {} ({} ms)", response.getStatusCode(), request.getURI(), response.getTime());
        LOG.debug("Response body: {}", response.getBody().asString());
        return response;
    }
}
