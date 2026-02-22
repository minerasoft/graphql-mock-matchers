package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.minerasoftware.wiremock.graphql.TestData.request;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlOperationRequestMatcherExtensionVariablesTest {

    private final GraphqlOperationRequestMatcherExtension extension =
            new GraphqlOperationRequestMatcherExtension();

    @Test
    void variablesMatchWhenExactlyEqual() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name } }",
                          "variables": { "code": "AD" }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Country($code: String!) { country(code: $code) { name } }",
                        "variables", Map.of("code", "AD")
                ))
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenRequestContainsExtraVariables() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name } }",
                          "variables": { "code": "AD", "extra": 123 }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Country($code: String!) { country(code: $code) { name } }",
                        "variables", Map.of("code", "AD")
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenRequestProvidesVariablesButExpectedDoesNot() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name } }",
                          "variables": { "code": "AD" }
                        }
                        """),
                Parameters.one(
                        "query", "query Country($code: String!) { country(code: $code) { name } }"
                )
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenVariableArrayOrderDiffers() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query CountryFilter($codes: [String!]!) { countries(codes: $codes) { code } }",
                          "variables": { "codes": ["AD", "FR", "ES"] }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query CountryFilter($codes: [String!]!) { countries(codes: $codes) { code } }",
                        "variables", Map.of("codes", new String[]{"ES", "AD", "FR"})
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void matchesWhenVariableArrayOrderIsTheSame() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query CountryFilter($codes: [String!]!) { countries(codes: $codes) { code } }",
                          "variables": { "codes": ["AD", "FR", "ES"] }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query CountryFilter($codes: [String!]!) { countries(codes: $codes) { code } }",
                        "variables", Map.of("codes", new String[]{"AD", "FR", "ES"})
                ))
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void matchesWhenQueryWithVariablesIsSemanticallyEquivalent() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name code } }",
                          "variables": { "code": "AD" }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Country($code: String!) { country(code: $code) { code name } }",
                        "variables", Map.of("code", "AD")
                ))
        );

        assertTrue(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenVariablesDoNotMatch() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name } }",
                          "variables": { "code": "AD" }
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Country($code: String!) { country(code: $code) { name } }",
                        "variables", Map.of("code", "US")
                ))
        );

        assertFalse(result.isExactMatch());
    }

    @Test
    void returnsNoMatchWhenExpectedVariablesAreMissingInRequest() {
        MatchResult result = extension.match(
                request("""
                        {
                          "query": "query Country($code: String!) { country(code: $code) { name } }"
                        }
                        """),
                Parameters.from(Map.of(
                        "query", "query Country($code: String!) { country(code: $code) { name } }",
                        "variables", Map.of("code", "AD")
                ))
        );

        assertFalse(result.isExactMatch());
    }
}
