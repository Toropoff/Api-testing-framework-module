dependencies {
    api(project(":framework-core"))
    implementation(project(":framework-test-support"))
    implementation("io.qameta.allure:allure-testng:2.33.0")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    testImplementation("org.testng:testng:7.12.0")
}
