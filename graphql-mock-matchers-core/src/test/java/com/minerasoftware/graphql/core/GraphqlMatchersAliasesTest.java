package com.minerasoftware.graphql.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class GraphqlMatchersAliasesTest {

    private final GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

    @Test
    void defaultMatcherReturnsFalseWhenAliasExistsOnlyOnOneSide() {
        assertFalse(matcher.semanticallyEqual(
                "query { countries { countryName: name } }",
                "query { countries { name } }"
        ));
    }

    @Test
    void defaultMatcherReturnsFalseWhenAliasesDiffer() {
        assertFalse(matcher.semanticallyEqual(
                "query { countries { countryName: name } }",
                "query { countries { nameAlias: name } }"
        ));
    }
}
