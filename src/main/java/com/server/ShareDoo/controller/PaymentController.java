package com.server.ShareDoo.controller;

import com.server.ShareDoo.dto.request.paymentRequest.CreateOrderRequest;
import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse;
import com.server.ShareDoo.dto.response.paymentResponse.PaymentLinkResponse;
import com.server.ShareDoo.dto.response.RestResponse;
import com.server.ShareDoo.service.paymentService.PaymentService;
import com.server.ShareDoo.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment management APIs")
public class PaymentController {
    
    private final PaymentService paymentService;
    private final SecurityUtil securityUtil;
    
    @PostMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new order", description = "Create a new order for payment")
    public ResponseEntity<RestResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Long userId = securityUtil.getCurrentUserId();
        OrderResponse order = paymentService.createOrder(request, userId);
        return ResponseEntity.ok(RestResponse.success(order, "Order created successfully"));
    }
    
    @PostMapping("/create-payment-link/{orderId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create payment link", description = "Create payment link with QR code for an order")
    public ResponseEntity<RestResponse<PaymentLinkResponse>> createPaymentLink(@PathVariable Long orderId) {
        PaymentLinkResponse paymentLink = paymentService.createPaymentLink(orderId);
        return ResponseEntity.ok(RestResponse.success(paymentLink, "Payment link created successfully"));
    }
    
    @PostMapping("/confirm")
    @Operation(summary = "Confirm payment", description = "Confirm payment after successful transaction")
    public ResponseEntity<RestResponse<String>> confirmPayment(@RequestParam String orderCode) {
        paymentService.confirmPayment(orderCode);
        return ResponseEntity.ok(RestResponse.success("Payment confirmed", "Payment confirmed successfully"));
    }
    
    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order by ID", description = "Get order details by order ID")
    public ResponseEntity<RestResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = paymentService.getOrderById(orderId);
        return ResponseEntity.ok(RestResponse.success(order, "Order retrieved successfully"));
    }
    
    @GetMapping("/orders/code/{orderCode}")
    @Operation(summary = "Get order by order code", description = "Get order details by order code")
    public ResponseEntity<RestResponse<OrderResponse>> getOrderByOrderCode(@PathVariable String orderCode) {
        OrderResponse order = paymentService.getOrderByOrderCode(orderCode);
        return ResponseEntity.ok(RestResponse.success(order, "Order retrieved successfully"));
    }
    
    @GetMapping("/orders/user")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user orders", description = "Get all orders for current user")
    public ResponseEntity<RestResponse<List<OrderResponse>>> getUserOrders() {
        Long userId = securityUtil.getCurrentUserId();
        List<OrderResponse> orders = paymentService.getOrdersByUserId(userId);
        return ResponseEntity.ok(RestResponse.success(orders, "User orders retrieved successfully"));
    }
    
    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get orders by status", description = "Get all orders by status (Admin only)")
    public ResponseEntity<RestResponse<List<OrderResponse>>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = paymentService.getOrdersByStatus(status);
        return ResponseEntity.ok(RestResponse.success(orders, "Orders retrieved successfully"));
    }
    
    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cancel order", description = "Cancel an order")
    public ResponseEntity<RestResponse<String>> cancelOrder(@PathVariable Long orderId) {
        paymentService.cancelOrder(orderId);
        return ResponseEntity.ok(RestResponse.success("Order cancelled", "Order cancelled successfully"));
    }
    
    @PostMapping("/webhook")
    @Operation(summary = "Payment webhook", description = "Webhook endpoint for PayOS payment notifications")
    public ResponseEntity<String> paymentWebhook(@RequestParam String orderCode, @RequestParam String status) {
        if ("PAID".equals(status)) {
            paymentService.confirmPayment(orderCode);
        }
        return ResponseEntity.ok("OK");
    }
} 