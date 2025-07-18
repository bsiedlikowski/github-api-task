package pl.bsiedlikowski.githubapitask;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.bsiedlikowski.githubapitask.client.GitHubClient;
import pl.bsiedlikowski.githubapitask.dto.BranchDto;
import pl.bsiedlikowski.githubapitask.dto.RepositoryDto;
import pl.bsiedlikowski.githubapitask.client.BranchResponse;
import pl.bsiedlikowski.githubapitask.client.RepositoryResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GitHubClient gitHubClient;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void shouldFilterOutForkRepositoriesAndReturnBranches() throws Exception {
		// given
		RepositoryResponse.Owner owner = new RepositoryResponse.Owner("exampleuser");

		RepositoryResponse forkedRepo = new RepositoryResponse("forked-repo", owner, true);
		RepositoryResponse nonForkedRepo = new RepositoryResponse("main-repo", owner, false);

		BranchResponse branch1 = new BranchResponse("main", new BranchResponse.Commit("sha123"));
		BranchResponse branch2 = new BranchResponse("dev", new BranchResponse.Commit("sha456"));

		when(gitHubClient.fetchRepositories("exampleuser"))
				.thenReturn(List.of(forkedRepo, nonForkedRepo));

		when(gitHubClient.fetchBranches("exampleuser", "main-repo"))
				.thenReturn(List.of(branch1, branch2));

		// when
		String json = mockMvc.perform(get("/api/github/users/exampleuser/repos")
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		// then
		List<RepositoryDto> response = objectMapper.readValue(json, new TypeReference<>() {});

		assertThat(response).hasSize(1);
		RepositoryDto repo = response.get(0);
		assertThat(repo.getRepositoryName()).isEqualTo("main-repo");
		assertThat(repo.getOwnerLogin()).isEqualTo("exampleuser");

		assertThat(repo.getBranches()).hasSize(2);
		assertThat(repo.getBranches()).extracting("name").containsExactlyInAnyOrder("main", "dev");
		assertThat(repo.getBranches()).extracting("lastCommitSha").containsExactlyInAnyOrder("sha123", "sha456");
	}
}
