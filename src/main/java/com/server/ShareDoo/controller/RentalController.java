package com.server.ShareDoo.controller;

import java.util.Map;


import com.server.ShareDoo.dto.request.RentalRequest.RentalRequestDTO;
import com.server.ShareDoo.dto.response.RentalResponse.RentalResponseDTO;
import com.server.ShareDoo.service.rentalService.RentalService;
import com.server.ShareDoo.service.walletService.WalletService;
import com.server.ShareDoo.repository.UserRepository;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.payos.PayOS;
import vn.payos.type.WebhookData;
import vn.payos.type.Webhook;
import com.server.ShareDoo.entity.Rental;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private PayOS payOS;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@RequestBody RentalRequestDTO rentalRequestDTO) {
        if (rentalService.isRentalAvailable(rentalRequestDTO.getUserId(), rentalRequestDTO.getProductId())) {
            RentalResponseDTO response = rentalService.createRental(rentalRequestDTO);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // Endpoint nhận webhook từ PayOS
    @PostMapping("/payos-webhook")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody ObjectNode webhookBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Webhook webhook = objectMapper.treeToValue(webhookBody, Webhook.class);
            WebhookData data = payOS.verifyPaymentWebhookData(webhook);
            // In log toàn bộ object để xác định trường trạng thái
            System.out.println("WebhookData: " + objectMapper.writeValueAsString(data));
            // Xử lý cập nhật trạng thái rental khi thanh toán thành công
            // Ưu tiên transactionStatus, nếu không có thì thử status
            String status = null;
            if (data != null) {
                try {
                    status = (String) data.getClass().getMethod("getTransactionStatus").invoke(data);
                } catch (Exception ignore) {}
                if (status == null) {
                    try {
                        status = (String) data.getClass().getMethod("getStatus").invoke(data);
                    } catch (Exception ignore) {}
                }
                Long orderCode = null;
                try {
                    orderCode = (Long) data.getClass().getMethod("getOrderCode").invoke(data);
                } catch (Exception ignore) {}
                if (orderCode != null && status != null && "PAID".equalsIgnoreCase(status)) {
                    Rental rental = rentalService.findByOrderCode(orderCode);
                    if (rental != null) {
                        rental.setStatus("paid");
                        rentalService.save(rental);
                        return ResponseEntity.ok("Webhook processed: payment success");
                    }
                }
            }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }

    // Endpoint cho FE redirect về (nếu cần kiểm tra trạng thái qua query param)
    @Autowired
    private com.server.ShareDoo.repository.ProductRepository productRepository;
    @Autowired
    private com.server.ShareDoo.service.rentalRequestService.RentalRequestService rentalRequestService;
    @Autowired
    private com.server.ShareDoo.util.SecurityUtil securityUtil;

    @PostMapping("/payment-status")
    public ResponseEntity<?> paymentStatus(@RequestBody Map<String, Object> body) {
        Long orderCode = null;
        String status = null;
        if (body.get("orderCode") != null) orderCode = Long.valueOf(body.get("orderCode").toString());
        if (body.get("status") != null) status = body.get("status").toString();
        if (orderCode != null && status != null && "PAID".equalsIgnoreCase(status)) {
            Rental rental = rentalService.findByOrderCode(orderCode);
            if (rental != null) {
                rental.setStatus("paid");
                rentalService.save(rental);
                // Lấy ownerId từ product
                com.server.ShareDoo.entity.Product product = rental.getProduct();
                if (product != null) {
                    Long userId = product.getUserId().longValue();
                    rentalRequestService.createRequest(rental.getId(), userId, "pending");
                }
                return ResponseEntity.ok(rental);
            }
            return ResponseEntity.badRequest().body("Rental not found");
        }
        return ResponseEntity.badRequest().body("Missing or invalid params");
    }

    // API: Người thuê xác nhận đã nhận hàng (packed -> received)
    @PostMapping("/mark-received")
    public ResponseEntity<?> markReceived(@RequestParam("orderCode") Long orderCode) {
        Rental rental = rentalService.findByOrderCode(orderCode);
        if (rental == null) return ResponseEntity.badRequest().body("Rental not found");
        if (!"packed".equalsIgnoreCase(rental.getStatus())) {
            return ResponseEntity.badRequest().body("Chỉ có thể xác nhận nhận hàng khi trạng thái là 'packed'");
        }
        rental.setStatus("received");
        rentalService.save(rental);
        return ResponseEntity.ok("Đã xác nhận nhận hàng thành công");
    }

    // API: Chủ sở hữu xác nhận đã bàn giao (received -> handover)
    @PostMapping("/mark-handover")
    public ResponseEntity<?> markHandover(@RequestParam("orderCode") Long orderCode) {
        Rental rental = rentalService.findByOrderCode(orderCode);
        if (rental == null) return ResponseEntity.badRequest().body("Rental not found");
        if (!"received".equalsIgnoreCase(rental.getStatus())) {
            return ResponseEntity.badRequest().body("Chỉ có thể xác nhận bàn giao khi trạng thái là 'received'");
        }
        rental.setStatus("handover");
        rentalService.save(rental);
        return ResponseEntity.ok("Đã xác nhận bàn giao thành công");
    }

    // API: Người thuê xác nhận đã trả hàng (return_wait -> returned)
    @PostMapping("/mark-returned")
    public ResponseEntity<?> markReturned(@RequestParam("orderCode") Long orderCode) {
        Rental rental = rentalService.findByOrderCode(orderCode);
        if (rental == null) return ResponseEntity.badRequest().body("Rental not found");
        if (!"return_wait".equalsIgnoreCase(rental.getStatus())) {
            return ResponseEntity.badRequest().body("Chỉ có thể xác nhận trả hàng khi trạng thái là 'return_wait'");
        }
        // 1. Chuyển trạng thái đơn hàng
        rental.setStatus("returned");
        rentalService.save(rental);
        // 2. Cộng tiền cho chủ sở hữu
        com.server.ShareDoo.entity.Product product = rental.getProduct();
        if (product == null) return ResponseEntity.badRequest().body("Product not found");
        Integer ownerId = product.getUserId();
        if (ownerId == null) return ResponseEntity.badRequest().body("Product owner not found");
        // Lấy ví chủ sở hữu và cộng tiền
        try {
            java.math.BigDecimal amount = java.math.BigDecimal.valueOf(rental.getTotalPrice());
            walletService.deposit(
                userRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found")),
                amount,
                "Cộng tiền cho chủ sở hữu khi trả hàng. Rental: " + rental.getId(),
                rental.getOrderCode()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể cộng tiền cho chủ sở hữu: " + e.getMessage());
        }
        // 3. Đặt trạng thái sản phẩm thành AVAILABLE
        product.setAvailabilityStatus(com.server.ShareDoo.dto.request.productRequest.ProductDTO.AvailabilityStatus.AVAILABLE);
        productRepository.save(product);
        return ResponseEntity.ok("Đã xác nhận trả hàng thành công, chủ sở hữu đã được cộng tiền và sản phẩm đã mở lại cho thuê.");
    }

    // API hủy đơn hàng (cancel rental)
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelRental(@RequestParam("orderCode") Long orderCode) {
        Rental rental = rentalService.findByOrderCode(orderCode);
        if (rental != null) {
            rental.setStatus("cancelled");
            rentalService.save(rental);
            return ResponseEntity.ok("Rental cancelled successfully");
        }
        return ResponseEntity.badRequest().body("Rental not found");
    }

    // API lấy danh sách đơn thuê thực tế cho FE
    @GetMapping("/list")
    public ResponseEntity<?> getRentalList(@RequestParam(value = "userId", required = false) Long userId) {
        if (userId != null) {
            return ResponseEntity.ok(rentalService.getRentalListByUser(userId));
        }
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    // API lấy chi tiết đơn thuê thực tế cho FE
    @GetMapping("/detail")
    public ResponseEntity<?> getRentalDetail(@RequestParam(value = "id", required = false) Long id,
                                             @RequestParam(value = "orderCode", required = false) Long orderCode) {
        Rental rental = null;
        if (id != null) rental = rentalService.findById(id);
        else if (orderCode != null) rental = rentalService.findByOrderCode(orderCode);
        if (rental != null) return ResponseEntity.ok(rental);
        return ResponseEntity.badRequest().body("Rental not found");
    }

    // API lấy danh sách đơn hàng cho chủ sở hữu sản phẩm
    @GetMapping("/owner-list")
    public ResponseEntity<?> getRentalsByOwner(@RequestParam(value = "userId", required = false) Long userId,
                                               @RequestParam(value = "status", required = false) String status) {
        // Nếu không truyền userId thì lấy từ SecurityUtil (user đăng nhập)
        if (userId == null) {
            try {
                userId = securityUtil.getCurrentUserId();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Missing or invalid userId");
            }
        }
        if (status != null && !status.isEmpty()) {
            return ResponseEntity.ok(rentalService.getRentalsByOwnerAndStatus(userId, status));
        }
        return ResponseEntity.ok(rentalService.getRentalsByOwner(userId));
    }
}