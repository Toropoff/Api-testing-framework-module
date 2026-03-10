package com.apiframework.apimodel.dto.auth;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
