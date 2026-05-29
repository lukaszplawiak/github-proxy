package com.atipera.github_proxy;

public record ErrorResponse(
        int status,
        String message
) {}