package com.minerasoftware.wiremock.graphql;

import com.github.tomakehurst.wiremock.matching.AbsentPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.MatchResult;

final class GraphqlOperationNamePattern {

    MatchResult match(GraphqlMatchContext context) {
        String expectedOperationName = context.parameterString("operationName");
        String actualOperationName = context.requestString("operationName");
        return expectedOperationName == null
                ? AbsentPattern.ABSENT.match(actualOperationName)
                : new EqualToPattern(expectedOperationName).match(actualOperationName);
    }
}
