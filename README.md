# REST API Test Framework (Monorepo)

Multi-module API test framework on Java 21 + Gradle + TestNG + REST Assured with domain separation, centralized retry, Allure reporting, and integration adapters.

## Branches

- **`main`** — production-ready configuration with full credential management (Owner property files, `SecretsProvider` chain for vault/env-var secrets).
- **`dev01`** — development branch with lightweight config. Uses system properties only, no secrets providers or property file resolution. Does not include production auth policies.

## Modules

- `framework-core` — HTTP client (REST Assured), auth (Basic Auth), config, filters.
- `framework-test-support` — base test classes, retry analyzer, listeners, `NetworkAwareMethodListener`.
- `framework-reporting` — Allure TestNG listener + native `AllureRestAssured` filter for HTTP step logging (request/response attachments, cURL preview).
- `framework-db-oracle` — Oracle datasource + jOOQ repositories + await helpers.
- `framework-messaging-rabbitmq` — message bus abstraction, RabbitMQ publish/consume, correlation-id checks.
- `framework-splunk` — placeholder, under separate review.
- `domains/postman-echo` — Postman Echo API domain (`endpoint/`, `model/`).
- `domains/open-holidays` — Open Holidays API domain (`endpoint/`, `model/`).
- `test-suites/tests-smoke` — smoke suite.
- `test-suites/tests-regression` — regression suite with retry and data providers.
- `test-suites/tests-integration` — integration suite with schema + snapshot contract validation.
- `test-suites/tests-public-api` — public API suite (Open Holidays).

## Test architecture (v0.02+)

Tests follow a flat three-layer pattern: **Test -> Api (POM) -> HttpClient**. No Flow or Assertions abstraction layers.

```java
public class PostmanEchoSmokeTest extends BaseApiTest {
    private PostmanEchoApi echoApi;

    @Override protected String basePath() { return PostmanEchoApi.basePath(); }
    @Override protected String targetApi() { return "postman-echo"; }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "initHttpClient")
    public void init() {
        this.echoApi = api(PostmanEchoApi::new);
    }

    // TODO: Placeholder for the test scenario description
    @Description("Verifies that GET /get echoes query parameters back in the response args map")
    @Test(description = "GET /get should echo query parameter")
    public void shouldEchoQueryParameter() {
        var response = echoApi.getEcho("suite", "smoke");

        Allure.step("Validate status 200 and echoed query parameter value", () -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.body().args()).containsEntry("suite", "smoke");
        });
    }
}
```

Key patterns:
- `NetworkAwareMethodListener` — auto-converts network failures to skips (no try/catch in tests)
- `api(PostmanEchoApi::new)` — constructs Api via method reference
- `dependsOnMethods = "initHttpClient"` — explicit ordering, no `super` calls

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

HTTP reporting is enabled by default for any test extending `BaseApiTest`. Native `AllureRestAssured` filter provides request/response attachments and cURL preview.

## Config and secrets

```bash
# Active profile
-Dframework.profile=dev|uat
```

Environment secret convention (`ApiClientFactory`):
- System.getenv("FRAMEWORK_CLIENT_NAME")
- System.getenv("FRAMEWORK_CLIENT_SECRET")

## Suite entry points

- Smoke: `test-suites/tests-smoke/src/test/resources/testng-smoke.xml`
- Regression: `test-suites/tests-regression/src/test/resources/testng-regression.xml`
- Integration: `test-suites/tests-integration/src/test/resources/testng-integration.xml`
- Public API: `test-suites/tests-public-api/src/test/resources/testng-public-api.xml`
