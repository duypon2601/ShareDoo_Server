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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

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
        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    public boolean isRentalAvailable(Long userId, Long productId) {
        return !rentalRepository.existsByUser_UserIdAndProduct_ProductIdAndStatusNotAndDeletedAtIsNull(userId.intValue(), productId, "completed");
    }
}