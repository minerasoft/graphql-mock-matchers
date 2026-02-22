package com.minerasoftware.graphql.core;

import com.minerasoftware.graphql.core.graphqljava.GraphqlJavaMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlMatchersTest {

    @Test
    void defaultMatcherReturnsSingletonGraphqlJavaMatcher() {
        GraphqlMatcher first = GraphqlMatchers.defaultMatcher();
        GraphqlMatcher second = GraphqlMatchers.defaultMatcher();

        assertSame(first, second);
        assertInstanceOf(GraphqlJavaMatcher.class, first);
    }

    @Test
    void defaultMatcherPerformsSemanticComparison() {
        GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

        assertTrue(matcher.semanticallyEqual(
                "query { countries { name currency } }",
                "query { countries { currency name } }"
        ));
    }
}
