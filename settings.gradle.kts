rootProject.name = "rest-api-test-framework"

include(
    "framework-core",
    "framework-api-model",
    "framework-testng",
    "framework-db-oracle",
    "framework-messaging-rabbitmq",
    "framework-contracts",
    "framework-reporting",
    "tests-smoke",
    "tests-regression",
    "tests-integration"
)
