package com.theanh.iamservice.IAM_Service_2.Services.ServiceImp.StorageImp;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StorageManagementServiceImp {

    private final RestTemplate restTemplate;

    public String uploadFileToStorageService(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = authHeader.substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String storageServiceUrl = "http://localhost:8082/api/storage/greet";
        ResponseEntity<String> response = restTemplate.exchange(
                storageServiceUrl,
                HttpMethod.POST,
                entity,
                String.class);

        return response.getBody();
    }
}
