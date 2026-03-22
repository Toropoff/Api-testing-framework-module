package com.apiframework.core.config;

import org.aeonbits.owner.Config;

@Config.Sources({
    "classpath:application-${framework.profile}.properties",
    "classpath:application.properties"
})
public interface FrameworkProperties extends Config {

    @Key("http.connectTimeoutMs")
    @DefaultValue("5000")
    int connectTimeoutMs();

    @Key("http.readTimeoutMs")
    @DefaultValue("15000")
    int readTimeoutMs();

    @Key("http.retry.maxAttempts")
    @DefaultValue("1")
    int retryMaxAttempts();

    @Key("http.retry.delayMs")
    @DefaultValue("0")
    long retryDelayMs();

    @Key("auth.basic.usernameSecretKey")
    @DefaultValue("auth.basic.username")
    String basicUsernameSecretKey();

    @Key("auth.basic.passwordSecretKey")
    @DefaultValue("auth.basic.password")
    String basicPasswordSecretKey();
}
