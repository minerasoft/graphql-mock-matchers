package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.common.Strings;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;
import com.minerasoftware.graphql.core.GraphqlMatcher;
import com.minerasoftware.graphql.core.GraphqlMatchers;

import java.util.List;

/**
 * WireMock-specific MatchResult implementation for semantic GraphQL operation matching.
 */
public final class GraphqlWireMockOperationMatchResult extends MatchResult {

    private final boolean exact;
    private final double distance;

    private GraphqlWireMockOperationMatchResult(
            boolean exact,
            double distance,
            String expectedCanonical,
            String actualCanonical
    ) {
        super(List.of(), new DiffDescription(expectedCanonical, actualCanonical, "GraphQL operation mismatch"));
        this.exact = exact;
        this.distance = distance;
    }

    @Override
    public boolean isExactMatch() {
        return exact;
    }

    @Override
    public double getDistance() {
        return distance;
    }

    public static MatchResult of(String expectedOperation, String actualOperation) {
        return of(expectedOperation, actualOperation, GraphqlMatchers.defaultMatcher());
    }

    static MatchResult of(String expectedOperation, String actualOperation, GraphqlMatcher matcher) {
        if (Strings.isNullOrEmpty(expectedOperation)) {
            return MatchResult.noMatch(SubEvent.error("expected operation is empty"));
        }
        if (Strings.isNullOrEmpty(actualOperation)) {
            return MatchResult.noMatch(SubEvent.error("actual operation is empty"));
        }

        try {
            boolean exact = matcher.semanticallyEqual(expectedOperation, actualOperation);

            String expectedCanonical = matcher.canonicalForm(expectedOperation);
            String actualCanonical = matcher.canonicalForm(actualOperation);

            double distance = exact ? 0 : Strings.normalisedLevenshteinDistance(
                    matcher.canonicalCompactForm(expectedOperation),
                    matcher.canonicalCompactForm(actualOperation)
            );

            return new GraphqlWireMockOperationMatchResult(exact, distance, expectedCanonical, actualCanonical);
        } catch (Exception e) {
            return MatchResult.noMatch(SubEvent.error(e.getMessage()));
        }
    }
}