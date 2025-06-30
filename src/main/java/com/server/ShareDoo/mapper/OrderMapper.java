package com.server.ShareDoo.mapper;

import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse;
import com.server.ShareDoo.entity.Order;
import com.server.ShareDoo.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    OrderResponse toOrderResponse(Order order);
    
    List<OrderResponse> toOrderResponseList(List<Order> orders);
    
    @Named("statusToString")
    default String statusToString(Order.OrderStatus status) {
        return status != null ? status.name() : null;
    }
} 