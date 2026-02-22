package com.minerasoftware.graphql.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphqlMatchersFragmentsTest {

    private final GraphqlMatcher matcher = GraphqlMatchers.defaultMatcher();

    @Test
    void defaultMatcherHandlesEquivalentNamedFragments() {
        assertTrue(matcher.semanticallyEqual(
                """
                query Countries {
                  countries {
                    ...CountryFields
                  }
                }

                fragment CountryFields on Country {
                  name
                  currency
                }
                """,
                """
                query Countries {
                  countries {
                    ...CountryFields
                  }
                }

                fragment CountryFields on Country {
                  currency
                  name
                }
                """
        ));
    }

    @Test
    void defaultMatcherReturnsFalseWhenFragmentContentDiffersWithSameName() {
        assertFalse(matcher.semanticallyEqual(
                """
                query Countries {
                  countries {
                    ...CountryFields
                  }
                }

                fragment CountryFields on Country {
                  name
                  currency
                }
                """,
                """
                query Countries {
                  countries {
                    ...CountryFields
                  }
                }

                fragment CountryFields on Country {
                  name
                  code
                }
                """
        ));
    }
}
