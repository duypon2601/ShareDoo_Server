package com.server.ShareDoo.service.huggingFaceService;


import com.server.ShareDoo.dto.request.productRequest.ProductRecommendationRequest;
import com.server.ShareDoo.dto.response.huggingFaceResponse.HuggingFaceResponse;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor

public class ProductRecommendationService {
    private final HuggingFaceService huggingFaceService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ResProductDTO> recommendProductsForEvent(ProductRecommendationRequest request) {



        String processedText = preprocessInput(request.getEventDescription());
        List<String> labels = Arrays.stream(Category.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        HuggingFaceResponse response = huggingFaceService.classifyEvent(processedText, labels);

        if (response == null || response.getLabels() == null || response.getScores() == null) {
            return List.of(); // fallback hoặc trả về danh sách rỗng
        }
//        if (response == null || response.getLabels() == null) {
//            log.warn("AI service returned null response, using fallback");
//            return getFallbackRecommendations();
//        }

        List<Category> categories = IntStream.range(0, response.getLabels().size())
                .filter(i -> response.getScores().get(i) > 0.5)
                .mapToObj(i -> Category.valueOf(response.getLabels().get(i).toUpperCase()))
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAvailableProductsByCategories(categories);

        return products.stream()
                .filter(p -> request.getMaxPricePerDay() == null || p.getPricePerDay().compareTo(request.getMaxPricePerDay()) <= 0)
                .map(productMapper::toResDTO)
                .collect(Collectors.toList());
    }

    private String preprocessInput(String text) {
        return text == null ? "" : text.toLowerCase().replaceAll("[^a-z0-9\\s]", "").replaceAll("\\s+", " ").trim();
    }
}
