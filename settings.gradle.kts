rootProject.name = "rest-api-test-framework"

include(
    "framework-core",
    "framework-test-support",
    "framework-db-oracle",
    "framework-messaging-rabbitmq",
    "framework-splunk",
    "framework-reporting",
    ":domains",
    ":test-suites:smoke",
    ":test-suites:regression",
    ":test-suites:integration",
    ":test-suites:public-api"
)
