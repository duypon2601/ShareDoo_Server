package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.request.productRequest.ProductDTO;
import com.server.ShareDoo.dto.response.productResponse.ResProductDTO;
import com.server.ShareDoo.entity.Product;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "similarityScore", ignore = true)
    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "similarityScore", ignore = true)
    Product toEntity(ResProductDTO resProductDTO);

    ResProductDTO toResDTO(Product product);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "similarityScore", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product entity);
}