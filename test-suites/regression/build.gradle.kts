tasks.test {
    useTestNG {
        suites("src/test/resources/testng-regression.xml")
    }
}

plugins {
id("java")
}