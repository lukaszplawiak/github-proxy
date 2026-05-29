package com.atipera.github_proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/repositories")
public class GithubController {

    private static final Logger log = LoggerFactory.getLogger(GithubController.class);

    private final GithubService githubService;

    GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @GetMapping("/{username}")
    List<RepositoryDto> getRepositories(@PathVariable String username) {
        log.info("Received request to fetch repositories for user: {}", username);
        return githubService.getRepositories(username);
    }
}