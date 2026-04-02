dependencies {
    api(project(":framework-core"))
    api("org.testng:testng:7.12.0")
    api("org.assertj:assertj-core:3.27.6")
    implementation("net.javacrumbs.json-unit:json-unit-assertj:5.1.0")
    implementation("com.networknt:json-schema-validator:1.5.9")
    api("io.qameta.allure:allure-java-commons:2.33.0")
    implementation("io.qameta.allure:allure-assertj:2.33.0")
}
