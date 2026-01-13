package pl.bsiedlikowski.githubapitask;

import java.util.List;

record RepositoryResponse(
        String repositoryName,
        String ownerLogin,
        List<BranchInfo> branches
) {
    record BranchInfo(
            String name,
            String lastCommitSha
    ) {}
}