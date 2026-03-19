package com.apiframework.domains.postmanecho.model;

public record EchoPayload(
    String event,
    int amount,
    boolean active
) {
}
