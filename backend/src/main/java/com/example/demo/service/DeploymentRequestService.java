package com.example.demo.service;

import com.example.demo.model.DeploymentRequest;
import com.example.demo.model.Repo;
import com.example.demo.model.User;
import com.example.demo.repository.DeploymentRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeploymentRequestService {
    @Autowired
    private DeploymentRequestRepository deploymentRequestRepository;

    public DeploymentRequest createDeploymentRequest(User user, Repo repo, String status) {
        DeploymentRequest request = new DeploymentRequest();
        request.setUser(user);
        request.setRepo(repo);
        request.setStatus(status);
        request.setRequestedAt(LocalDateTime.now());
        return deploymentRequestRepository.save(request);
    }

    public Optional<DeploymentRequest> getById(Long id) {
        return deploymentRequestRepository.findById(id);
    }

    public List<DeploymentRequest> getByUserId(Long userId) {
        return deploymentRequestRepository.findByUserUserId(userId);
    }

    public List<DeploymentRequest> getByRepoId(Long repoId) {
        return deploymentRequestRepository.findByRepoRepoId(repoId);
    }

    public List<DeploymentRequest> getByStatus(String status) {
        return deploymentRequestRepository.findByStatus(status);
    }

    public DeploymentRequest updateStatus(Long id, String status) {
        Optional<DeploymentRequest> optional = deploymentRequestRepository.findById(id);
        if (optional.isPresent()) {
            DeploymentRequest request = optional.get();
            request.setStatus(status);
            if (status.equalsIgnoreCase("SUCCESS") || status.equalsIgnoreCase("FAILED")) {
                request.setCompletedAt(LocalDateTime.now());
            }
            return deploymentRequestRepository.save(request);
        }
        return null;
    }
}
