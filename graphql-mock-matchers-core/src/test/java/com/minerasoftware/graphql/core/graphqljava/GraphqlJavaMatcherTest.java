package com.minerasoftware.graphql.core.graphqljava;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlJavaMatcherTest {

    private final GraphqlJavaMatcher matcher = new GraphqlJavaMatcher();

    @Test
    void semanticallyEqualReturnsTrueForEquivalentOperationWithDifferentFieldOrder() {
        assertTrue(matcher.semanticallyEqual(
                "query Countries { countries { name currency } }",
                "query Countries { countries { currency name } }"
        ));
    }

    @Test
    void semanticallyEqualReturnsFalseForDifferentOperations() {
        assertFalse(matcher.semanticallyEqual(
                "query { countries { name } }",
                "query { countries { code } }"
        ));
    }

    @Test
    void canonicalFormNormalizesEquivalentOperationsToSameValue() {
        String first = matcher.canonicalForm("query { countries { name currency } }");
        String second = matcher.canonicalForm("query { countries { currency name } }");

        assertEquals(first, second);
    }

    @Test
    void canonicalCompactFormNormalizesEquivalentOperationsToSameValue() {
        String first = matcher.canonicalCompactForm("query { countries { name currency } }");
        String second = matcher.canonicalCompactForm("query { countries { currency name } }");

        assertEquals(first, second);
    }

    @Test
    void methodsThrowWhenOperationIsInvalid() {
        String invalidOperation = "query {";

        assertThrows(RuntimeException.class, () -> matcher.semanticallyEqual(invalidOperation, "query { ping }"));
        assertThrows(RuntimeException.class, () -> matcher.canonicalForm(invalidOperation));
        assertThrows(RuntimeException.class, () -> matcher.canonicalCompactForm(invalidOperation));
    }
}
