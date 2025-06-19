package com.server.ShareDoo.service.aiSuggestionService;

import com.server.ShareDoo.service.productService.ProductService;
import com.server.ShareDoo.service.rentalService.RentalService;
import com.server.ShareDoo.service.userService.UserService;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.service.SentenceEmbeddingService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class AiSuggestionService {

    private final SentenceEmbeddingService embeddingService;
    private final ProductService productService;
    private final RentalService rentalService;
    private final UserService userService;

    public AiSuggestionService(SentenceEmbeddingService embeddingService,
                               ProductService productService,
                               RentalService rentalService,
                               UserService userService) {
        this.embeddingService = embeddingService;
        this.productService = productService;
        this.rentalService = rentalService;
        this.userService = userService;
    }

    public List<Product> suggestProductsForEvent(String eventDescription,
                                          String budget,
                                          Integer guestCount) {
        List<Product> availableProducts = productService.getAvailableProducts();
        List<Product> suggestions = new ArrayList<>();
        
        for (Product product : availableProducts) {
            String productText = product.getName() + " " + product.getDescription();
            double similarity = embeddingService.calculateSimilarity(eventDescription, productText);
            if (similarity > 0.5) { // Ngưỡng tương đồng
                product.setSimilarityScore(similarity);
                suggestions.add(product);
            }
        }
        
        return suggestions.stream()
            .sorted(Comparator.comparingDouble(Product::getSimilarityScore).reversed())
            .collect(Collectors.toList());
    }

    public List<Product> analyzeUserRentalHistory(Long userId) {
        List<Product> userRentalHistory = rentalService.getUserRentalHistory(userId);
        List<Product> availableProducts = productService.getAvailableProducts();
        List<Product> suggestions = new ArrayList<>();
        
        String userHistoryText = userRentalHistory.stream()
            .map(p -> p.getName() + " " + p.getDescription())
            .collect(Collectors.joining(" "));
            
        for (Product product : availableProducts) {
            String productText = product.getName() + " " + product.getDescription();
            double similarity = embeddingService.calculateSimilarity(userHistoryText, productText);
            if (similarity > 0.5) {
                product.setSimilarityScore(similarity);
                suggestions.add(product);
            }
        }
        
        return suggestions.stream()
            .sorted(Comparator.comparingDouble(Product::getSimilarityScore).reversed())
            .collect(Collectors.toList());
    }

    public List<Product> predictRentalTrends() {
        String rentalData = rentalService.getRentalTrendsData();
        List<Product> availableProducts = productService.getAvailableProducts();
        List<Product> suggestions = new ArrayList<>();
        
        for (Product product : availableProducts) {
            String productText = product.getName() + " " + product.getDescription();
            double similarity = embeddingService.calculateSimilarity(rentalData, productText);
            if (similarity > 0.5) {
                product.setSimilarityScore(similarity);
                suggestions.add(product);
            }
        }
        
        return suggestions.stream()
            .sorted(Comparator.comparingDouble(Product::getSimilarityScore).reversed())
            .collect(Collectors.toList());
    }
}
