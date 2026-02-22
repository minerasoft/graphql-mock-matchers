package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.minerasoftware.wiremock.graphql.TestData.request;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlOperationRequestMatcherExtensionOperationNameTest {

    private final GraphqlOperationRequestMatcherExtension extension =
            new GraphqlOperationRequestMatcherExtension();

    @Test
    void matchesWhenOperationNameMatches() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": "Ping"
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", "Ping"
                ))
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenOperationNameDiffers() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": "Ping"
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", "DifferentName"
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void matchesWhenExpectedOperationNameIsMissingAndActualIsMissing() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }"
                        }
                        """),
                Parameters.one("query", "query Ping { ping }")
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedOperationNameIsMissingAndActualIsPresent() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": "Ping"
                        }
                        """),
                Parameters.one("query", "query Ping { ping }")
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedOperationNameIsPresentAndActualIsMissing() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }"
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", "Ping"
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedOperationNameIsPresentAndActualIsEmpty() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": ""
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", "Ping"
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedOperationNameIsEmptyAndActualIsNonEmpty() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": "Ping"
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", ""
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void matchesWhenBothOperationNamesAreEmpty() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Ping { ping }",
                          "operationName": ""
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Ping { ping }",
                        "operationName", ""
                ))
        );

        assertTrue(result.isExactMatch());
    }
}
