package com.apiframework.oracle.config;

import com.apiframework.config.EnvResolver;

public record OracleConnectionConfig(
    String jdbcUrl,
    String username,
    String password,
    int maximumPoolSize
) {

    public static OracleConnectionConfig fromSystem() {
        return new OracleConnectionConfig(
            EnvResolver.required("ORACLE_JDBC_URL"),
            EnvResolver.required("ORACLE_USERNAME"),
            EnvResolver.required("ORACLE_PASSWORD"),
            EnvResolver.integer("ORACLE_POOL_SIZE", 5)
        );
    }
}
