package com.atipera.github_proxy;

public record BranchDto(
        String name,
        String lastCommitSha
) {}