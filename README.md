# REST API Test Framework

Multi-module API test framework on Java 21 + Gradle + TestNG + REST Assured with domain separation, centralized retry, Allure reporting, and integration adapters.

## Branches

## Modules

Active (included in build):

- `framework-core` — HTTP client (REST Assured), auth (Basic Auth), config, filters.
- `framework-test-support` — base test classes, retry analyzer, `NetworkAwareMethodListener`, AssertJ DSL for API responses.
- `framework-reporting` — Allure TestNG listener + `AllureHttpFilter` for HTTP step logging (request/response attachments) + vendored `AllureAspectJ` for automatic assertion step generation.
- `framework-splunk` — Splunk REST client, SPL query builder, AssertJ assertion DSL (`SplunkResponseAssert`, `SplunkResultAssert`).
- `framework-bundle` — aggregation entry point; the single dependency consumers import.

Skeletons (excluded from build, not compiled):

- `framework-db-oracle` — Oracle datasource + jOOQ repositories + await helpers.
- `framework-messaging-rabbitmq` — message bus abstraction, RabbitMQ publish/consume, correlation-id checks.

## Test architecture

Tests follow a flat pattern: **Test → `call()` → HttpClient**, with an AssertJ DSL layer for API response assertions.

```java
public class PostmanEchoIntegrationTest extends BaseApiTest {

    @Override
    protected String domain() { return "postman-echo"; }

    @Description("Verifies that GET /get response conforms to the JSON schema and matches the golden-file snapshot")
    @Test(description = "GET /get should match schema and snapshot contract")
    public void shouldMatchEchoGetContractAndSnapshot() {
        var response = call("get-echo", String.class)
                .query("suite", "integration")
                .send();

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                .field("args.suite").hasValue("integration")
                .matchesSchema("schemas/postman-echo-get.schema.json")
                .matchesSnapshot("postman-echo-get");
    }
}
```

Key patterns:
- `NetworkAwareMethodListener` — auto-converts network failures to skips (no try/catch in tests)
- `dependsOnMethods = "initHttpClient"` — explicit ordering, no `super` calls
- **No `Allure.step()` in tests** — Allure steps for assertions are generated automatically via AspectJ LTW (`AllureAspectJ`). Each assertion method call (`hasStatus`, `isNotEmpty`, `field`, etc.) produces a named step in the report.

### AssertJ DSL

`framework-test-support` provides a generic fluent `ApiResponse<T>` assertion DSL built on Jackson `JsonNode` navigation:

| Class | Role |
|---|---|
| `ApiResponseAssert<T>` | Entry point. `assertThat(response)` → fluent chain. Methods: `hasStatus(int)`, `body()`. |
| `AbstractApiResponseAssert` | Base class. Parses `rawBody` once (lazy-cached `JsonNode`). Produces `BodyAssert` via `body()`. |
| `BodyAssert` | Body-level assertions. `isNotEmpty()`, `at(int)`, `first()`, `field(String dotPath)`, `hasField(String dotPath)`, `matchesSchema(String)`, `matchesSnapshot(String)`. |
| `FieldAssert` | Field-level terminal assertions. `hasValue(Object)`, `isNotBlank()`, `isNotEmpty()`, `isPresent()`. All terminals return `BodyAssert` for continued chaining. |

Chain return types: `.body()` → `BodyAssert` → `.field("x")` → `FieldAssert` → `.hasValue(...)` → `BodyAssert` → `.matchesSchema(...)` → `BodyAssert`.

Schema and snapshot validation always operate on the **full original response JSON** (`rawBody`), regardless of how deep `.first()` or `.at(i)` has navigated the `JsonNode` cursor. `.matchesSchema()` / `.matchesSnapshot()` are only on `BodyAssert` — calling them on `FieldAssert` is a compile error by design.

## Running tests

```bash
# All tests
./gradlew test

# Specific suite
./gradlew :test-suites:smoke:test
./gradlew :test-suites:regression:test
./gradlew :test-suites:integration:test
./gradlew :test-suites:public-api:test

# With retry
./gradlew test -Dtest.retry.maxRetries=2 -Dtest.retry.delayMs=500

# Single class
./gradlew :test-suites:smoke:test -Dtest.class=...

#Aggregate results
./gradlew collectAllureResults 
```


## Allure reports

```bash
# Build report from existing results
./gradlew allureReport

# Run all suites + build report
./gradlew allureReportWithTests

# Serve report locally
./gradlew allureServe
```

Report path: `build/reports/allure-report/allureReport/index.html`

HTTP reporting is enabled by default for any test extending `BaseApiTest`. `AllureHttpFilter` provides request/response attachments with sensitive-data masking. Assertion steps (e.g. `status 200`, `matches schema`) are generated automatically by `AllureAspectJ` via AspectJ LTW — no manual `Allure.step()` needed in tests. Navigation steps (`body`, `first`, `at`) and framework-internal assertion types are filtered from the report automatically.


## Suite entry points

- Smoke: `test-suites/smoke/src/test/resources/testng-smoke.xml`
- Regression: `test-suites/regression/src/test/resources/testng-regression.xml`
- Integration: `test-suites/integration/src/test/resources/testng-integration.xml`
- Public API: `test-suites/public-api/src/test/resources/testng-public-api.xml`
