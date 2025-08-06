package com.server.ShareDoo.service.rentalRequestService;

import com.server.ShareDoo.entity.RentalRequest;
import com.server.ShareDoo.repository.RentalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentalRequestService {
    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    public RentalRequest createRequest(Long rentalId, Long ownerId, String status) {
        RentalRequest request = new RentalRequest();
        request.setRentalId(rentalId);
        request.setOwnerId(ownerId);
        request.setStatus(status);
        request.setCreatedAt(java.time.LocalDateTime.now());
        return rentalRequestRepository.save(request);
    }
}
