package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.matching.AbsentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToJsonPattern;
import com.github.tomakehurst.wiremock.matching.MatchResult;

final class GraphqlVariableMatcher {

    private static final boolean IGNORE_ARRAY_ORDER = false;
    private static final boolean IGNORE_EXTRA_ELEMENTS = false;

    MatchResult match(GraphqlMatchContext context) {
        String expectedVarsJson = context.parameterJsonOrNull("variables");
        String actualVarsJson = context.requestJsonOrNull("variables");
        return expectedVarsJson == null
                ? AbsentPattern.ABSENT.match(actualVarsJson)
                : new EqualToJsonPattern(
                        expectedVarsJson,
                        IGNORE_ARRAY_ORDER,
                        IGNORE_EXTRA_ELEMENTS
                ).match(actualVarsJson);
    }
}
