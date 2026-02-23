package com.minerasoftware.wiremock.graphql;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.matching.MatchResult;
import com.github.tomakehurst.wiremock.matching.RequestMatcherExtension;
import com.github.tomakehurst.wiremock.stubbing.SubEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
/**
 * Matches GraphQL POST requests by:
 * - operation document (request JSON field "query") - semantic matching via graphql-java engine
 * - optional operationName
 * - optional variables
 * Stub parameters:
 * - query: required (expected GraphQL document string)
 * - operationName: optional
 * - variables: optional (Map)
 */
public class GraphqlOperationRequestMatcherExtension extends RequestMatcherExtension {
    public static final String GRAPHQL_OPERATION_MATCHER = "GraphqlOperationRequestMatcher";
    private static final GraphqlOperationMatcher OPERATION_MATCHER = new GraphqlOperationMatcher();
    private static final GraphqlOperationNameMatcher OPERATION_NAME_MATCHER = new GraphqlOperationNameMatcher();
    private static final GraphqlVariableMatcher VARIABLE_MATCHER = new GraphqlVariableMatcher();

    @Override
    public String getName() {
        return GRAPHQL_OPERATION_MATCHER;
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

            GraphqlMatchContext context = new GraphqlMatchContext(requestJson, parameters);

            return MatchResult.aggregate(
                    OPERATION_MATCHER.match(context),
                    OPERATION_NAME_MATCHER.match(context),
                    VARIABLE_MATCHER.match(context)
            );
        } catch (Exception e) {
            return MatchResult.noMatch(SubEvent.error(e.getMessage()));
        }
    }

    /**
     * Programmatic stubbing helper:
     * stubFor(post("/graphql").andMatching(
     *     GRAPHQL_OPERATION_MATCHER,
     *     query("query {...}")
     * ))
     */
    public static Parameters query(String query) {
        return parameters(query).build();
    }

    /**
     * Programmatic stubbing helper for advanced configuration:
     * stubFor(post("/graphql").andMatching(
     *     GRAPHQL_OPERATION_MATCHER,
     *     parameters("query {...}").operationName("Name").variable("k", "v").build()
     * ))
     */
    public static ParametersBuilder parameters(String query) {
        return new ParametersBuilder(query);
    }

    public static final class ParametersBuilder {
        private final String query;
        private String operationName;
        private final Map<String, Object> variables = new LinkedHashMap<>();

        private ParametersBuilder(String query) {
            this.query = Objects.requireNonNull(query, "query must not be null");
        }

        public ParametersBuilder operationName(String operationName) {
            this.operationName = operationName;
            return this;
        }

        public ParametersBuilder variables(Map<String, ?> variables) {
            this.variables.clear();
            if (variables != null) {
                this.variables.putAll(variables);
            }
            return this;
        }

        public ParametersBuilder variable(String name, Object value) {
            this.variables.put(name, value);
            return this;
        }

        public Parameters build() {
            Map<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("query", query);

            if (operationName != null) {
                parameters.put("operationName", operationName);
            }

            if (!variables.isEmpty()) {
                parameters.put("variables", new LinkedHashMap<>(variables));
            }

            return Parameters.from(parameters);
        }
    }

}
