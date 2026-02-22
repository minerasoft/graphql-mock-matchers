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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Demonstrates using {@link GraphqlOperationRequestMatcherExtension} with WireMock's JUnit 5 extension.
 *
 * <p>This test starts a WireMock server with the GraphQL operation matcher extension registered via
 * {@code .extensions(...)} and relies on a JSON mapping file under
 * {@code src/test/resources/mappings/}.
 */
class WireMockJsonMappingsWithGraphqlMatcherExampleTest {

    private static HttpClient client;

    @BeforeAll
    static void init() {
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void shutdown() {
        client.close();
    }

    @RegisterExtension
    static WireMockExtension server = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .dynamicPort()
                    .extensions(new GraphqlOperationRequestMatcherExtension())
            )
            .build();

    @Test
    void matchesOperation() throws Exception {
        String body = """
                {
                  "query": "query { countries { currency name } }"
                }
                """;

        var req = HttpRequest.newBuilder()
                .uri(URI.create(server.baseUrl() + "/graphql"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }

    @Test
    void matchesOperationWithOperationNameAndVariablesFromJsonMapping() throws Exception {
        String body = """
                {
                  "query": "query Country($code: String!) { country(code: $code) { code name } }",
                  "operationName": "Country",
                  "variables": {
                    "code": "AD"
                  }
                }
                """;

        var req = HttpRequest.newBuilder()
                .uri(URI.create(server.baseUrl() + "/graphql-advanced"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        var resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
    }
}
