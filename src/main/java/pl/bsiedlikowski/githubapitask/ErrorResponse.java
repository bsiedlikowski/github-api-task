package pl.bsiedlikowski.githubapitask;

record ErrorResponse(
        int status,
        String message
) {}