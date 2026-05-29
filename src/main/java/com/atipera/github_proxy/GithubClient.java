package com.atipera.github_proxy;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange
interface GithubClient {

    @GetExchange("/users/{username}/repos?type=sources")
    List<GithubRepository> getRepositories(@PathVariable String username);

    @GetExchange("/repos/{owner}/{repo}/branches")
    List<GithubBranch> getBranches(
            @PathVariable String owner,
            @PathVariable String repo
    );
}