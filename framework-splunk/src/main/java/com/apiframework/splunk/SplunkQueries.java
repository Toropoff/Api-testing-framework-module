package com.apiframework.splunk;

public final class SplunkQueries {

    private SplunkQueries() {}

    public static String forCorrelationId(String corrId, String index) {
        return SplunkQueryBuilder.search()
            .index(index)
            .where("correlationId", corrId)
            .pipe("spath")
            .pipe("table correlationId, message, tracePoint, applicationName, timestamp")
            .build();
    }

    public static String forCorrelationId(String corrId, String index, String tableFields) {
        return SplunkQueryBuilder.search()
            .index(index)
            .where("correlationId", corrId)
            .pipe("spath")
            .pipe("table " + tableFields)
            .build();
    }
}
