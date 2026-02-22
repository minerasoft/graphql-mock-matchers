package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.minerasoftware.wiremock.graphql.TestData.request;
import static com.minerasoftware.wiremock.graphql.TestData.requestWithoutBody;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlOperationRequestMatcherExtensionQueryTest {

    private final GraphqlOperationRequestMatcherExtension extension =
            new GraphqlOperationRequestMatcherExtension();

    @Test
    void matchesWhenQueryIsSemanticallyEquivalent() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query { countries { currency name } }"
                        }
                        """),
                Parameters.one("query", "query { countries { name currency } }")
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenRequestBodyIsEmptyJsonObject() {
        MatchResult result = extension.match(request("{}"), Parameters.one("query", "query { ping }"));

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenRequestBodyIsMissing() {
        MatchResult result = extension.match(requestWithoutBody(), Parameters.one("query", "query { ping }"));

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenRequestQueryFieldIsMissing() {
        MatchResult result = extension.match(
                request("{\"operationName\":\"Ping\"}"),
                Parameters.one("query", "query { ping }")
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedQueryIsMissing() {
        MatchResult result = extension.match(request("{\"query\":\"query { ping }\"}"), Parameters.empty());

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedQueryParameterIsNull() {
        Map<String, Object> params = new HashMap<>();
        params.put("query", null);

        MatchResult result = extension.match(
                request("{\"query\":\"query { ping }\"}"),
                Parameters.from(params)
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedQueryParameterIsEmpty() {
        MatchResult result = extension.match(
                request("{\"query\":\"query { ping }\"}"),
                Parameters.one("query", "")
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void invalidJsonReturnsNoMatch() {
        MatchResult result = extension.match(
                request("not-json"),
                Parameters.one("query", "query { ping }")
        );

        assertFalse(result.isExactMatch());
    }
}
