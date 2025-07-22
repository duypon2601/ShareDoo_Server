package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.service.productService.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@SecurityRequirement(name = "api")
@Tag(name = "Product Management", description = "Product management APIs")
@RequestMapping("/api/products")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private static final String API_URL = "https://api-inference.huggingface.co/models/tiiuae/falcon-7b-instruct";

    @PostMapping
    @Operation(summary = "Create a new product", description = "Allows authenticated users to create a product listing.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ResProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return new ResponseEntity<>(productService.createProduct(productDTO, userId), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<ResProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves a specific product by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ResProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Update a product", description = "Updates an existing product. Only the owner or admin can update.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ResProductDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(productService.updateProduct(id, productDTO, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Delete a product", description = "Soft deletes a product (marks as deleted).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        productService.deleteProduct(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products with various filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<Page<ResProductDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return ResponseEntity.ok(productService.searchProducts(keyword, category, minPrice, maxPrice, pageable));
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's own products", description = "Retrieves all products created by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ResProductDTO>> getMyProducts(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(productService.getMyProducts(userId));
    }

    @GetMapping("/my-products/active")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get user's active products", description = "Retrieves active products created by the authenticated user.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Active user products retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<ResProductDTO>> getMyActiveProducts(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(productService.getMyActiveProducts(userId));
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            logger.error("Invalid authentication or JWT token");
            throw new IllegalArgumentException("Invalid authentication or JWT token");
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        // Log all claims for debugging
        Map<String, Object> claims = jwt.getClaims();
        logger.info("JWT Claims: {}", claims);

        // Try to get userId from different claims
        Object userIdObj = claims.get("userId");
        if (userIdObj != null) {
            logger.info("Found userId in 'userId' claim: {}", userIdObj);
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                try {
                    return Long.valueOf((String) userIdObj);
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse userId from 'userId' claim: {}", userIdObj);
                }
            }
        }

        // Try to get from 'sub' claim
        String sub = jwt.getSubject();
        if (sub != null) {
            logger.info("Found subject in 'sub' claim: {}", sub);
            try {
                return Long.valueOf(sub);
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse userId from 'sub' claim: {}", sub);
            }
        }

        // Try to get from 'user_id' claim
        userIdObj = claims.get("user_id");
        if (userIdObj != null) {
            logger.info("Found userId in 'user_id' claim: {}", userIdObj);
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                try {
                    return Long.valueOf((String) userIdObj);
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse userId from 'user_id' claim: {}", userIdObj);
                }
            }
        }

        // Try to get from 'id' claim
        userIdObj = claims.get("id");
        if (userIdObj != null) {
            logger.info("Found userId in 'id' claim: {}", userIdObj);
            if (userIdObj instanceof Number) {
                return ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                try {
                    return Long.valueOf((String) userIdObj);
                } catch (NumberFormatException e) {
                    logger.warn("Failed to parse userId from 'id' claim: {}", userIdObj);
                }
            }
        }

        logger.error("Could not find valid userId in JWT claims: {}", claims);
        throw new IllegalArgumentException("Could not extract valid userId from JWT token");
    }
}