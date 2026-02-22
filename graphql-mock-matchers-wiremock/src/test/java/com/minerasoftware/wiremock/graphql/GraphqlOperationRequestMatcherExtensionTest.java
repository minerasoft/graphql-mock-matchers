package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.extension.Parameters;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlOperationRequestMatcherExtensionTest {

    private final GraphqlOperationRequestMatcherExtension extension =
            new GraphqlOperationRequestMatcherExtension();

    @Test
    void getNameReturnsExtensionName() {
        assertEquals(GraphqlOperationRequestMatcherExtension.GRAPHQL_OPERATION_MATCHER, extension.getName());
    }

    @Test
    void queryHelperBuildsQueryOnlyParameters() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.query("query { ping }");

        assertTrue(parameters.containsKey("query"));
        assertEquals("query { ping }", parameters.getString("query"));
        assertFalse(parameters.containsKey("operationName"));
        assertFalse(parameters.containsKey("variables"));
    }

    @Test
    void operationBuilderCanBuildQueryOnlyParameters() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query { ping }")
                .build();

        assertTrue(parameters.containsKey("query"));
        assertEquals("query { ping }", parameters.getString("query"));
        assertFalse(parameters.containsKey("operationName"));
        assertFalse(parameters.containsKey("variables"));
    }

    @Test
    void operationBuilderCanIncludeOperationName() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query Ping { ping }")
                .operationName("Ping")
                .build();

        assertTrue(parameters.containsKey("query"));
        assertEquals("query Ping { ping }", parameters.getString("query"));
        assertTrue(parameters.containsKey("operationName"));
        assertEquals("Ping", parameters.getString("operationName"));
        assertFalse(parameters.containsKey("variables"));
    }

    @Test
    void operationBuilderCanIncludeVariables() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query Country($code: String!) { country(code: $code) { name } }")
                .variables(Map.of("code", "AD"))
                .build();

        assertTrue(parameters.containsKey("query"));
        assertTrue(parameters.containsKey("variables"));
        assertEquals("AD", ((Map<?, ?>) parameters.get("variables")).get("code"));
        assertFalse(parameters.containsKey("operationName"));
    }

    @Test
    void operationBuilderCanIncludeOperationNameAndVariables() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query Country($code: String!) { country(code: $code) { name } }")
                .operationName("Country")
                .variables(Map.of("code", "AD"))
                .build();

        assertTrue(parameters.containsKey("query"));
        assertEquals("query Country($code: String!) { country(code: $code) { name } }", parameters.getString("query"));
        assertTrue(parameters.containsKey("operationName"));
        assertEquals("Country", parameters.getString("operationName"));
        assertTrue(parameters.containsKey("variables"));
        assertEquals("AD", ((Map<?, ?>) parameters.get("variables")).get("code"));
    }

    @Test
    void operationBuilderCanBuildReadableParameters() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query Country($code: String!) { country(code: $code) { name } }")
                .operationName("Country")
                .variable("code", "AD")
                .build();

        assertEquals("query Country($code: String!) { country(code: $code) { name } }", parameters.getString("query"));
        assertEquals("Country", parameters.getString("operationName"));
        assertEquals("AD", ((Map<?, ?>) parameters.get("variables")).get("code"));
    }

    @Test
    void operationBuilderVariablesCallCanReplaceVariableMap() {
        Parameters parameters = GraphqlOperationRequestMatcherExtension.parameters("query Country($code: String!) { country(code: $code) { name } }")
                .variable("unused", "value")
                .variables(Map.of("code", "AD"))
                .build();

        assertEquals("AD", ((Map<?, ?>) parameters.get("variables")).get("code"));
        assertFalse(((Map<?, ?>) parameters.get("variables")).containsKey("unused"));
    }
}
