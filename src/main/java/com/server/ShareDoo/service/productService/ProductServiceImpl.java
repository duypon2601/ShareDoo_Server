package com.server.ShareDoo.service.productService;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ResProductDTO createProduct(ProductDTO productDTO, Long userId) {
        validateProduct(productDTO);
        Product product = productMapper.toEntity(productDTO);
        product.setUserId(userId.intValue());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        return productMapper.toResDTO(savedProduct);
    }

    private void validateProduct(ProductDTO productDTO) {
        if (productDTO.getPricePerDay().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per day must be greater than 0");
        }
        if (productDTO.getAvailabilityStatus() == null) {
            throw new IllegalArgumentException("Availability status is required");
        }
    }

    @Override
    public Page<ResProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAllActive(pageable)
                .map(productMapper::toResDTO);
    }

    @Override
    public ResProductDTO getProductById(Long id) {
        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found or deleted"));
        return productMapper.toResDTO(product);
    }

    @Override
    @Transactional
    public ResProductDTO updateProduct(Long id, ProductDTO productDTO, Long userId) {
        validateProduct(productDTO);
        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found or deleted"));
        if (!product.getUserId().equals(userId.intValue())) {
            throw new SecurityException("Unauthorized to update this product");
        }
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setPricePerDay(productDTO.getPricePerDay());
        product.setAvailabilityStatus(productDTO.getAvailabilityStatus());
        product.setUpdatedAt(LocalDateTime.now());
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findActiveById(id)
                .orElseThrow(() -> new NoSuchElementException("Product not found or deleted"));
        if (!product.getUserId().equals(userId.intValue())) {
            throw new SecurityException("Unauthorized to delete this product");
        }
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    public Page<ResProductDTO> searchProducts(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, category, minPrice, maxPrice, pageable)
                .map(productMapper::toResDTO);
    }

    @Override
    public String getAvailableProductsAsString() {
        List<Product> availableProducts = productRepository.findByDeletedAtIsNull();
        return availableProducts.stream()
            .map(product -> String.format(
                "ID: %d, Tên: %s, Danh mục: %s, Giá: %s, Mô tả: %s",
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getPricePerDay(),
                product.getDescription()
            ))
            .collect(Collectors.joining("\n"));
    }

    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailabilityStatus(
                ProductDTO.AvailabilityStatus.AVAILABLE);
    }

    private String getCategoryDisplayName(Category category) {
        return switch (category) {
            case CAMPING -> "Cắm trại";
            case HIKING -> "Leo núi";
            case FISHING -> "Câu cá";
            case BICYCLING -> "Đạp xe";
            case CITY -> "Thành phố";
            case BEACH -> "Biển";
            case MOUNTAINS -> "Núi";
            case FOREST -> "Rừng";
            case SKIING -> "Trượt tuyết";
            case SNOWBOARDING -> "Trượt ván tuyết";
            case OTHER -> "Khác";
        };
    }

//    public List<Product> getProductsByCategory(ProductDTO.Category category) {
//        return productRepository.findByCategory(category);
//    }
//
//    public List<Product> getAvailableProductsByCategory(ProductDTO.Category category) {
//        return productRepository.findByAvailabilityStatusAndCategory(
//                ProductDTO.AvailabilityStatus.AVAILABLE, category);
//    }
//
//    public List<Product> getUserProducts(Integer userId) {
//        return productRepository.findByUserId(userId);
//    }
//
//    public List<Product> getUserAvailableProducts(Integer userId) {
//        return productRepository.findByUserIdAndAvailabilityStatus(
//                userId, ProductDTO.AvailabilityStatus.AVAILABLE);
//    }
//
//    public List<Product> searchProducts(String keyword) {
//        return productRepository.searchByName(keyword);
//    }
}