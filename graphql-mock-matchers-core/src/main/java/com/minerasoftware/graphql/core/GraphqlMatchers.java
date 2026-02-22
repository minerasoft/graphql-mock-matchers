package com.minerasoftware.graphql.core;

import com.minerasoftware.graphql.core.graphqljava.GraphqlJavaMatcher;


public final class GraphqlMatchers {

    private static final GraphqlMatcher DEFAULT = new GraphqlJavaMatcher();

    private GraphqlMatchers() {}

    public static GraphqlMatcher defaultMatcher() {
        return DEFAULT;
    }

}
