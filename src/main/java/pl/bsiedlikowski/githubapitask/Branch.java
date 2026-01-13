package pl.bsiedlikowski.githubapitask;

record Branch(
        String name,
        Commit commit
) {
    record Commit(String sha) {}
}