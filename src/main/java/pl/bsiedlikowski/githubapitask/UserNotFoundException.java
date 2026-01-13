package pl.bsiedlikowski.githubapitask;

class UserNotFoundException extends RuntimeException {
    UserNotFoundException(String username) {
        super("User not found: " + username);
    }
}