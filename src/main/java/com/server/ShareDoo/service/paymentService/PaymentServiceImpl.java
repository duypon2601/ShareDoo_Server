package com.server.ShareDoo.service.paymentService;

import com.server.ShareDoo.dto.request.paymentRequest.CreateOrderRequest;
import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse;
import com.server.ShareDoo.dto.response.paymentResponse.PaymentLinkResponse;
import com.server.ShareDoo.entity.Order;
import com.server.ShareDoo.entity.OrderItem;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.repository.OrderItemRepository;
import com.server.ShareDoo.repository.OrderRepository;
import com.server.ShareDoo.repository.ProductRepository;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.util.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.server.ShareDoo.config.PayOSConfig;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PayOSConfig payOSConfig;
    
    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        User user = userRepository.findById(userId.intValue())
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        String orderCode = generateOrderCode();
        
        Order order = new Order();
        order.setOrderCode(orderCode);
        order.setUser(user);
        order.setDescription(request.getDescription());
        order.setStatus(Order.OrderStatus.INIT);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(product.getPricePerDay());
            orderItem.setTotalPrice(product.getPricePerDay().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            orderItem.setNotes(itemRequest.getNotes());
            
            orderItems.add(orderItem);
            totalAmount = totalAmount.add(orderItem.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        
        return mapToOrderResponse(savedOrder);
    }
    
    @Override
    @Transactional
    public PaymentLinkResponse createPaymentLink(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        // Chuẩn bị payload gửi lên PayOS
        Map<String, Object> payload = new HashMap<>();
        payload.put("amount", order.getTotalAmount().intValue());
        payload.put("description", order.getDescription());
        payload.put("returnUrl", payOSConfig.getReturnUrl());
        payload.put("cancelUrl", payOSConfig.getCancelUrl());

        // Chuẩn bị header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-client-id", payOSConfig.getClientId());
        headers.set("x-api-key", payOSConfig.getApiKey());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();
        String payosApiUrl = "https://api.payos.vn/v1/payment-requests";

        ResponseEntity<String> response = restTemplate.postForEntity(payosApiUrl, request, String.class);
        String checkoutUrl = null;
        String qrCode = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> json = mapper.readValue(response.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) json.get("data");
            checkoutUrl = (String) data.get("checkoutUrl");
            qrCode = (String) data.get("qrCode");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi parse response từ PayOS", e);
        }

        order.setPaymentUrl(checkoutUrl);
        order.setPaymentOrderCode(order.getOrderCode());
        order.setStatus(Order.OrderStatus.WAIT_FOR_PAYMENT);
        orderRepository.save(order);

        return new PaymentLinkResponse(
                checkoutUrl,
                order.getOrderCode(),
                order.getId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                "Payment link created successfully",
                qrCode
        );
    }
    
    @Override
    @Transactional
    public void confirmPayment(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        
        // Simulate payment confirmation
        order.setStatus(Order.OrderStatus.PAID);
        orderRepository.save(order);
        log.info("Payment confirmed for order: {}", orderCode);
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }
    
    @Override
    public OrderResponse getOrderByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return mapToOrderResponse(order);
    }
    
    @Override
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream().map(this::mapToOrderResponse).toList();
    }
    
    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus);
        return orders.stream().map(this::mapToOrderResponse).toList();
    }
    
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        
        if (order.getStatus() == Order.OrderStatus.INIT || 
            order.getStatus() == Order.OrderStatus.WAIT_FOR_PAYMENT) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
        }
    }
    
    private String generateOrderCode() {
        String orderCode;
        do {
            orderCode = "ORDER_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderCode(order.getOrderCode());
        response.setUserId((long) order.getUser().getUserId());
        response.setUserName(order.getUser().getName());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus().name());
        response.setDescription(order.getDescription());
        response.setPaymentUrl(order.getPaymentUrl());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .toList();
        response.setItems(itemResponses);
        
        return response;
    }
    
    private OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(orderItem.getId());
        response.setProductId(orderItem.getProduct().getProductId());
        response.setProductName(orderItem.getProduct().getName());
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setNotes(orderItem.getNotes());
        return response;
    }
} 