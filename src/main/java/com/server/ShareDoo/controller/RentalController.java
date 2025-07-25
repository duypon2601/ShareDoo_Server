package com.server.ShareDoo.controller;


import com.server.ShareDoo.dto.request.RentalRequest.RentalRequestDTO;
import com.server.ShareDoo.dto.response.RentalResponse.RentalResponseDTO;
import com.server.ShareDoo.service.rentalService.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.payos.PayOS;
import vn.payos.type.WebhookData;
import vn.payos.type.Webhook;
import com.server.ShareDoo.entity.Rental;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private PayOS payOS;

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
    @GetMapping("/payment-status")
    public ResponseEntity<String> paymentStatus(@RequestParam("orderCode") Long orderCode, @RequestParam("status") String status) {
        if (orderCode != null && status != null && "PAID".equalsIgnoreCase(status)) {
            Rental rental = rentalService.findByOrderCode(orderCode);
            if (rental != null) {
                rental.setStatus("paid");
                rentalService.save(rental);
                return ResponseEntity.ok("Payment success and rental updated");
            }
        }
        return ResponseEntity.ok("Payment status checked");
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
}