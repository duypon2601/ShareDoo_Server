package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.service.aiSuggestionService.ProductSuggestionService;
import com.server.ShareDoo.service.SentenceEmbeddingService;
import com.server.ShareDoo.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@Tag(name = "Product Suggestions", description = "APIs for product suggestions based on semantic similarity")
@SecurityRequirement(name = "api")
public class ProductSuggestionController {
    private static final Logger logger = LoggerFactory.getLogger(ProductSuggestionController.class);
    private final ProductSuggestionService suggestionService;
    private final SentenceEmbeddingService embeddingService;

    @GetMapping("/products")
    @Operation(summary = "Get similar products", description = "Get a list of products similar to the given query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved similar products"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getSimilarProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        logger.info("Received request for similar products with query: {}", query);
        List<ResProductDTO> suggestions = suggestionService.getSimilarProducts(query, limit);
        
        if (suggestions.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Không tìm thấy sản phẩm phù hợp. Vui lòng thử tìm kiếm với từ khóa khác hoặc xem các sản phẩm liên quan.");
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/event-suggestions")
    @Operation(summary = "Get product suggestions for an event", description = "Uses semantic similarity to suggest products based on event details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Suggestions generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> suggestProductsForEvent(@RequestBody EventSuggestionRequest request) {
        logger.info("Received event suggestion request: {}", request.getEventDescription());
        String query = String.format("Sự kiện: %s, Ngân sách: %s, Số khách: %d", 
            request.getEventDescription(), 
            request.getBudget(), 
            request.getGuestCount());
            
        List<ResProductDTO> suggestions = suggestionService.getSimilarProducts(query, 10);
        
        if (suggestions.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Hiện tại không có sản phẩm phù hợp cho sự kiện của bạn. Vui lòng thử tìm kiếm với yêu cầu khác hoặc liên hệ với chúng tôi để được tư vấn thêm.");
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/user-suggestions/{userId}")
    @Operation(summary = "Get personalized product suggestions", description = "Uses semantic similarity to suggest products based on user's rental history")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Suggestions generated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> getUserSuggestions(@PathVariable Long userId) {
        logger.info("Received request for user suggestions, userId: {}", userId);
        List<Product> userHistory = suggestionService.getUserRentalHistory(userId);
        
        if (userHistory.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Chưa có lịch sử thuê sản phẩm. Vui lòng xem các sản phẩm phổ biến hoặc liên hệ với chúng tôi để được tư vấn.");
            return ResponseEntity.ok(response);
        }
        
        String userHistoryText = userHistory.stream()
            .map(p -> p.getName() + " " + p.getDescription())
            .collect(Collectors.joining(" "));
            
        List<ResProductDTO> suggestions = suggestionService.getSimilarProducts(userHistoryText, 5);
        
        if (suggestions.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Không tìm thấy sản phẩm phù hợp với lịch sử thuê của bạn. Vui lòng xem các sản phẩm liên quan hoặc liên hệ với chúng tôi để được tư vấn.");
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.ok(suggestions);
    }

    public static class EventSuggestionRequest {
        private String eventDescription;
        private String budget;
        private Integer guestCount;

        // Getters and setters
        public String getEventDescription() {
            return eventDescription;
        }

        public void setEventDescription(String eventDescription) {
            this.eventDescription = eventDescription;
        }

        public String getBudget() {
            return budget;
        }

        public void setBudget(String budget) {
            this.budget = budget;
        }

        public Integer getGuestCount() {
            return guestCount;
        }

        public void setGuestCount(Integer guestCount) {
            this.guestCount = guestCount;
        }
    }
} 