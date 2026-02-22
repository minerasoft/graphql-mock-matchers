package com.minerasoftware.graphql.core;

import com.minerasoftware.graphql.core.graphqljava.GraphqlJavaMatcher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlMatchersTest {

    @Test
    void defaultMatcherPerformsSemanticComparison() {
        GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

        assertTrue(matcher.semanticallyEqual(
                "query { countries { name currency } }",
                "query { countries { currency name } }"
        ));
    }

    @Test
    void defaultMatcherReturnsSingletonGraphqlJavaMatcher() {
        GraphqlMatcher first = GraphqlMatchers.defaultMatcher();
        GraphqlMatcher second = GraphqlMatchers.defaultMatcher();

        assertSame(first, second);
        assertInstanceOf(GraphqlJavaMatcher.class, first);
    }

    @Test
    void defaultMatcherPerformsSemanticComparisonForMutation() {
        GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

        assertTrue(matcher.semanticallyEqual(
                """
                mutation UpdateCountry($code: String!, $name: String!) {
                  updateCountry(code: $code, name: $name) {
                    code
                    name
                  }
                }
                """,
                """
                mutation UpdateCountry($name: String!, $code: String!) {
                  updateCountry(name: $name, code: $code) {
                    name
                    code
                  }
                }
                """
        ));
    }

    @Test
    void defaultMatcherPerformsSemanticComparisonForSubscription() {
        GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

        assertTrue(matcher.semanticallyEqual(
                """
                subscription CountryUpdated {
                  countryUpdated {
                    code
                    name
                  }
                }
                """,
                """
                subscription CountryUpdated {
                  countryUpdated {
                    name
                    code
                  }
                }
                """
        ));
    }
}
