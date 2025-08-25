package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deployment_requests")
public class DeploymentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deployment_request_id")
    private Long deploymentRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repo_id", nullable = false)
    private Repo repo;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // e.g., PENDING, RUNNING, SUCCESS, FAILED

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Getters and setters
    public Long getDeploymentRequestId() { return deploymentRequestId; }
    public void setDeploymentRequestId(Long deploymentRequestId) { this.deploymentRequestId = deploymentRequestId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Repo getRepo() { return repo; }
    public void setRepo(Repo repo) { this.repo = repo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
