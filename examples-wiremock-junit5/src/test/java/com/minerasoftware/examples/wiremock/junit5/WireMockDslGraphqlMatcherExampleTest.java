package com.minerasoftware.examples.wiremock.junit5;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.graphqlOperation;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Demonstrates the use of {@link com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension}
 * with WireMock's static DSL for programmatic stubbing in a JUnit 5 test.
 *
 * <p>This example configures a stub using the DSL method
 * {@code .andMatching(graphqlOperation(...))} instead of JSON mapping files.
 * Since the matcher instance is supplied directly in the stub definition,
 * the extension does not need to be registered with the WireMock server.
 */
@WireMockTest
class WireMockDslGraphqlMatcherExampleTest {

    private static HttpClient client;

    @BeforeAll
    static void init() {
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void shutdown() {
        client.close();
    }

    @Test
    void matchesOperation(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
        stubFor(post(urlEqualTo("/graphql"))
                .andMatching(graphqlOperation(
                        "query { countries { name currency } }"
                ))
                .willReturn(okJson("""
                          { "data": { "countries": [ { "name": "Andorra", "currency": "EUR" } ] } }
                        """)));

        String body = """
                {
                  "query": "query { countries { currency name } }"
                }
                """;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(wmRuntimeInfo.getHttpBaseUrl() + "/graphql"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }
}