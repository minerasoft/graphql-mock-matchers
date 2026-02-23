package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.minerasoftware.graphql.core.GraphqlMatcher;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlOperationMatcherTest {

    @Test
    void returnsExactMatchForSemanticallyEquivalentOperations() {
        MatchResult result = new GraphqlOperationMatcher().match(context(
                "query { countries { name currency } }",
                "query { countries { currency name } }"
        ));

        assertTrue(result.isExactMatch());
        assertEquals(0, result.getDistance());
    }

    @Test
    void returnsNoExactMatchAndPositiveDistanceForDifferentOperations() {
        MatchResult result = new GraphqlOperationMatcher().match(context(
                "query { countries { name } }",
                "query { countries { code } }"
        ));

        assertFalse(result.isExactMatch());
        assertTrue(result.getDistance() > 0);
    }

    @Test
    void returnsNoMatchWhenExpectedOperationIsEmpty() {
        MatchResult result = new GraphqlOperationMatcher().match(context("", "query { ping }"));

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenActualOperationIsEmpty() {
        MatchResult result = new GraphqlOperationMatcher().match(context("query { ping }", ""));

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenMatcherThrows() {
        GraphqlMatcher throwingMatcher = new GraphqlMatcher() {
            @Override
            public boolean semanticallyEqual(String expectedOperation, String actualOperation) {
                throw new IllegalStateException("boom");
            }

            @Override
            public String canonicalForm(String operation) {
                return operation;
            }
        };

        MatchResult result = new GraphqlOperationMatcher(throwingMatcher)
                .match(context("query { ping }", "query { ping }"));

        assertFalse(result.isExactMatch());
    }

    private static GraphqlMatchContext context(String expectedQuery, String actualQuery) {
        return new GraphqlMatchContext(
                Map.of("query", actualQuery),
                Parameters.one("query", expectedQuery)
        );
    }
}
