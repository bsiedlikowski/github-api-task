package pl.bsiedlikowski.githubapitask.service;

import org.springframework.stereotype.Service;
import pl.bsiedlikowski.githubapitask.client.GitHubClient;
import pl.bsiedlikowski.githubapitask.client.RepositoryResponse;
import pl.bsiedlikowski.githubapitask.client.BranchResponse;
import pl.bsiedlikowski.githubapitask.dto.RepositoryDto;
import pl.bsiedlikowski.githubapitask.dto.BranchDto;

import java.util.List;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;

    public GitHubService(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    public List<RepositoryDto> getUserRepositories(String username) {
        List<RepositoryResponse> repositories = gitHubClient.fetchRepositories(username);

        return repositories.stream()
                .filter(repo -> !repo.isFork())
                .map(repo -> {
                    List<BranchResponse> branches = gitHubClient.fetchBranches(username, repo.getName());

                    List<BranchDto> branchDtos = branches.stream()
                            .map(branch -> new BranchDto(branch.getName(), branch.getCommit().getSha()))
                            .toList();

                    return new RepositoryDto(repo.getName(), repo.getOwner().getLogin(), branchDtos);
                })
                .toList();
    }
}
