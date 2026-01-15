package pl.bsiedlikowski.githubapitask;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.apache.commons.lang3.time.StopWatch;
import static org.assertj.core.api.Assertions.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GithubControllerIntegrationTest {

	@Autowired
	private WebTestClient webTestClient;

	@RegisterExtension
	static WireMockExtension wireMock = WireMockExtension.newInstance()
			.options(options().dynamicPort())
			.build();

	@DynamicPropertySource
	static void configureGithubBaseUrl(DynamicPropertyRegistry registry) {
		registry.add("github.api.base-url", () -> wireMock.baseUrl());
	}

	@Test
	void shouldReturnOnlyNonForkedRepositoriesWithBranches() throws Exception {
		String username = "testuser";

		wireMock.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                  {
                                    "name": "forked-repo",
                                    "owner": {"login": "%s"},
                                    "fork": true
                                  },
                                  {
                                    "name": "non-forked-repo",
                                    "owner": {"login": "%s"},
                                    "fork": false
                                  }
                                ]
                                """.formatted(username, username))));

		wireMock.stubFor(get(urlEqualTo("/repos/" + username + "/non-forked-repo/branches"))
				.willReturn(aResponse()
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                  {"name": "main", "commit": {"sha": "abcdef1234567890abcdef1234567890"}},
                                  {"name": "feature-x", "commit": {"sha": "fedcba9876543210fedcba9876543210"}}
                                ]
                                """)));

		String responseBody = webTestClient.get()
				.uri("/users/{username}/repositories", username)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		JSONAssert.assertEquals("""
                [
                  {
                    "repositoryName": "non-forked-repo",
                    "ownerLogin": "%s",
                    "branches": [
                      {"name": "main",      "lastCommitSha": "abcdef1234567890abcdef1234567890"},
                      {"name": "feature-x", "lastCommitSha": "fedcba9876543210fedcba9876543210"}
                    ]
                  }
                ]
                """.formatted(username), responseBody, true);
	}

	@Test
	void shouldReturn404WhenUserDoesNotExist() {
		String username = "non-existing-user";

		wireMock.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
				.willReturn(aResponse().withStatus(404)));

		webTestClient.get()
				.uri("/users/{username}/repositories", username)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.json("""
                        {
                          "status": 404,
                          "message": "User not found: %s"
                        }
                        """.formatted(username));
	}

	@Test
	void shouldFetchMultipleNonForkedRepositoriesBranchesInParallel() throws Exception {
		String username = "testuser";

		wireMock.stubFor(get(urlEqualTo("/users/" + username + "/repos"))
				.willReturn(aResponse()
						.withFixedDelay(1000)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                  {
                                    "name": "forked-repo",
                                    "owner": {"login": "%s"},
                                    "fork": true
                                  },
                                  {
                                    "name": "non-forked-repo-1",
                                    "owner": {"login": "%s"},
                                    "fork": false
                                  },
                                  {
                                    "name": "non-forked-repo-2",
                                    "owner": {"login": "%s"},
                                    "fork": false
                                  }
                                ]
                                """.formatted(username, username, username))));

		wireMock.stubFor(get(urlEqualTo("/repos/" + username + "/non-forked-repo-1/branches"))
				.willReturn(aResponse()
						.withFixedDelay(1000)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                  {"name": "main", "commit": {"sha": "abcdef1234567890abcdef1234567890"}},
                                  {"name": "feature-x", "commit": {"sha": "fedcba9876543210fedcba9876543210"}}
                                ]
                                """)));
		wireMock.stubFor(get(urlEqualTo("/repos/" + username + "/non-forked-repo-2/branches"))
				.willReturn(aResponse()
						.withFixedDelay(1000)
						.withHeader("Content-Type", "application/json")
						.withBody("""
                                [
                                  {"name": "develop", "commit": {"sha": "xyzxyz0101010101xyzxyz0101010101"}},
                                  {"name": "feature-y", "commit": {"sha": "oprstu5678998765oprstu5678998765"}}
                                ]
                                """)));

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		String responseBody = webTestClient.get()
				.uri("/users/{username}/repositories", username)
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class)
				.returnResult()
				.getResponseBody();

		stopWatch.stop();
		long executionTime = stopWatch.getTime();
		System.out.println("Czas wykonania: " + executionTime + " ms");
		JSONAssert.assertEquals("""
                [
                  {
                    "repositoryName": "non-forked-repo-1",
                    "ownerLogin": "%s",
                    "branches": [
                      {"name": "main", "lastCommitSha": "abcdef1234567890abcdef1234567890"},
                      {"name": "feature-x", "lastCommitSha": "fedcba9876543210fedcba9876543210"}
                    ]
                  },
                  {
                    "repositoryName": "non-forked-repo-2",
                    "ownerLogin": "%s",
                    "branches": [
                      {"name": "develop", "lastCommitSha": "xyzxyz0101010101xyzxyz0101010101"},
                      {"name": "feature-y", "lastCommitSha": "oprstu5678998765oprstu5678998765"}
                    ]
                  }
                ]
                """.formatted(username, username), responseBody, true);

		wireMock.verify(3, getRequestedFor(urlMatching(".*")));

		assertThat(executionTime).isBetween(2000L, 2500L);
	}


}