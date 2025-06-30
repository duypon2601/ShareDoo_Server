package com.server.ShareDoo.service.paymentService;

import com.server.ShareDoo.dto.request.paymentRequest.CreateOrderRequest;
import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse;
import com.server.ShareDoo.dto.response.paymentResponse.PaymentLinkResponse;

import java.util.List;

public interface PaymentService {
    
    OrderResponse createOrder(CreateOrderRequest request, Long userId);
    
    PaymentLinkResponse createPaymentLink(Long orderId);
    
    void confirmPayment(String orderCode);
    
    OrderResponse getOrderById(Long orderId);
    
    OrderResponse getOrderByOrderCode(String orderCode);
    
    List<OrderResponse> getOrdersByUserId(Long userId);
    
    List<OrderResponse> getOrdersByStatus(String status);
    
    void cancelOrder(Long orderId);
} 