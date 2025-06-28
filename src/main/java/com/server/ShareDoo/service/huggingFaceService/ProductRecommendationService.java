package com.server.ShareDoo.service.huggingFaceService;

import com.server.ShareDoo.dto.request.productRequest.ProductRecommendationRequest;
import com.server.ShareDoo.dto.response.huggingFaceResponse.HuggingFaceResponse;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductRecommendationService {
    private final HuggingFaceService huggingFaceService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Value("${ai.recommendation.threshold:0.3}")
    private double threshold;

    @Value("${ai.recommendation.max-recommendations:10}")
    private int maxRecommendations;

    @Value("${ai.recommendation.fallback-enabled:true}")
    private boolean fallbackEnabled;

    // Keyword mapping cho việc fallback
    private static final Map<String, List<Category>> KEYWORD_CATEGORY_MAPPING = createKeywordMapping();

    private static Map<String, List<Category>> createKeywordMapping() {
        Map<String, List<Category>> mapping = new HashMap<>();
        mapping.put("camping", Arrays.asList(Category.CAMPING, Category.FOREST));
        mapping.put("hiking", Arrays.asList(Category.HIKING, Category.MOUNTAINS));
        mapping.put("fishing", Arrays.asList(Category.FISHING));
        mapping.put("bicycling", Arrays.asList(Category.BICYCLING, Category.CITY));
        mapping.put("beach", Arrays.asList(Category.BEACH));
        mapping.put("skiing", Arrays.asList(Category.SKIING));
        mapping.put("snowboarding", Arrays.asList(Category.SNOWBOARDING));
        mapping.put("mountain", Arrays.asList(Category.MOUNTAINS, Category.HIKING));
        mapping.put("forest", Arrays.asList(Category.FOREST, Category.CAMPING));
        mapping.put("city", Arrays.asList(Category.CITY, Category.BICYCLING));
        mapping.put("outdoor", Arrays.asList(Category.CAMPING, Category.HIKING, Category.FISHING));
        mapping.put("adventure", Arrays.asList(Category.HIKING, Category.MOUNTAINS, Category.CAMPING));
        return mapping;
    }

    public List<ResProductDTO> recommendProductsForEvent(ProductRecommendationRequest request) {
        log.info("Starting product recommendation for event: {}", request.getEventDescription());
        
        String processedText = preprocessInput(request.getEventDescription());
        List<String> labels = Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        // Gọi AI service
        HuggingFaceResponse aiResponse = huggingFaceService.classifyEvent(processedText, labels);
        
        List<Category> selectedCategories = new ArrayList<>();
        
        if (aiResponse != null && aiResponse.getLabels() != null && aiResponse.getScores() != null) {
            // Lọc categories dựa trên AI scores
            selectedCategories = IntStream.range(0, aiResponse.getLabels().size())
                    .filter(i -> aiResponse.getScores().get(i) > threshold)
                    .mapToObj(i -> {
                        try {
                            return Category.valueOf(aiResponse.getLabels().get(i).toUpperCase());
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid category from AI: {}", aiResponse.getLabels().get(i));
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        // Fallback logic nếu AI không trả về kết quả hoặc kết quả không đủ
        if (selectedCategories.isEmpty() && fallbackEnabled) {
            log.info("AI returned no categories, using keyword-based fallback");
            selectedCategories = getCategoriesFromKeywords(processedText);
        }

        // Nếu vẫn không có categories, trả về tất cả sản phẩm có sẵn
        if (selectedCategories.isEmpty()) {
            log.info("No categories found, returning all available products");
            List<Product> allProducts = productRepository.findAvailableProducts(
                com.server.ShareDoo.dto.request.productRequest.ProductDTO.AvailabilityStatus.AVAILABLE
            );
            return filterAndLimitProducts(allProducts, request.getMaxPricePerDay());
        }

        log.info("Selected categories: {}", selectedCategories);
        
        // Lấy sản phẩm theo categories
        List<Product> products = productRepository.findAvailableProductsByCategories(selectedCategories);
        
        return filterAndLimitProducts(products, request.getMaxPricePerDay());
    }

    private List<Category> getCategoriesFromKeywords(String text) {
        String lowerText = text.toLowerCase();
        Set<Category> categories = new HashSet<>();
        
        for (Map.Entry<String, List<Category>> entry : KEYWORD_CATEGORY_MAPPING.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                categories.addAll(entry.getValue());
            }
        }
        
        log.info("Keyword-based categories found: {}", categories);
        return new ArrayList<>(categories);
    }

    private List<ResProductDTO> filterAndLimitProducts(List<Product> products, java.math.BigDecimal maxPrice) {
        return products.stream()
                .filter(p -> maxPrice == null || p.getPricePerDay().compareTo(maxPrice) <= 0)
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt())) // Sắp xếp theo thời gian tạo mới nhất
                .limit(maxRecommendations)
                .map(productMapper::toResDTO)
                .collect(Collectors.toList());
    }

    private String preprocessInput(String text) {
        if (text == null) return "";
        
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ") // Loại bỏ ký tự đặc biệt
                .replaceAll("\\s+", " ") // Chuẩn hóa khoảng trắng
                .trim();
    }
}
