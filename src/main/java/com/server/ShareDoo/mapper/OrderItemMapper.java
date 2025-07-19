package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse.OrderItemResponse;
import com.server.ShareDoo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
    
    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems);
} 