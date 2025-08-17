package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.productRequest.ProductRecommendationRequest;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.service.huggingFaceService.ProductRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@SecurityRequirement(name = "api")
@Slf4j
@RequestMapping("/api/products/recommendations")
@AllArgsConstructor
public class ProductRecommendationController {

    private final ProductRecommendationService recommendationService;

    @PostMapping
    @Operation(summary = "Get product recommendations for an event")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<List<ResProductDTO>> getEventRecommendations(
            @Valid @RequestBody ProductRecommendationRequest request) {
        log.info("Received recommendation request for event: {}", request.getEventDescription());
        List<ResProductDTO> recommendations = recommendationService.recommendProductsForEvent(request);
        log.info("Returning {} recommendations", recommendations.size());
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/test")
    @Operation(summary = "Test AI recommendation with sample data")
    public ResponseEntity<String> testRecommendation() {
        ProductRecommendationRequest testRequest = new ProductRecommendationRequest();
        testRequest.setEventDescription("I want to go camping in the mountains this weekend");
        testRequest.setMaxPricePerDay(new java.math.BigDecimal("100"));
        
        log.info("Testing AI recommendation with: {}", testRequest.getEventDescription());
        List<ResProductDTO> recommendations = recommendationService.recommendProductsForEvent(testRequest);
        
        return ResponseEntity.ok("Test completed. Found " + recommendations.size() + " recommendations");
    }
}
