# REST API Test Framework (Monorepo)

Multi-module API test framework on Java 21 + Gradle + TestNG + Rest Assured with API POM, centralized retry, reporting, and integration adapters.

## Modules

- `framework-core` - HTTP abstraction, request/response model, auth strategies, profile config, secrets providers, filters and masking, error model.
- `examples/sample-domain` - sample domain for Postman Echo (`endpoint`, `flow`, `assertions`, `model`).
- `framework-testng` - base test classes, retry analyzer, listeners, soft assertions, data providers.
- `framework-db-oracle` - Oracle datasource + jOOQ repositories + await helpers.
- `framework-messaging-rabbitmq` - message bus abstraction, RabbitMQ publish/consume, correlation-id checks, await helpers.
- `framework-contracts` - JSON schema validation, JsonUnit assertions, snapshot checks.
- `framework-reporting` - Allure TestNG listener + request/response attachments with masking.
- `framework-suite-support` - aggregated dependencies for suite modules.
- `test-suites/tests-smoke` - smoke suite examples.
- `test-suites/tests-regression` - regression suite examples.
- `test-suites/tests-integration` - integration suite examples.

## Key Patterns Implemented

- TestNG centralized retry (`FrameworkRetryAnalyzer`) with:
  - global policy via JVM props (`test.retry.maxRetries`, `test.retry.delayMs`),
  - custom policy via `@RetrySetting` on class/method,
  - attempt logging and flaky report (`RetryReportingListener`).
- API POM:
  - Endpoint Objects (`PostmanEchoApi`),
  - Flow Objects (`EchoFlow`),
  - Assertion Helpers (`EchoAssertions`).
- Domain DTOs are kept in `examples/sample-domain/model` to avoid coupling framework-core to a specific API.
- Auth strategies:
  - `BasicAuthStrategy`,
  - `OAuth2ClientCredentialsStrategy`,
  - `OAuth2PasswordStrategy` with thread-safe token cache, TTL-aware refresh and 401 fallback refresh.
- Config and secrets:
  - profiles (`dev/stage/prod`),
  - `EnvSecretsProvider`,
  - `VaultSecretsProvider`,
  - centralized resolver `ConfigResolver`.
- Unified HTTP filters:
  - request/response logging,
  - correlation-id,
  - timing,
  - sensitive masking.
- Contracts and snapshots:
  - JSON schema (`JsonSchemaContractValidator`),
  - JsonUnit assertions,
  - snapshot checker (`SnapshotContractChecker`).
- Reporting:
  - Allure TestNG listener,
  - auto-attachments for request/response,
  - masking for headers and body fields.

## Running Tests

### Quick start

Run all tests:

```bash
./gradlew test
```

> Note: this repository keeps wrapper scripts and properties, but excludes `gradle/wrapper/gradle-wrapper.jar` from VCS to avoid binary artifacts in PRs. Regenerate it locally with `gradle wrapper` if needed.

Run a specific suite module:

```bash
./gradlew :test-suites:smoke:test
./gradlew :test-suites:regression:test
./gradlew :test-suites:integration:test
```

### Automatic CLI parameter pickup

Framework parameters passed from command line are now forwarded automatically into test JVMs.
You can use either JVM properties (`-D`) or Gradle project properties (`-P`) with prefixes `framework.`, `test.`, `auth.`.

Examples:

```bash
./gradlew :test-suites:smoke:test -Dframework.profile=stage -Dframework.runLiveTests=true
./gradlew :test-suites:regression:test -Ptest.retry.maxRetries=3 -Ptest.retry.delayMs=500
./gradlew :test-suites:integration:test -Pauth.basic.username=my_user -Pauth.basic.password=my_pass
```

You can combine `-D` and `-P` in one command.



### HTTP reporting is enabled by default via `BaseApiTest`

Any test extending `BaseApiTest` automatically gets reporting-aware HTTP filter policy:

- `framework.reporting.httpSteps.enabled=true` by default,
- `framework.reporting.attachments.enabled=true` by default,
- request/response/metadata attachments are generated in Allure per HTTP call,
- can be turned off only via system properties:

```bash
-Dframework.reporting.httpSteps.enabled=false
-Dframework.reporting.attachments.enabled=false
```

`BaseApiTest` still allows local override of `filterPolicy()` for special scenarios.

## Allure Reports

The root project uses the Allure Gradle plugin configured via the Kotlin DSL `plugins {}` block.
Before report generation, root task `collectAllureResults` aggregates results from:

- `test-suites/tests-smoke/allure-results`
- `test-suites/tests-regression/allure-results`
- `test-suites/tests-integration/allure-results`

into a single source directory:

- `build/allure-results`

`allureReport` depends only on this aggregation step and builds HTML from the aggregated catalog.

### Allure tasks in root project

- `collectAllureResults` - collects existing results from suite modules into `build/allure-results`.
- `runSuitesForAllure` - explicitly runs smoke/regression/integration tests to prepare fresh Allure results (for this flow suite tests are forced to execute, not taken from up-to-date cache).
- `allureReport` - builds HTML report from already existing/collected results (does **not** run test suites by itself).
- `allureReportWithTests` - orchestration task: runs `runSuitesForAllure` and then generates `allureReport`.

1) Build aggregated HTML report from existing results:

```bash
./gradlew allureReport
```

2) Run all suites and then build aggregated report:

```bash
./gradlew allureReportWithTests
```

Generated report path:

- `build/reports/allure-report/allureReport/index.html`

3) Open report locally with temporary web server:

```bash
./gradlew allureServe
```

Notes:
- Use only plugin-based Allure tasks (`allureReport`, `allureServe`) and avoid duplicate manual task declarations with the same names.
- If tests are skipped (for example, live API disabled), Allure still generates a report with skipped status.

## Run Profiles and Secrets

Active profile:

```bash
-Dframework.profile=dev|stage|prod
```

Live API tests are disabled by default. Enable explicitly:

```bash
-Dframework.runLiveTests=true
```

Global retry policy:

```bash
-Dtest.retry.maxRetries=2 -Dtest.retry.delayMs=500
```

Environment secret convention (`EnvSecretsProvider`):

- `FRAMEWORK_SECRET_AUTH_BASIC_USERNAME`
- `FRAMEWORK_SECRET_AUTH_BASIC_PASSWORD`
- `FRAMEWORK_SECRET_AUTH_OAUTH2_CLIENTSECRET`
- etc.

## Suite Entry Points

- Smoke: `test-suites/tests-smoke/src/test/resources/testng-smoke.xml`
- Regression: `test-suites/tests-regression/src/test/resources/testng-regression.xml`
- Integration: `test-suites/tests-integration/src/test/resources/testng-integration.xml`

## Notes

- Oracle and RabbitMQ modules are implemented as reusable adapters and require runtime infra credentials/hosts.

## Retry Extensions (Pluggable)

You can swap retry behavior without changing framework code:

```bash
-Dtest.retry.predicateClass=com.example.CustomRetryPredicate
-Dtest.retry.delayStrategyClass=com.example.ExponentialBackoffDelayStrategy
```

Both classes must implement `RetryPredicate` and `RetryDelayStrategy` respectively and have a no-arg constructor.

## Sample Domain Architecture (without `@Step`)

The sample module demonstrates `endpoint -> flow -> assertions` layering with framework executor-based steps:

- Endpoint: `PostmanEchoApi` (transport only, no manual Allure calls),
- Flow: `EchoFlow` (business orchestration),
- Assertions: `EchoAssertions` (domain checks),
- Executor: `AllureActionExecutor` (`action/assertion/composite`) for reusable step creation.

```java
QueryRoundtripResult result = echoFlow.verifyQueryRoundtrip("suite", "smoke");
EchoAssertions.assertQueryRoundtrip(result);
```

Result in Allure:

- flow-level step,
- action/assertion sub-steps,
- HTTP step per `httpClient.execute(...)` call with auto attachments.

