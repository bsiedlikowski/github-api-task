# GitHub Api Task

Spring Boot application that fetches a GitHub user's repositories (excluding forks) along with their branches and the SHA of the last commit on each branch.

## Technologies
- Java 25 (must be set as JAVA_HOME)
- Spring Boot 4.0.1
- Gradle-Kotlin

## How to run

Requirements:
- Java 25
- Gradle-Kotlin

Running the application locally:
```bash
./gradlew bootRun
```
The application starts by default on:
http://localhost:8080

## Endpoint
Fetch non-fork repositories with branches:
```bash
GET /users/{username}/repositories
```
Replace {username} with any GitHub username.

**Example:** http://localhost:8080/users/octocat/repositories

Aplikacja zwróci listę repozytoriów, które nie są forkami, wraz z ich branchami i ostatnim commit SHA.


**Successful response example:**
```json
[
  {
    "repositoryName": "non-fork-repo",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "abcdef1234567890abcdef1234567890"
      },
      {
        "name": "feature-x",
        "lastCommitSha": "fedcba9876543210fedcba9876543210"
      }
    ]
  }
]
```
**Error response (when user does not exist):**

```json
{
  "status": 404,
  "message": "User non-existing-user not found"
}
```
## Tests
```bash
./gradlew test
```
