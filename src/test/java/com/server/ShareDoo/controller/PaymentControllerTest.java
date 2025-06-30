package com.server.ShareDoo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.ShareDoo.dto.request.paymentRequest.CreateOrderRequest;
import com.server.ShareDoo.dto.response.paymentResponse.OrderResponse;
import com.server.ShareDoo.dto.response.paymentResponse.PaymentLinkResponse;
import com.server.ShareDoo.service.paymentService.PaymentService;
import com.server.ShareDoo.util.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private SecurityUtil securityUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CreateOrderRequest createOrderRequest;
    private OrderResponse orderResponse;
    private PaymentLinkResponse paymentLinkResponse;

    @BeforeEach
    void setUp() {
        // Setup test data
        createOrderRequest = new CreateOrderRequest();
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setNotes("Test item");
        createOrderRequest.setItems(Arrays.asList(itemRequest));
        createOrderRequest.setDescription("Test order");

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setOrderCode("ORDER_TEST001");
        orderResponse.setUserId(1L);
        orderResponse.setUserName("Test User");
        orderResponse.setTotalAmount(new BigDecimal("500000"));
        orderResponse.setStatus("INIT");

        paymentLinkResponse = new PaymentLinkResponse();
        paymentLinkResponse.setCheckoutUrl("https://payos.vn/checkout/test");
        paymentLinkResponse.setOrderCode("ORDER_TEST001");
        paymentLinkResponse.setOrderId(1L);
        paymentLinkResponse.setAmount(new BigDecimal("500000"));
        paymentLinkResponse.setStatus("WAIT_FOR_PAYMENT");
        paymentLinkResponse.setMessage("Payment link created successfully");
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_ShouldReturnOrderResponse() throws Exception {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(paymentService.createOrder(any(CreateOrderRequest.class), eq(1L)))
                .thenReturn(orderResponse);

        mockMvc.perform(post("/api/payment/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderCode").value("ORDER_TEST001"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createPaymentLink_ShouldReturnPaymentLinkResponse() throws Exception {
        when(paymentService.createPaymentLink(1L)).thenReturn(paymentLinkResponse);

        mockMvc.perform(post("/api/payment/create-payment-link/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.checkoutUrl").value("https://payos.vn/checkout/test"))
                .andExpect(jsonPath("$.data.orderCode").value("ORDER_TEST001"));
    }

    @Test
    void confirmPayment_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/payment/confirm")
                        .param("orderCode", "ORDER_TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Payment confirmed successfully"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderById_ShouldReturnOrderResponse() throws Exception {
        when(paymentService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/payment/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderCode").value("ORDER_TEST001"));
    }

    @Test
    void getOrderByOrderCode_ShouldReturnOrderResponse() throws Exception {
        when(paymentService.getOrderByOrderCode("ORDER_TEST001")).thenReturn(orderResponse);

        mockMvc.perform(get("/api/payment/orders/code/ORDER_TEST001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.orderCode").value("ORDER_TEST001"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserOrders_ShouldReturnOrderList() throws Exception {
        when(securityUtil.getCurrentUserId()).thenReturn(1L);
        when(paymentService.getOrdersByUserId(1L))
                .thenReturn(Arrays.asList(orderResponse));

        mockMvc.perform(get("/api/payment/orders/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderCode").value("ORDER_TEST001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrdersByStatus_ShouldReturnOrderList() throws Exception {
        when(paymentService.getOrdersByStatus("INIT"))
                .thenReturn(Arrays.asList(orderResponse));

        mockMvc.perform(get("/api/payment/orders/status/INIT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].status").value("INIT"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void cancelOrder_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/payment/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("Order cancelled successfully"));
    }

    @Test
    void paymentWebhook_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/payment/webhook")
                        .param("orderCode", "ORDER_TEST001")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
} 