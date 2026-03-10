dependencies {
    testImplementation(project(":framework-suite-support"))
    testImplementation(project(":framework-db-oracle"))
    testImplementation(project(":framework-messaging-rabbitmq"))
}

tasks.test {
    useTestNG {
        suites("src/test/resources/testng-integration.xml")
    }
}
