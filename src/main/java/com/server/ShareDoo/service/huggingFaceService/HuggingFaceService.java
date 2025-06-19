package com.server.ShareDoo.service.huggingFaceService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ShareDoo.dto.response.huggingFaceResponse.HuggingFaceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HuggingFaceService {
    private final RestTemplate restTemplate;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.key}")
    private String apiKey;

    public HuggingFaceResponse classifyEvent(String text, List<String> labels) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text);
        requestBody.put("parameters", Map.of(
                "candidate_labels", labels,
                "multi_label", true
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON); // ĐÃ SỬA
//        headers.setAccept(List.of(MediaType.APPLICATION_JSON)); // ĐÃ SỬA
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            // Gọi API và xử lý response dạng String
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            // Parse thủ công để tránh lỗi media type
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response.getBody(), HuggingFaceResponse.class);

        } catch (RestClientException | JsonProcessingException e) {
            log.error("Hugging Face API call failed", e);
            return null;
        }
    }
}
