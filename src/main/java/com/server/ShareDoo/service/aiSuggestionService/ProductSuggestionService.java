package com.server.ShareDoo.service.aiSuggestionService;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import com.server.ShareDoo.service.rentalService.RentalService;
import com.server.ShareDoo.service.SentenceEmbeddingService;
import com.server.ShareDoo.dto.EventSuggestionRequest;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.enums.AvailabilityStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductSuggestionService {
    private static final Logger logger = LoggerFactory.getLogger(ProductSuggestionService.class);
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RentalService rentalService;
    
    @Autowired
    private SentenceEmbeddingService sentenceEmbeddingService;

    private static final String PYTHON_SERVICE_URL = "http://localhost:5000";

    public List<ResProductDTO> getSimilarProducts(String query, int limit) {
        logger.info("Getting similar products for query: {}", query);
        
        // Lấy tất cả sản phẩm có sẵn
        List<Product> availableProducts = productRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        logger.info("Found {} available products", availableProducts.size());
        
        if (availableProducts.isEmpty()) {
            logger.warn("No available products found in the system");
            return Collections.emptyList();
        }
        
        // Tạo danh sách các cặp (sản phẩm, độ tương đồng)
        List<Map.Entry<Product, Double>> productSimilarities = new ArrayList<>();
        
        // Tính độ tương đồng cho từng sản phẩm
        for (Product product : availableProducts) {
            String productText = product.getName() + " " + product.getDescription();
            double similarity = calculateSimilarity(query, productText);
            logger.debug("Similarity score for product {}: {}", product.getName(), similarity);
            productSimilarities.add(new AbstractMap.SimpleEntry<>(product, similarity));
        }
        
        // Sắp xếp theo độ tương đồng giảm dần
        productSimilarities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        // Lấy top N sản phẩm
        List<ResProductDTO> suggestions = productSimilarities.stream()
                .limit(limit)
                .map(entry -> {
                    ResProductDTO dto = productMapper.toResDTO(entry.getKey());
                    dto.setSimilarityScore(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
        
        logger.info("Returning {} product suggestions", suggestions.size());
        return suggestions;
    }

    public List<Product> getUserRentalHistory(Long userId) {
        logger.info("Getting rental history for user: {}", userId);
        return rentalService.getUserRentalHistory(userId);
    }

    private double calculateSimilarity(String text1, String text2) {
        try {
            logger.debug("Calculating similarity between texts: '{}' and '{}'", text1, text2);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text1", text1);
            requestBody.put("text2", text2);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    PYTHON_SERVICE_URL + "/similarity",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                double similarity = (Double) response.getBody().get("similarity");
                logger.debug("Similarity score: {}", similarity);
                return similarity;
            }
        } catch (Exception e) {
            logger.error("Error calculating similarity: {}", e.getMessage(), e);
        }
        return 0.0;
    }

    public List<Product> getEventSuggestions(EventSuggestionRequest request) {
        logger.info("Getting event suggestions for: {}", request.getEventDescription());
        
        // Get all available products
        List<Product> allProducts = productRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        logger.info("Found {} total available products", allProducts.size());
        
        if (allProducts.isEmpty()) {
            logger.warn("No available products found in the system");
            return Collections.emptyList();
        }
        
        // Filter products by category that might be relevant for events
        List<Product> eventProducts = allProducts.stream()
            .filter(p -> p.getCategory() == Category.PARTY || 
                        p.getCategory() == Category.FURNITURE ||
                        p.getCategory() == Category.DECORATION)
            .collect(Collectors.toList());
        
        logger.info("Found {} products in relevant categories", eventProducts.size());
        
        if (eventProducts.isEmpty()) {
            logger.warn("No products found in relevant categories");
            return Collections.emptyList();
        }
        
        // Calculate similarity scores
        List<Product> productsWithScores = eventProducts.stream()
            .map(product -> {
                String productText = product.getName() + " " + product.getDescription();
                double similarity = sentenceEmbeddingService.calculateSimilarity(request.getEventDescription(), productText);
                logger.debug("Similarity score for product {}: {}", product.getName(), similarity);
                product.setSimilarityScore(similarity);
                return product;
            })
            .filter(product -> product.getSimilarityScore() > 0.1) // Filter out low similarity scores
            .sorted(Comparator.comparingDouble(Product::getSimilarityScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        logger.info("Returning {} product suggestions", productsWithScores.size());
        return productsWithScores;
    }
} 