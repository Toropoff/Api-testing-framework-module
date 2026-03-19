dependencies {
    testImplementation(project(":framework-test-support"))
    testImplementation(project(":framework-contracts"))
    testImplementation(project(":framework-reporting"))
    testImplementation(project(":domains:postman-echo"))
}

tasks.test {
    useTestNG {
        suites("src/test/resources/testng-integration.xml")
    }
}
