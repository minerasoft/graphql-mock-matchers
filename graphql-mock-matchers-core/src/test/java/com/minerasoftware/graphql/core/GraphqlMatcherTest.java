package com.minerasoftware.graphql.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraphqlMatcherTest {

    @Test
    void canonicalCompactFormDefaultsToCanonicalForm() {
        GraphqlMatcher matcher = new GraphqlMatcher() {
            @Override
            public boolean semanticallyEqual(String expectedOperation, String actualOperation) {
                return expectedOperation.equals(actualOperation);
            }

            @Override
            public String canonicalForm(String operation) {
                return "canonical:" + operation;
            }
        };

        assertEquals("canonical:query { ping }", matcher.canonicalCompactForm("query { ping }"));
    }
}
