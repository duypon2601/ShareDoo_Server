package com.server.ShareDoo.service.rentalRequestService;

import com.server.ShareDoo.entity.RentalRequest;
import com.server.ShareDoo.repository.RentalRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentalRequestServiceImpl extends RentalRequestService {
    @Autowired
    private RentalRequestRepository rentalRequestRepository;

    @Override
    public RentalRequest createRequest(Long rentalId, Long userId, String status) {
        RentalRequest request = new RentalRequest();
        request.setRentalId(rentalId);
        request.setUserId(userId);
        request.setStatus(status);
        request.setCreatedAt(java.time.LocalDateTime.now());
        return rentalRequestRepository.save(request);
    }
}
