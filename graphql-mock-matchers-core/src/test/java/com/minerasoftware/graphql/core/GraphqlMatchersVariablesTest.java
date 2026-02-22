package com.minerasoftware.graphql.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlMatchersVariablesTest {

    private final GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

    @Test
    void defaultMatcherHandlesEquivalentOperationsWithVariables() {
        assertTrue(matcher.semanticallyEqual(
                """
                query Country($code: String!, $lang: String) {
                  country(code: $code) {
                    name(lang: $lang)
                    currency
                  }
                }
                """,
                """
                query Country($lang: String, $code: String!) {
                  country(code: $code) {
                    currency
                    name(lang: $lang)
                  }
                }
                """
        ));
    }

    @Test
    void defaultMatcherReturnsFalseWhenVariableTypeDiffers() {
        assertFalse(matcher.semanticallyEqual(
                """
                query Country($code: String!) {
                  country(code: $code) { name }
                }
                """,
                """
                query Country($code: ID!) {
                  country(code: $code) { name }
                }
                """
        ));
    }

    @Test
    void defaultMatcherReturnsFalseWhenVariableUsageDiffers() {
        assertFalse(matcher.semanticallyEqual(
                """
                query Country($code: String!) {
                  country(code: $code) { name }
                }
                """,
                """
                query Country($id: String!) {
                  country(code: $id) { name }
                }
                """
        ));
    }
}
