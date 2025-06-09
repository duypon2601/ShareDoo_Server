package com.server.ShareDoo.service.productService;


import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    ResProductDTO createProduct(ProductDTO productDTO, Long userId);
    Page<ResProductDTO> getProducts(Pageable pageable);
    ResProductDTO getProductById(Long id);
    ResProductDTO updateProduct(Long id, ProductDTO productDTO, Long userId);
    void deleteProduct(Long id, Long userId);
    Page<ResProductDTO> searchProducts(String keyword, ProductDTO.Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}