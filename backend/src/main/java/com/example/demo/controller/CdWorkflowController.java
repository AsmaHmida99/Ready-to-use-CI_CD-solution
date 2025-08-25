package com.example.demo.controller;

import com.example.demo.model.Repo;
import com.example.demo.repository.RepoRepository;
import com.example.demo.service.CdWorkflowGenerationService;
import com.example.demo.service.GitHubService;
import com.example.demo.service.DeploymentUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/cd-workflow")
@CrossOrigin(origins = "*")
public class CdWorkflowController {
    @Autowired private CdWorkflowGenerationService cdWorkflowGenerationService;
    @Autowired private GitHubService gitHubService;
    @Autowired private RepoRepository repoRepository;
    @Autowired private DeploymentUrlService deploymentUrlService;
    // GET LIVE URLS: Returns the live URLs for deployed services
    @GetMapping("/live-urls")
    public ResponseEntity<?> getLiveUrls(@RequestParam Long repoId, @RequestParam String vmHost, Authentication authentication) {
        try {
            Repo repo = requireAuthAndRepo(authentication, repoId);
            if (repo == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, "message", "Authentication required or repo not found / not owned"
            ));
            var urls = deploymentUrlService.getLiveUrls(repo, vmHost);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "urls", urls
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // PREVIEW: Generate the workflow YAML but do not push
    @PostMapping("/preview")
    public ResponseEntity<?> previewCdWorkflow(@RequestParam Long repoId, Authentication authentication) {
        try {
            Repo repo = requireAuthAndRepo(authentication, repoId);
            if (repo == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, "message", "Authentication required or repo not found / not owned"
            ));
            String workflowYaml = cdWorkflowGenerationService.generateCdWorkflow("${{ secrets.VM_HOST }}", "${{ secrets.VM_USER }}");
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "workflowYaml", workflowYaml
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // APPLY: Generate and push the workflow YAML to the user's repo
    @PostMapping("/apply")
    public ResponseEntity<?> applyCdWorkflow(@RequestParam Long repoId, Authentication authentication) {
        try {
            Repo repo = requireAuthAndRepo(authentication, repoId);
            if (repo == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false, "message", "Authentication required or repo not found / not owned"
            ));
            String token = repo.getUser().getToken();
            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "GitHub token not found for user"));
            }
            gitHubService.setCurrentToken(token);
            String workflowYaml = cdWorkflowGenerationService.generateCdWorkflow("${{ secrets.VM_HOST }}", "${{ secrets.VM_USER }}");
            GitHubService.PushResult pr = gitHubService.pushWorkflowToGitHub(
                    token,
                    repo.getFullName(),
                    repo.getDefaultBranch(),
                    ".github/workflows/cd-deploy.yml",
                    workflowYaml,
                    GitHubService.FileHandlingStrategy.OVERWRITE
            );
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "CD workflow generated & pushed",
                    "commitHash", pr != null ? pr.getCommitHash() : null
            ));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", "IO Error: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Helper: Auth + repo lookup 
    private Repo requireAuthAndRepo(Authentication authentication, Long repoId) {
        if (authentication == null || repoId == null) return null;
        String username = authentication.getName();
        return repoRepository.findByIdAndUserUsername(repoId, username).orElse(null);
    }
}
