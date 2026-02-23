package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.common.Strings;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;
import com.minerasoftware.graphql.core.GraphqlMatcher;
import com.minerasoftware.graphql.core.GraphqlMatchers;

import java.util.List;

final class GraphqlOperationMatcher {

    private final GraphqlMatcher matcher;

    GraphqlOperationMatcher() {
        this(GraphqlMatchers.defaultMatcher());
    }

    GraphqlOperationMatcher(GraphqlMatcher matcher) {
        this.matcher = matcher;
    }

    MatchResult match(GraphqlMatchContext context) {
        String expectedQuery = context.parameterString("query");
        String actualOperation = context.requestString("query");

        if (Strings.isNullOrEmpty(expectedQuery)) {
            return MatchResult.noMatch(SubEvent.error("expected operation is empty"));
        }
        if (Strings.isNullOrEmpty(actualOperation)) {
            return MatchResult.noMatch(SubEvent.error("actual operation is empty"));
        }

        try {
            boolean exact = matcher.semanticallyEqual(expectedQuery, actualOperation);

            String expectedCanonical = matcher.canonicalForm(expectedQuery);
            String actualCanonical = matcher.canonicalForm(actualOperation);

            double distance = exact ? 0 : Strings.normalisedLevenshteinDistance(
                    matcher.canonicalCompactForm(expectedQuery),
                    matcher.canonicalCompactForm(actualOperation)
            );

            return new OperationMatchResult(exact, distance, expectedCanonical, actualCanonical);
        } catch (Exception e) {
            return MatchResult.noMatch(SubEvent.error(e.getMessage()));
        }
    }

    private static final class OperationMatchResult extends MatchResult {

        private final boolean exact;
        private final double distance;

        private OperationMatchResult(
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
    }
}
