package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class CdWorkflowGenerationService {
    /**
     * Generates a GitHub Actions workflow YAML for deploying to a single VM using docker-compose.
     * @param vmHost The VM host/IP (use secret placeholder if needed)
     * @param vmUser The VM username (use secret placeholder if needed)
     * @return The workflow YAML as a String
     */
    public String generateCdWorkflow(String vmHost, String vmUser) {
        return """
name: CD Deploy to VM

on:
  workflow_dispatch:

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Copy docker-compose.yml to VM
        uses: appleboy/scp-action@v0.1.7
        with:
          host: ${{ secrets.VM_HOST }}
          username: ${{ secrets.VM_USER }}
          key: ${{ secrets.VM_SSH_KEY }}
          source: docker-compose.yml
          target: /home/${{ secrets.VM_USER }}/app

      - name: Deploy on VM via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.VM_HOST }}
          username: ${{ secrets.VM_USER }}
          key: ${{ secrets.VM_SSH_KEY }}
          script: |
            cd /home/${{ secrets.VM_USER }}/app
            docker-compose pull
            docker-compose up -d --remove-orphans
""";
    }
}
