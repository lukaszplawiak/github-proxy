package com.atipera.github_proxy;

record GithubRepository(
        String name,
        GithubOwner owner,
        boolean fork
) {}