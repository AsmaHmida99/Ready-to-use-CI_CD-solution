package com.example.demo.service;

import com.example.demo.model.DeploymentFile;
import com.example.demo.model.DeploymentRequest;
import com.example.demo.repository.DeploymentFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeploymentFileServiceTest {
    @Mock
    private DeploymentFileRepository deploymentFileRepository;

    @InjectMocks
    private DeploymentFileService deploymentFileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDeploymentFile() {
        DeploymentRequest request = new DeploymentRequest();
        DeploymentFile file = new DeploymentFile();
        when(deploymentFileRepository.save(any(DeploymentFile.class))).thenReturn(file);
        DeploymentFile result = deploymentFileService.saveDeploymentFile(request, "Dockerfile", "DOCKERFILE", "content");
        assertNotNull(result);
        verify(deploymentFileRepository, times(1)).save(any(DeploymentFile.class));
    }

    @Test
    void testGetFilesByDeploymentRequest() {
        DeploymentRequest request = new DeploymentRequest();
        when(deploymentFileRepository.findByDeploymentRequest(request)).thenReturn(Collections.emptyList());
        List<DeploymentFile> result = deploymentFileService.getFilesByDeploymentRequest(request);
        assertNotNull(result);
    }

    @Test
    void testGetFilesByDeploymentRequestId() {
        when(deploymentFileRepository.findByDeploymentRequestDeploymentRequestId(1L)).thenReturn(Collections.emptyList());
        List<DeploymentFile> result = deploymentFileService.getFilesByDeploymentRequestId(1L);
        assertNotNull(result);
    }
}
