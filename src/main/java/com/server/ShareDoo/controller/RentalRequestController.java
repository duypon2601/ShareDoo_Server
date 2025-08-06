package com.server.ShareDoo.controller;

import com.server.ShareDoo.entity.RentalRequest;
import com.server.ShareDoo.entity.Rental;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.repository.RentalRequestRepository;
import com.server.ShareDoo.repository.ProductRepository;
import com.server.ShareDoo.service.rentalService.RentalService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@SecurityRequirement(name = "api")
@RestController
@RequestMapping("/api/rental-requests")
@RequiredArgsConstructor
public class RentalRequestController {
    @Autowired
    private RentalRequestRepository rentalRequestRepository;
    @Autowired
    private RentalService rentalService;
    @Autowired
    private ProductRepository productRepository;

    // API lấy danh sách request cho user đăng sản phẩm
    @Autowired
    private com.server.ShareDoo.util.SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<List<RentalRequest>> getRentalRequestsByUser() {
        Long userId = securityUtil.getCurrentUserId();
        List<RentalRequest> requests = rentalRequestRepository.findByUserId(userId)
            .stream()
            .filter(r -> "pending".equalsIgnoreCase(r.getStatus()))
            .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(requests);
    }

    // API xác nhận giao dịch (duyệt request)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmRentalRequest(@RequestParam Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId).orElse(null);
        if (request == null) return ResponseEntity.badRequest().body("Request not found");
        request.setStatus("confirmed");
        rentalRequestRepository.save(request);

        // Cập nhật trạng thái đơn hàng
        Rental rental = rentalService.findById(request.getRentalId());
        if (rental != null) {
            rental.setStatus("packed"); // Chờ bàn giao
            rentalService.save(rental);
        }

        // Cập nhật trạng thái sản phẩm
        if (rental != null && rental.getProduct() != null) {
            Product product = rental.getProduct();
            product.setAvailabilityStatus(com.server.ShareDoo.dto.request.productRequest.ProductDTO.AvailabilityStatus.UNAVAILABLE);
            productRepository.save(product);
        }
        return ResponseEntity.ok("Rental request confirmed and order updated");
    }

    // API từ chối giao dịch (cancel)
    @PostMapping("/reject")
    public ResponseEntity<?> rejectRentalRequest(@RequestParam Long requestId) {
        RentalRequest request = rentalRequestRepository.findById(requestId).orElse(null);
        if (request == null) return ResponseEntity.badRequest().body("Request not found");
        request.setStatus("rejected");
        rentalRequestRepository.save(request);

        // Cập nhật trạng thái đơn hàng
        Rental rental = rentalService.findById(request.getRentalId());
        if (rental != null) {
            rental.setStatus("cancelled");
            rentalService.save(rental);
        }
        return ResponseEntity.ok("Rental request rejected and order cancelled");
    }
}
