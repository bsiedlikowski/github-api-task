package pl.bsiedlikowski.githubapitask;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@org.springframework.stereotype.Service
class GithubService {

    private final GithubClient client;
    GithubService(GithubClient client) {
        this.client = client;
    }

    public List<RepositoryResponse> getNonForkRepositoriesWithBranches(String username) {
        List<Repository> repositories = client.getUserRepositories(username);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

            List<Callable<RepositoryResponse>> tasks = repositories.stream()
                    .filter(r -> !r.fork())
                    .map(r -> (Callable<RepositoryResponse>) () -> {
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
                    .toList();

            try {
                return executor.invokeAll(tasks).stream()
                        .map(f -> {
                            try {
                                return f.get();
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to fetch repository data",e);
                            }
                        })
                        .toList();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while fetching repositories", e);
            }
        }
    }
}