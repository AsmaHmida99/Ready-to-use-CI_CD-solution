package com.example.demo.service;

import com.example.demo.model.Repo;
import com.example.demo.model.DockerComposeHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.*;

@Service
public class DeploymentUrlService {
    @Autowired private DockerComposeHistoryService dockerComposeHistoryService;

    /**
     * Returns a map of service name to live URL(s) for the latest compose file of the repo.
     * @param repo the repo
     * @param vmHost the VM host (IP or domain)
     * @return map of service name to list of URLs
     */
    public Map<String, List<String>> getLiveUrls(Repo repo, String vmHost) {
        // Get the latest compose file for the repo
        List<DockerComposeHistory> histories = dockerComposeHistoryService.repo.findByRepoOrderByCreatedAtDesc(repo);
        if (histories.isEmpty()) return Collections.emptyMap();
        String composeContent = histories.get(0).getContent();
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(composeContent);
        Map<String, Object> services = (Map<String, Object>) obj.get("services");
        Map<String, List<String>> urls = new HashMap<>();
        for (Map.Entry<String, Object> entry : services.entrySet()) {
            String serviceName = entry.getKey();
            Map<String, Object> service = (Map<String, Object>) entry.getValue();
            List<String> ports = (List<String>) service.get("ports");
            List<String> serviceUrls = new ArrayList<>();
            if (ports != null) {
                for (String portMapping : ports) {
                    String externalPort = portMapping.split(":")[0].replaceAll("'", "");
                    String url = "http://" + vmHost + (externalPort.equals("80") ? "" : ":" + externalPort);
                    serviceUrls.add(url);
                }
            }
            if (!serviceUrls.isEmpty()) {
                urls.put(serviceName, serviceUrls);
            }
        }
        return urls;
    }
}
