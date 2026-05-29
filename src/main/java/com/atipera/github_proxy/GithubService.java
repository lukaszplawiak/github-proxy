package com.atipera.github_proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Service
class GithubService {

    private static final Logger log = LoggerFactory.getLogger(GithubService.class);

    private final GithubClient githubClient;

    GithubService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepositoryDto> getRepositories(String username) {
        log.info("Fetching repositories for user: {}", username);
        try {
            var repos = githubClient.getRepositories(username);
            log.info("Successfully fetched {} repositories for user: {}", repos.size(), username);
            return repos.stream()
                    .map(repository -> new RepositoryDto(
                            repository.name(),
                            repository.owner().login(),
                            getBranches(username, repository.name())
                    ))
                    .toList();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("GitHub user not found: {}", username);
            throw new UserNotFoundException(username);
        } catch (Exception e) {
            log.error("Unexpected error while fetching repositories for user: {}", username, e);
            throw e;
        }
    }

    private List<BranchDto> getBranches(String username, String repositoryName) {
        log.debug("Fetching branches for repository: {}/{}", username, repositoryName);
        var branches = githubClient.getBranches(username, repositoryName)
                .stream()
                .map(branch -> new BranchDto(
                        branch.name(),
                        branch.commit().sha()
                ))
                .toList();
        log.debug("Found {} branches for repository: {}/{}", branches.size(), username, repositoryName);
        return branches;
    }
}