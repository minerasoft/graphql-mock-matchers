package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.common.Json;
import com.github.tomakehurst.wiremock.extension.Parameters;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class GraphqlMatchContext {

    private final Map<String, Object> requestJson;
    private final Parameters parameters;

    GraphqlMatchContext(Map<String, Object> requestJson, Parameters parameters) {
        this.requestJson = requestJson == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(requestJson));
        this.parameters = parameters == null ? Parameters.empty() : parameters;
    }

    String requestString(String key) {
        return asString(requestObject(key));
    }

    String parameterString(String key) {
        return asString(parameterObject(key));
    }

    Object requestObject(String key) {
        return requestJson.get(key);
    }

    Object parameterObject(String key) {
        if (parameters != null && parameters.containsKey(key)) {
            return parameters.get(key);
        }
        return null;
    }

    String requestJsonOrNull(String key) {
        return jsonOrNull(requestObject(key));
    }

    String parameterJsonOrNull(String key) {
        return jsonOrNull(parameterObject(key));
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static String jsonOrNull(Object value) {
        return value == null ? null : Json.write(value);
    }
}
