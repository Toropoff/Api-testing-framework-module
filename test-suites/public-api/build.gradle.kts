dependencies {
    testImplementation(project(":framework-test-support"))
    testImplementation(project(":framework-reporting"))
    testImplementation(project(":domains:open-holidays"))
}

tasks.test {
    useTestNG {
        suites("src/test/resources/testng-public-api.xml")
    }
}
