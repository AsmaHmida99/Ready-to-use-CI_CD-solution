package com.example.demo.repository;

import com.example.demo.model.DeploymentFile;
import com.example.demo.model.DeploymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentFileRepository extends JpaRepository<DeploymentFile, Long> {
    List<DeploymentFile> findByDeploymentRequest(DeploymentRequest deploymentRequest);
    List<DeploymentFile> findByDeploymentRequestDeploymentRequestId(Long deploymentRequestId);
}
