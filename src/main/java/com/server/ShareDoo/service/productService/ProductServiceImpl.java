package com.server.ShareDoo.service.productService;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.mapper.ProductMapper;
import com.server.ShareDoo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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
    public Page<ResProductDTO> searchProducts(String keyword, ProductDTO.Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, category, minPrice, maxPrice, pageable)
                .map(productMapper::toResDTO);
    }
}