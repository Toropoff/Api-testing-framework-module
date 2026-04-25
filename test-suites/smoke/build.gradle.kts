tasks.test {
    useTestNG {
        suites("src/test/resources/testng-smoke.xml")
    }
}
plugins {
    id("java")
}