package com.server.ShareDoo.service.aiSuggestionService;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductSuggestionService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RestTemplate restTemplate;
    private static final String PYTHON_SERVICE_URL = "http://localhost:5000";

    public List<ResProductDTO> getSimilarProducts(String query, int limit) {
        // Lấy tất cả sản phẩm có sẵn
        List<Product> availableProducts = productRepository.findByAvailabilityStatus(ProductDTO.AvailabilityStatus.AVAILABLE);
        
        // Tạo danh sách các cặp (sản phẩm, độ tương đồng)
        List<Map.Entry<Product, Double>> productSimilarities = new ArrayList<>();
        
        // Tính độ tương đồng cho từng sản phẩm
        for (Product product : availableProducts) {
            String productText = product.getName() + " " + product.getDescription();
            double similarity = calculateSimilarity(query, productText);
            productSimilarities.add(new AbstractMap.SimpleEntry<>(product, similarity));
        }
        
        // Sắp xếp theo độ tương đồng giảm dần
        productSimilarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        // Lấy top N sản phẩm
        return productSimilarities.stream()
                .limit(limit)
                .map(entry -> {
                    ResProductDTO dto = productMapper.toResDTO(entry.getKey());
                    dto.setSimilarityScore(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private double calculateSimilarity(String text1, String text2) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text1", text1);
            requestBody.put("text2", text2);
            requestBody.put("model", "multilingual"); // Sử dụng model đa ngôn ngữ

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    PYTHON_SERVICE_URL + "/similarity",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Double) response.getBody().get("similarity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }
} 