package com.server.ShareDoo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class SentenceEmbeddingService {
    private final RestTemplate restTemplate;
    
    private static final String PYTHON_API_URL = "http://localhost:5000";

    public SentenceEmbeddingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double calculateSimilarity(String text1, String text2) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text1", text1);
            requestBody.put("text2", text2);

            Map<String, Object> response = restTemplate.postForObject(
                PYTHON_API_URL + "/similarity",
                requestBody,
                Map.class
            );

            if (response != null && response.containsKey("similarity")) {
                return ((Number) response.get("similarity")).doubleValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double[] getEmbedding(String text) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);

            Map<String, Object> response = restTemplate.postForObject(
                PYTHON_API_URL + "/embedding",
                requestBody,
                Map.class
            );

            if (response != null && response.containsKey("embedding")) {
                List<Number> embeddingList = (List<Number>) response.get("embedding");
                return embeddingList.stream()
                    .mapToDouble(Number::doubleValue)
                    .toArray();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[0];
    }
} 