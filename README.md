# graphql-mock-matchers

Semantic GraphQL request matching utilities, with WireMock integration.

## Modules
- `graphql-mock-matchers-core`: GraphQL semantic matcher based on `graphql-java`.
- `graphql-mock-matchers-wiremock`: WireMock request matcher extension for GraphQL query/operationName/variables.
- `examples-wiremock-junit5`: runnable JUnit 5 examples.

## Requirements
- Java 21
- Gradle Wrapper (`./gradlew`)

## Quick Start (WireMock DSL)
```java
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.GRAPHQL_OPERATION_MATCHER;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.query;

stubFor(post(urlEqualTo("/graphql"))
    .andMatching(GRAPHQL_OPERATION_MATCHER, query("query { countries { name currency } }"))
    .willReturn(okJson("{\"data\":{}}")));
```

Advanced parameters:
```java
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.GRAPHQL_OPERATION_MATCHER;
import static com.minerasoftware.wiremock.graphql.GraphqlOperationRequestMatcherExtension.parameters;

.andMatching(
  GRAPHQL_OPERATION_MATCHER,
  parameters("query Country($code: String!) { country(code: $code) { name } }")
    .operationName("Country")
    .variable("code", "AD")
    .build()
)
```

## Build
```bash
./gradlew test
```

## Publishing (future Maven Central)
Library modules are configured with `maven-publish` + `signing`:
- `graphql-mock-matchers-core`
- `graphql-mock-matchers-wiremock`

To publish, provide credentials/keys via Gradle properties or environment variables:
- `OSSRH_USERNAME`, `OSSRH_PASSWORD`
- `SIGNING_KEY`, `SIGNING_PASSWORD`

## License
Apache License 2.0.
