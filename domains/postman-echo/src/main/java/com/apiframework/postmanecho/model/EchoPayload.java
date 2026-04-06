package com.apiframework.postmanecho.model;

public record EchoPayload(
    String event,
    int amount,
    boolean active
) {
}
