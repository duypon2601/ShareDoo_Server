package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.service.aiSuggestionService.ProductSuggestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
@Tag(name = "Product Suggestions", description = "APIs for product suggestions based on semantic similarity")
@SecurityRequirement(name = "api")
public class ProductSuggestionController {
    private final ProductSuggestionService suggestionService;

    @GetMapping("/products")
    @Operation(summary = "Get similar products", description = "Get a list of products similar to the given query")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved similar products"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ResProductDTO>> getSimilarProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int limit) {
        List<ResProductDTO> suggestions = suggestionService.getSimilarProducts(query, limit);
        return ResponseEntity.ok(suggestions);
    }
} 