package com.minerasoftware.examples.wiremock.junit5;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.GRAPHQL_OPERATION_MATCHER;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.parameters;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.query;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Demonstrates the use of {@link com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension}
 * with WireMock's static DSL for programmatic stubbing in a JUnit 5 test.
 *
 * <p>This example configures a stub using the DSL method
 * {@code .andMatching(GRAPHQL_OPERATION_MATCHER, query(...))}
 * instead of JSON mapping files. The matcher extension is registered on the
 * WireMock server via {@code .extensions(...)}.
 */
class WireMockDslGraphqlMatcherExampleTest {

    private static HttpClient client;

    @RegisterExtension
    static WireMockExtension server = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
                    .extensions(new GraphqlOperationRequestMatcherExtension())
            )
            .build();

    @BeforeAll
    static void init() {
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void shutdown() {
        client.close();
    }

    @Test
    void matchesOperation() throws Exception {
        server.stubFor(post(urlEqualTo("/graphql"))
                .andMatching(
                        GRAPHQL_OPERATION_MATCHER,
                        query("query { countries { name currency } }")
                )
                .willReturn(okJson("""
                          { "data": { "countries": [ { "name": "Andorra", "currency": "EUR" } ] } }
                        """)));

        String body = """
                {
                  "query": "query { countries { currency name } }"
                }
                """;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(server.baseUrl() + "/graphql"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    @Test
    void matchesOperationWithOperationNameAndVariables() throws Exception {
        server.stubFor(post(urlEqualTo("/graphql-advanced"))
                .andMatching(
                        GRAPHQL_OPERATION_MATCHER,
                        parameters("query Country($code: String!) { country(code: $code) { name code } }")
                                .operationName("Country")
                                .variable("code", "AD")
                                .build()
                )
                .willReturn(okJson("""
                          { "data": { "country": { "name": "Andorra", "code": "AD" } } }
                        """)));

        String body = """
                {
                  "query": "query Country($code: String!) { country(code: $code) { code name } }",
                  "operationName": "Country",
                  "variables": {
                    "code": "AD"
                  }
                }
                """;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(server.baseUrl() + "/graphql-advanced"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }
}
