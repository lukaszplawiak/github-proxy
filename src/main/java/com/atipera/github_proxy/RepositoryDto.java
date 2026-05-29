package com.atipera.github_proxy;

import java.util.List;

public record RepositoryDto(
        String repositoryName,
        String ownerLogin,
        List<BranchDto> branches
) {}