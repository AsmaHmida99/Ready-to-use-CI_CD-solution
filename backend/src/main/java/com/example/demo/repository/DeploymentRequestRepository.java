package com.example.demo.repository;

import com.example.demo.model.DeploymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRequestRepository extends JpaRepository<DeploymentRequest, Long> {
    List<DeploymentRequest> findByUserUserId(Long userId);
    List<DeploymentRequest> findByRepoRepoId(Long repoId);
    List<DeploymentRequest> findByStatus(String status);
}
