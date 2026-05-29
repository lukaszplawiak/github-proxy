package com.atipera.github_proxy;

class UserNotFoundException extends RuntimeException {

    UserNotFoundException(String username) {
        super("User '%s' not found".formatted(username));
    }
}