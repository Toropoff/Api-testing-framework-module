rootProject.name = "rest-api-test-framework"

include(
    "framework-core",
    "framework-test-support",
    "framework-db-oracle",
    "framework-messaging-rabbitmq",
    "framework-splunk",
    "framework-reporting",
    ":domains:postman-echo",
    ":domains:open-holidays",
    ":test-suites:smoke",
    ":test-suites:regression",
    ":test-suites:integration",
    ":test-suites:public-api"
)

project(":domains:postman-echo").projectDir = file("domains/postman-echo")
project(":domains:open-holidays").projectDir = file("domains/open-holidays")
project(":test-suites:smoke").projectDir = file("test-suites/tests-smoke")
project(":test-suites:regression").projectDir = file("test-suites/tests-regression")
project(":test-suites:integration").projectDir = file("test-suites/tests-integration")
project(":test-suites:public-api").projectDir = file("test-suites/tests-public-api")
