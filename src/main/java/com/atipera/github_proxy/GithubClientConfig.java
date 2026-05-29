package com.atipera.github_proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
class GithubClientConfig {

    private static final Logger log = LoggerFactory.getLogger(GithubClientConfig.class);

    @Bean
    GithubClient githubClient(
            @Value("${github.api.base-url}") String baseUrl) {
        log.info("Configuring GitHub client with base URL: {}", baseUrl);
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(
                        RestClient.builder()
                                .baseUrl(baseUrl)
                                .build()))
                .build()
                .createClient(GithubClient.class);
    }
}