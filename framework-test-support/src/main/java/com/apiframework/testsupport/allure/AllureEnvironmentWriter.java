package com.apiframework.testsupport.allure;

import com.apiframework.config.ConfigResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class AllureEnvironmentWriter implements ISuiteListener {

    private static final Logger log = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    @Override
    public void onFinish(ISuite suite) {
        try {
            String env = ConfigResolver.resolveFromSystem().env();
            Properties props = new Properties();
            props.setProperty("Environment", env);
            Files.createDirectories(Paths.get("allure-results"));
            try (FileWriter writer = new FileWriter("allure-results/environment.properties")) {
                props.store(writer, null);
            }
        } catch (Exception e) {
            log.error("Failed to write allure-results/environment.properties: {}", e.getMessage());
        }
    }
}
