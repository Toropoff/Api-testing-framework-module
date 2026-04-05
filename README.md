# REST API Test Framework (Monorepo)

Multi-module API test framework on Java 21 + Gradle + TestNG + REST Assured with domain separation, centralized retry, Allure reporting, and integration adapters.

## Branches

- **`main`** — production-ready configuration with full credential management (Owner property files, `SecretsProvider` chain for vault/env-var secrets).
- **`dev01`** — development branch with lightweight config. Uses system properties only, no secrets providers or property file resolution. Does not include production auth policies.

## Modules

- `framework-core` — HTTP client (REST Assured), auth (Basic Auth), config, filters.
- `framework-test-support` — base test classes, retry analyzer, listeners, `NetworkAwareMethodListener`, AssertJ DSL for API responses.
- `framework-reporting` — Allure TestNG listener + `AllureHttpFilter` for HTTP step logging (request/response attachments) + vendored `AllureAspectJ` for automatic assertion step generation.
- `framework-db-oracle` — Oracle datasource + jOOQ repositories + await helpers.
- `framework-messaging-rabbitmq` — message bus abstraction, RabbitMQ publish/consume, correlation-id checks.
- `framework-splunk` — placeholder, under separate review.
- `domains/postman-echo` — Postman Echo API domain (`endpoint/`, `model/`).
- `domains/open-holidays` — Open Holidays API domain (`endpoint/`, `model/`).
- `test-suites/smoke` — smoke suite.
- `test-suites/regression` — regression suite with retry and data providers.
- `test-suites/integration` — integration suite with schema + snapshot contract validation.
- `test-suites/public-api` — public API suite (Open Holidays).

## Test architecture

Tests follow a flat three-layer pattern: **Test → Api (POM) → HttpClient**, with an optional AssertJ DSL layer for API response assertions.

```java
public class OpenHolidaysPublicApiTest extends BaseApiTest {
    private OpenHolidaysApi openHolidaysApi;

    @Override protected String basePath() { return OpenHolidaysApi.basePath(); }
    @Override protected String targetApi() { return "open-holidays"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.openHolidaysApi = api(OpenHolidaysApi::new);
    }

    @Description("Verifies that GET /PublicHolidaysByDate returns 200 with public holidays conforming to schema and snapshot")
    @Test(description = "GET /PublicHolidaysByDate should return public holidays for a valid date")
    public void shouldReturnPublicHolidaysByDate() {
        var response = openHolidaysApi.getPublicHolidaysByDate("2024-12-25", "EN");

        ApiResponseAssert.assertThat(response)
                .hasStatus(200)
                .body()
                    .isNotEmpty()
                    .first()
                    .field("type").hasValue("Public")
                    .field("country.isoCode").isNotBlank()
                .matchesSchema("schemas/public-holidays-by-date.schema.json")
                .matchesSnapshot("public-holidays-by-date");
    }
}
```

Key patterns:
- `NetworkAwareMethodListener` — auto-converts network failures to skips (no try/catch in tests)
- `api(OpenHolidaysApi::new)` — constructs Api via method reference
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

# With auth
FRAMEWORK_CLIENT_NAME=my_user FRAMEWORK_CLIENT_SECRET=my_pass ./gradlew test
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

## Config and secrets

```bash
# Active profile
-Dframework.profile=dev|uat
```

Environment secret convention (`ApiClientFactory`):
- System.getenv("FRAMEWORK_CLIENT_NAME")
- System.getenv("FRAMEWORK_CLIENT_SECRET")

## Suite entry points

- Smoke: `test-suites/smoke/src/test/resources/testng-smoke.xml`
- Regression: `test-suites/regression/src/test/resources/testng-regression.xml`
- Integration: `test-suites/integration/src/test/resources/testng-integration.xml`
- Public API: `test-suites/public-api/src/test/resources/testng-public-api.xml`
