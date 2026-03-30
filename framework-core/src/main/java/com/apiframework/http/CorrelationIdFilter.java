package com.apiframework.http;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class CorrelationIdFilter implements Filter {
    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    public static void set(String id) {
        CURRENT.set(id);
    }

    public static void clear() {
        CURRENT.remove();
    }

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        String id = CURRENT.get();
        if (id != null) {
            req.header("X-Correlation-Id", id);
        }
        return ctx.next(req, res);
    }
}
