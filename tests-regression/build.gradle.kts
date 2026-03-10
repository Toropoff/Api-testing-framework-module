dependencies {
    testImplementation(project(":framework-core"))
    testImplementation(project(":framework-api-model"))
    testImplementation(project(":framework-testng"))
    testImplementation(project(":framework-contracts"))
    testImplementation(project(":framework-db-oracle"))
    testImplementation(project(":framework-messaging-rabbitmq"))
    testImplementation(project(":framework-reporting"))
    testImplementation("org.testng:testng:7.12.0")
}

tasks.test {
    useTestNG {
        suites("src/test/resources/testng-regression.xml")
    }
}
