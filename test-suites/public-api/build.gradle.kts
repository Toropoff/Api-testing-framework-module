tasks.test {
    useTestNG {
        suites("src/test/resources/testng-public-api.xml")
    }
}
plugins {
    id("java")
}