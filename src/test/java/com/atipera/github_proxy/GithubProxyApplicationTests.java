package com.atipera.github_proxy;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"github.api.base-url=https://api.github.com"
})
class GithubProxyApplicationTests {

	@Test
	void contextLoads() {
	}
}