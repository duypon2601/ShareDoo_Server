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
            // Giả sử trường đúng là transactionStatus và orderCode
            // if (data != null && "PAID".equalsIgnoreCase(data.getTransactionStatus())) {
            //     Long orderCode = data.getOrderCode();
            //     Rental rental = rentalService.findByOrderCode(orderCode);
            //     if (rental != null) {
            //         rental.setStatus("paid");
            //         rentalService.save(rental);
            //     }
            //     return ResponseEntity.ok("Webhook processed: payment success");
            // }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid webhook");
        }
    }
}