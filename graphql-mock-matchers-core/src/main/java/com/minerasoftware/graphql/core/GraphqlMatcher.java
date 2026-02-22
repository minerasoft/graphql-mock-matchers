package com.minerasoftware.graphql.core;

/**
 * Strategy SPI for comparing GraphQL operation documents (query/mutation/subscription).
 * "operation" here means the GraphQL document string carried in the HTTP "query" field.
 */
public interface GraphqlMatcher {

    boolean semanticallyEqual(String expectedOperation, String actualOperation);

    /**
     * Canonical representation suitable for diff output.
     */
    String canonicalForm(String operation);

    /**
     * More compact canonical representation suitable for distance calculations.
     * By default, this is the same as the canonical form, but implementations may choose to provide a more compact representation if it can be computed more efficiently.
     */
    default String canonicalCompactForm(String operation) {
        return canonicalForm(operation);
    }
}
