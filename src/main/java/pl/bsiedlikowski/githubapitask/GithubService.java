package pl.bsiedlikowski.githubapitask;

import java.util.List;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Service
class GithubService {

    private final GithubClient client;

    GithubService(GithubClient client) {
        this.client = client;
    }

    List<RepositoryResponse> getNonForkRepositoriesWithBranches(String username) {
        List<Repository> repositories = client.getUserRepositories(username);
        return repositories.stream()
                .filter(r -> !r.fork())
                .map(r -> {
                    List<Branch> branches = client.getRepositoryBranches(
                            r.owner().login(),
                            r.name()
                    );

                    List<RepositoryResponse.BranchInfo> branchInfos = branches.stream()
                            .map(b -> new RepositoryResponse.BranchInfo(
                                    b.name(),
                                    b.commit().sha()
                            ))
                            .toList();

                    return new RepositoryResponse(
                            r.name(),
                            r.owner().login(),
                            branchInfos
                    );
                })
                .collect(toList());
    }
}