package pl.bsiedlikowski.githubapitask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
class GithubClient {

    private final RestClient client;

    GithubClient(RestClient.Builder builder,
                 @Value("${github.api.base-url:https://api.github.com}") String baseUrl) {
        this.client = builder
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("User-Agent", "GithubApiRecruitmentTask/1.0")
                .build();
    }

    public List<Repository> getUserRepositories(String username) {
        Repository[] array = client.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(s -> s.value() == 404, (req, res) -> {
                    throw new UserNotFoundException(username);
                })
                .body(Repository[].class);

        return array != null ? Arrays.asList(array) : List.of();
    }

    List<Branch> getRepositoryBranches(String owner, String repo) {
        Branch[] branches = client.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repo)
                .retrieve()
                .body(Branch[].class);

        return branches != null ? Arrays.asList(branches) : List.of();
    }
}