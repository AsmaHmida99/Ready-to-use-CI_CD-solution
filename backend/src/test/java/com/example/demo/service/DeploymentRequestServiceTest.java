package com.example.demo.service;

import com.example.demo.model.DeploymentRequest;
import com.example.demo.model.Repo;
import com.example.demo.model.User;
import com.example.demo.repository.DeploymentRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeploymentRequestServiceTest {
    @Mock
    private DeploymentRequestRepository deploymentRequestRepository;

    @InjectMocks
    private DeploymentRequestService deploymentRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDeploymentRequest() {
        User user = new User();
        Repo repo = new Repo();
        DeploymentRequest request = new DeploymentRequest();
        when(deploymentRequestRepository.save(any(DeploymentRequest.class))).thenReturn(request);
        DeploymentRequest result = deploymentRequestService.createDeploymentRequest(user, repo, "PENDING");
        assertNotNull(result);
        verify(deploymentRequestRepository, times(1)).save(any(DeploymentRequest.class));
    }

    @Test
    void testGetById() {
        DeploymentRequest request = new DeploymentRequest();
        when(deploymentRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        Optional<DeploymentRequest> result = deploymentRequestService.getById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void testGetByUserId() {
        when(deploymentRequestRepository.findByUserUserId(1L)).thenReturn(Collections.emptyList());
        assertNotNull(deploymentRequestService.getByUserId(1L));
    }

    @Test
    void testUpdateStatus() {
        DeploymentRequest request = new DeploymentRequest();
        when(deploymentRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(deploymentRequestRepository.save(any(DeploymentRequest.class))).thenReturn(request);
        DeploymentRequest result = deploymentRequestService.updateStatus(1L, "SUCCESS");
        assertNotNull(result);
        verify(deploymentRequestRepository, times(1)).save(any(DeploymentRequest.class));
    }
}
