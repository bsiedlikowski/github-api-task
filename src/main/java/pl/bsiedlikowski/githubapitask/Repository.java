package pl.bsiedlikowski.githubapitask;

record Repository(
        String name,
        Owner owner,
        boolean fork
) {
    record Owner(String login) {}
}
