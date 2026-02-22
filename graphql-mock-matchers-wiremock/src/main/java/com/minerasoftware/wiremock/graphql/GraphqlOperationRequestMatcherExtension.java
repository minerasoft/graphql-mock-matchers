package com.minerasoftware.wiremock.graphql;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.common.Strings;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;

import java.util.Map;
/**
 * Matches GraphQL POST requests by:
 * - operation document (request JSON field "query") - semantic matching via graphql-java engine
 * - optional operationName
 * - optional variables
 *
 * Stub parameters:
 * - operation: required (expected GraphQL document string)
 * - operationName: optional
 * - variables: optional (Map)
 */
public class GraphqlOperationRequestMatcherExtension extends RequestMatcherExtension {
    public static final String NAME = "GraphqlOperationRequestMatcher";

    private final String defaultExpectedOperation;

    /**
     * Required for JSON mapping usage (WireMock instantiates extensions with no-arg constructor).
     */
    public GraphqlOperationRequestMatcherExtension() {
        this(null);
    }

    /**
     * Programmatic use (andMatching(graphqlOperation("..."))).
     */
    private GraphqlOperationRequestMatcherExtension(String defaultExpectedOperation) {
        this.defaultExpectedOperation = defaultExpectedOperation;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public MatchResult match(Request request, Parameters parameters) {
        try {
            Map<String, Object> requestJson = Json.read(
                    request.getBodyAsString(),
                    new TypeReference<>() {
                    }
            );

            if (requestJson == null || requestJson.isEmpty()) {
                return MatchResult.noMatch(SubEvent.error("Request body is empty"));
            }

            // ---- Actual request fields (GraphQL over HTTP) ----
            String actualOperation = asString(requestJson.get("query"));          // required by spec
            String actualOperationName = asString(requestJson.get("operationName")); // optional
            Object actualVariablesObj = requestJson.get("variables");               // optional

            if (Strings.isNullOrEmpty(actualOperation)) {
                return MatchResult.noMatch(SubEvent.error("Request body does not contain a 'query' field"));
            }

            // ---- Expected (from stub parameters) ----
            String expectedOperation = getString(parameters, "operation", defaultExpectedOperation);
            if (Strings.isNullOrEmpty(expectedOperation)) {
                return MatchResult.noMatch(SubEvent.error("Expected operation not configured (use parameters.operation or graphqlOperation(...))"));
            }

            String expectedOperationName = getString(parameters, "operationName", null); // optional
            Object expectedVariablesObj = getObject(parameters, "variables");            // optional

            // ---- 1) Operation match (required) ----
            MatchResult opResult = GraphqlWireMockOperationMatchResult.of(expectedOperation, actualOperation);
            if (!opResult.isExactMatch()) {
                return opResult;
            }

            // ---- 2) operationName match (optional) ----
            if (expectedOperationName != null) {
                if (!new EqualToPattern(expectedOperationName).match(actualOperationName).isExactMatch()) {
                    return MatchResult.noMatch(SubEvent.error("operationName mismatch"));
                }
            }

            // ---- 3) variables match (optional; subset by default) ----
            if (expectedVariablesObj != null) {
                if (actualVariablesObj == null) {
                    return MatchResult.noMatch(SubEvent.error("variables mismatch"));
                }

                String expectedVarsJson = Json.write(expectedVariablesObj);
                String actualVarsJson = Json.write(actualVariablesObj);

                boolean ignoreArrayOrder = true;
                boolean ignoreExtraElements = true; // subset semantics

                boolean varsMatch = new EqualToJsonPattern(
                        expectedVarsJson,
                        ignoreArrayOrder,
                        ignoreExtraElements
                ).match(actualVarsJson).isExactMatch();

                if (!varsMatch) {
                    return MatchResult.noMatch(SubEvent.error("variables mismatch"));
                }
            }

            return MatchResult.exactMatch();

        } catch (Exception e) {
            return MatchResult.noMatch(SubEvent.error(e.getMessage()));
        }
    }

    /**
     * Programmatic stubbing helper:
     * stubFor(post("/graphql").andMatching(graphqlOperation("query {...}")) ...)
     */
    public static RequestMatcherExtension graphqlOperation(String operation) {
        return new GraphqlOperationRequestMatcherExtension(operation);
    }

    // ---------- helpers ----------

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static String getString(Parameters p, String key, String fallback) {
        if (p != null && p.containsKey(key)) {
            String v = p.getString(key);
            return Strings.isNullOrEmpty(v) ? fallback : v;
        }
        return fallback;
    }

    private static Object getObject(Parameters p, String key) {
        if (p != null && p.containsKey(key)) {
            return p.get(key);
        }
        return null;
    }

    @Override
    public String getExpected() {
        // For JSON mappings, expected operation is per-stub (parameters), so this is best-effort for diagnostics.
        return defaultExpectedOperation;
    }
}
