dependencies {
    testImplementation(project(":framework-test-support"))
    testImplementation(project(":framework-reporting"))
    testImplementation(project(":domains"))
}

tasks.test {
    useTestNG {
        suites("src/test/resources/testng-integration.xml")
    }
}
