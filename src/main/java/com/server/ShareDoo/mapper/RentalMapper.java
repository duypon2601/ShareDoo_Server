package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.request.RentalRequest.RentalRequestDTO;
import com.server.ShareDoo.dto.response.RentalResponse.RentalResponseDTO;
import com.server.ShareDoo.entity.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RentalMapper {
    RentalMapper INSTANCE = Mappers.getMapper(RentalMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "product", ignore = true)
    Rental toEntity(RentalRequestDTO dto);

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "product.productId", target = "productId")
    RentalResponseDTO toDto(Rental entity);
}