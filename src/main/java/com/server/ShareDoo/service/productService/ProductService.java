package com.server.ShareDoo.service.productService;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ResProductDTO createProduct(ProductDTO productDTO, Long userId);
    Page<ResProductDTO> getProducts(Pageable pageable);
    ResProductDTO getProductById(Long id);
    ResProductDTO updateProduct(Long id, ProductDTO productDTO, Long userId);
    void deleteProduct(Long id, Long userId);
    Page<ResProductDTO> searchProducts(String keyword, Category category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    String getAvailableProductsAsString();
    List<Product> getAvailableProducts();
//    List<Product> getProductsByCategory(Category category);
//    List<Product> getAvailableProductsByCategory(Category category);
//    List<Product> getUserProducts(Integer userId);
//    List<Product> getUserAvailableProducts(Integer userId);
//    List<Product> searchProducts(String keyword);
}