package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.http.ImmutableRequest;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.RequestMethod;

import java.nio.charset.StandardCharsets;

final class TestData {

    private TestData() {
    }

    static Request request(String body) {
        return ImmutableRequest.create()
                .withAbsoluteUrl("http://localhost/graphql")
                .withMethod(RequestMethod.POST)
                .withBody(body.getBytes(StandardCharsets.UTF_8))
                .build();
    }

    static Request requestWithoutBody() {
        return ImmutableRequest.create()
                .withAbsoluteUrl("http://localhost/graphql")
                .withMethod(RequestMethod.POST)
                .build();
    }
}
