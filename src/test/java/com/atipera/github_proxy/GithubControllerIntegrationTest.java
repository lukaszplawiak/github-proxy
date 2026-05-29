package com.atipera.github_proxy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableWireMock(
        @ConfigureWireMock(
                baseUrlProperties = "github.api.base-url"
        )
)
@AutoConfigureRestTestClient
class GithubControllerIntegrationTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    void shouldReturnRepositoriesForExistingUser() {
        stubFor(get(urlEqualTo("/users/testuser/repos?type=sources"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "repo1",
                                        "owner": { "login": "testuser" },
                                        "fork": false
                                    }
                                ]
                                """)));

        stubFor(get(urlEqualTo("/repos/testuser/repo1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "main",
                                        "commit": { "sha": "abc123" }
                                    }
                                ]
                                """)));

        List<RepositoryDto> response = restTestClient.get()
                .uri("/repositories/testuser")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<RepositoryDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().repositoryName()).isEqualTo("repo1");
        assertThat(response.getFirst().ownerLogin()).isEqualTo("testuser");
        assertThat(response.getFirst().branches()).hasSize(1);
        assertThat(response.getFirst().branches().getFirst().name()).isEqualTo("main");
        assertThat(response.getFirst().branches().getFirst().lastCommitSha()).isEqualTo("abc123");
    }

    @Test
    void shouldReturn404ForNonExistingUser() {
        stubFor(get(urlEqualTo("/users/nonexistent/repos?type=sources"))
                .willReturn(aResponse()
                        .withStatus(404)));

        ErrorResponse response = restTestClient.get()
                .uri("/repositories/nonexistent")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.status()).isEqualTo(404);
        assertThat(response.message()).isEqualTo("User 'nonexistent' not found");
    }

    @Test
    void shouldReturnEmptyListWhenAllRepositoriesAreForks() {
        stubFor(get(urlEqualTo("/users/testuser/repos?type=sources"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        List<RepositoryDto> response = restTestClient.get()
                .uri("/repositories/testuser")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<RepositoryDto>>() {})
                .returnResult()
                .getResponseBody();

        assertThat(response).isEmpty();
    }
}