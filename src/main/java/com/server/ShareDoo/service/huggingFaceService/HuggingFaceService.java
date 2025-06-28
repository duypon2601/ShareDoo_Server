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

    @Value("${ai.recommendation.threshold:0.3}")
    private double threshold;

    public HuggingFaceResponse classifyEvent(String text, List<String> labels) {
        if (apiKey == null || apiKey.equals("YOUR_HUGGINGFACE_API_KEY_HERE")) {
            log.error("Hugging Face API key not configured properly");
            return createFallbackResponse(labels);
        }

        if (text == null || text.trim().isEmpty()) {
            log.warn("Empty text provided for classification");
            return createFallbackResponse(labels);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", text);
        requestBody.put("parameters", Map.of(
                "candidate_labels", labels,
                "multi_label", true
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        try {
            log.info("Calling Hugging Face API for text: {}", text.substring(0, Math.min(100, text.length())));
            
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("Hugging Face API returned status: {}", response.getStatusCode());
                return createFallbackResponse(labels);
            }

            ObjectMapper mapper = new ObjectMapper();
            HuggingFaceResponse result = mapper.readValue(response.getBody(), HuggingFaceResponse.class);
            
            log.info("AI Classification result - Labels: {}, Scores: {}", result.getLabels(), result.getScores());
            return result;

        } catch (RestClientException e) {
            log.error("Hugging Face API call failed with RestClientException", e);
            return createFallbackResponse(labels);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Hugging Face API response", e);
            return createFallbackResponse(labels);
        } catch (Exception e) {
            log.error("Unexpected error in Hugging Face API call", e);
            return createFallbackResponse(labels);
        }
    }

    private HuggingFaceResponse createFallbackResponse(List<String> labels) {
        log.info("Creating fallback response for labels: {}", labels);
        // Trả về response mặc định với score thấp cho tất cả categories
        List<Double> fallbackScores = labels.stream()
                .map(label -> 0.1) // Score thấp để ưu tiên logic fallback khác
                .toList();
        
        return new HuggingFaceResponse(labels, fallbackScores);
    }
}
