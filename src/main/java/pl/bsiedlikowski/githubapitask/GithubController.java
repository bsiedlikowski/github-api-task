package pl.bsiedlikowski.githubapitask;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{username}/repositories")
class GithubController {

    private final GithubService service;

    GithubController(GithubService service) {
        this.service = service;
    }

    @GetMapping
    List<RepositoryResponse> getNonForkRepositories(@PathVariable String username) {

        return service.getNonForkRepositoriesWithBranches(username);
    }
}