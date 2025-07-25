package com.server.ShareDoo.service.rentalService;

import com.server.ShareDoo.dto.request.RentalRequest.RentalRequestDTO;
import com.server.ShareDoo.dto.response.RentalResponse.RentalResponseDTO;
import com.server.ShareDoo.entity.Product;
import com.server.ShareDoo.entity.Rental;
import com.server.ShareDoo.entity.User;
import com.server.ShareDoo.mapper.RentalMapper;
import com.server.ShareDoo.repository.RentalRepository;
import com.server.ShareDoo.repository.UserRepository;
import com.server.ShareDoo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import vn.payos.PayOS;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.CheckoutResponseData;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private RentalMapper rentalMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PayOS payOS;

    @Value("${PAYOS_RETURN_URL}")
    private String payosReturnUrl;

    @Value("${PAYOS_CANCEL_URL}")
    private String payosCancelUrl;

    public RentalResponseDTO createRental(RentalRequestDTO rentalRequestDTO) {
        Rental rental = rentalMapper.toEntity(rentalRequestDTO);
        
        User user = userRepository.findById(rentalRequestDTO.getUserId().intValue())
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(rentalRequestDTO.getProductId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
            
        rental.setUser(user);
        rental.setProduct(product);
        rental.setStatus("pending");
        rental.setDeletedAt(null);
        // Sinh orderCode cho PayOS và lưu vào rental
        long orderCode = System.currentTimeMillis() % 1000000;
        rental.setOrderCode(orderCode);
        Rental savedRental = rentalRepository.save(rental);

        // Tạo payment link PayOS
        String paymentUrl = null;
        try {
            ItemData item = ItemData.builder()
                .name(product.getName())
                .price(product.getPricePerDay().intValue())
                .quantity(1)
                .build();
            // Tạo mô tả ngắn gọn, tối đa 25 ký tự cho PayOS
            String description = ("Thuê: " + product.getName());
            if (description.length() > 25) {
                description = description.substring(0, 25);
            }
            PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(product.getPricePerDay().intValue())
                .description(description)
                .returnUrl(payosReturnUrl)
                .cancelUrl(payosCancelUrl)
                .item(item)
                .build();
            CheckoutResponseData data = payOS.createPaymentLink(paymentData);
            paymentUrl = data.getCheckoutUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RentalResponseDTO responseDTO = rentalMapper.toDto(savedRental);
        responseDTO.setPaymentUrl(paymentUrl); // Cần thêm field paymentUrl vào RentalResponseDTO
        return responseDTO;
    }

    public boolean isRentalAvailable(Long userId, Long productId) {
        return !rentalRepository.existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(userId.intValue(), productId, "completed");
    }

    public String getUserRentalHistory(Long userId) {
        List<Rental> rentals = rentalRepository.findByUser_UserIdAndDeletedAtIsNull(userId.intValue());
        return rentals.stream()
            .map(rental -> String.format(
                "Sản phẩm: %s, Ngày thuê: %s, Trạng thái: %s",
                rental.getProduct().getName(),
                rental.getCreatedAt(),
                rental.getStatus()
            ))
            .collect(Collectors.joining("\n"));
    }

    public String getRentalTrendsData() {
        List<Rental> recentRentals = rentalRepository.findTop100ByDeletedAtIsNullOrderByCreatedAtDesc();
        return recentRentals.stream()
            .map(rental -> String.format(
                "Sản phẩm: %s, Danh mục: %s, Ngày thuê: %s, Trạng thái: %s",
                rental.getProduct().getName(),
                rental.getProduct().getCategory(),
                rental.getCreatedAt(),
                rental.getStatus()
            ))
            .collect(Collectors.joining("\n"));
    }

    public Rental findByOrderCode(Long orderCode) {
        return rentalRepository.findByOrderCode(orderCode);
    }
    public Rental save(Rental rental) {
        return rentalRepository.save(rental);
    }
}