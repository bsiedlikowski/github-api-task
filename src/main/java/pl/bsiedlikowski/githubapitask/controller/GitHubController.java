package pl.bsiedlikowski.githubapitask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.bsiedlikowski.githubapitask.dto.RepositoryDto;
import pl.bsiedlikowski.githubapitask.exception.UserNotFoundException;
import pl.bsiedlikowski.githubapitask.service.GitHubService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/users/{username}/repos")
    public ResponseEntity<?> getUserRepositories(@PathVariable String username) {
        try {
            List<RepositoryDto> repositories = gitHubService.getUserRepositories(username);
            return ResponseEntity.ok(repositories);
        } catch (UserNotFoundException ex) {
            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("status", HttpStatus.NOT_FOUND.value());
            errorBody.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }
}
