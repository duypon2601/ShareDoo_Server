package com.server.ShareDoo.controller;


import com.server.ShareDoo.dto.request.productRequest.ProductRecommendationRequest;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;

import com.server.ShareDoo.service.huggingFaceService.ProductRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@RequestMapping("/api/products/recommendations")
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
        List<ResProductDTO> recommendations = recommendationService.recommendProductsForEvent(request);
        return ResponseEntity.ok(recommendations);
    }
}
