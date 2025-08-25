package com.example.demo.controller;

import com.example.demo.model.DeploymentRequest;
import com.example.demo.model.Repo;
import com.example.demo.model.User;
import com.example.demo.model.DeploymentFile;
import com.example.demo.repository.RepoRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.DeploymentRequestService;
import com.example.demo.service.DeploymentFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/test-deployment")
public class TempDeploymentTestController {
    @Autowired
    private DeploymentRequestService deploymentRequestService;
    @Autowired
    private DeploymentFileService deploymentFileService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RepoRepository repoRepository;

    // TEMP endpoint to create a deployment request and file for demo/testing
    @PostMapping("/create")
    public DeploymentRequest createDeploymentRequest(@RequestParam Long userId, @RequestParam Long repoId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Repo> repoOpt = repoRepository.findById(repoId);
        if (userOpt.isEmpty() || repoOpt.isEmpty()) {
            throw new RuntimeException("User or Repo not found");
        }
        DeploymentRequest request = deploymentRequestService.createDeploymentRequest(userOpt.get(), repoOpt.get(), "PENDING");
        // Optionally create a dummy deployment file
        deploymentFileService.saveDeploymentFile(request, "Dockerfile", "DOCKERFILE", "FROM openjdk:17\nCOPY . /app\nWORKDIR /app\nCMD java -jar app.jar");
        return request;
    }

    // TEMP endpoint to get all deployment files for a request
    @GetMapping("/files/{deploymentRequestId}")
    public List<DeploymentFile> getFiles(@PathVariable Long deploymentRequestId) {
        return deploymentFileService.getFilesByDeploymentRequestId(deploymentRequestId);
    }
}
