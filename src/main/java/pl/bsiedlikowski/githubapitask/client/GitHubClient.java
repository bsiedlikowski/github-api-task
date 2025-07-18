package pl.bsiedlikowski.githubapitask.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.bsiedlikowski.githubapitask.exception.UserNotFoundException;

import java.util.List;

@Component
public class GitHubClient {

    private final RestTemplate restTemplate;

    private static final String REPOS_URL = "https://api.github.com/users/{username}/repos";
    private static final String BRANCHES_URL = "https://api.github.com/repos/{owner}/{repo}/branches";

    public GitHubClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<RepositoryResponse> fetchRepositories(String username) {
        try {
            RepositoryResponse[] repositories = restTemplate.getForObject(REPOS_URL, RepositoryResponse[].class, username);

            if (repositories == null) {
                return List.of();
            }

            return List.of(repositories);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new UserNotFoundException("User " + username + " not found");
            }
            throw e;
        }
    }

    public List<BranchResponse> fetchBranches(String owner, String repo) {
        BranchResponse[] branches = restTemplate.getForObject(BRANCHES_URL, BranchResponse[].class, owner, repo);
        if (branches == null) {
            return List.of();
        }
        return List.of(branches);
    }
}
