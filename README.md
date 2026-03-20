# graphql-mock-matchers

Semantic GraphQL request matching utilities, with WireMock integration.

## Why Use This Library
- GraphQL semantic matching instead of raw string matching.
- Field/projection order differences do not break stubs.
- Optional `operationName` and `variables` matching for stricter request contracts.
- Works with both WireMock DSL and JSON mappings.

## Modules
- `graphql-mock-matchers-core`: GraphQL semantic matcher based on `graphql-java`.
- `graphql-mock-matchers-wiremock`: WireMock request matcher extension for GraphQL query/operationName/variables.
- `examples-wiremock-junit5`: runnable JUnit 5 examples.

## Requirements
- Java 21
- Gradle Wrapper (`./gradlew`)

## Dependency
WireMock users will typically want `graphql-mock-matchers-wiremock`.

Gradle:
```groovy
dependencies {
    testImplementation "io.github.minerasoft:graphql-mock-matchers-wiremock:<version>"
}
```

Maven:
```xml
<dependency>
  <groupId>io.github.minerasoft</groupId>
  <artifactId>graphql-mock-matchers-wiremock</artifactId>
  <version>VERSION</version>
  <scope>test</scope>
</dependency>
```

## Quick Start (WireMock DSL)
```java
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.graphqlRequest;

stubFor(post(urlEqualTo("/graphql"))
    .andMatching(graphqlRequest("query { countries { name currency } }"))
    .willReturn(okJson("{\"data\":{}}")));
```

This request still matches semantically (same fields, different order):
```json
{
  "query": "query { countries { currency name } }"
}
```

Advanced parameters:
```java
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.graphqlRequest;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.parameters;

.andMatching(graphqlRequest(
  parameters("query Country($code: String!) { country(code: $code) { name } }")
    .operationName("Country")
    .variable("code", "AD")
    .build()
))
```

## JSON Mapping Example
```json
{
  "request": {
    "method": "POST",
    "url": "/graphql",
    "customMatcher": {
      "name": "GraphqlOperationRequestMatcher",
      "parameters": {
        "query": "query { countries { name currency } }"
      }
    }
  },
  "response": {
    "status": 200
  }
}
```

## Full Runnable Examples
- DSL usage: [WireMockDslGraphqlMatcherExampleTest.java](examples-wiremock-junit5/src/test/java/com/minerasoftware/examples/wiremock/junit5/WireMockDslGraphqlMatcherExampleTest.java)
- JSON mapping usage: [WireMockJsonMappingsWithGraphqlMatcherExampleTest.java](examples-wiremock-junit5/src/test/java/com/minerasoftware/examples/wiremock/junit5/WireMockJsonMappingsWithGraphqlMatcherExampleTest.java)
- Mapping files: [examples-wiremock-junit5/src/test/resources/mappings/](examples-wiremock-junit5/src/test/resources/mappings/)

## Build
```bash
./gradlew test
```

## License
Apache License 2.0.
