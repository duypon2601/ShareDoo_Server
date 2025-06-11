package com.server.ShareDoo.controller;


import com.server.ShareDoo.dto.request.RentalRequest.RentalRequestDTO;
import com.server.ShareDoo.dto.response.RentalResponse.RentalResponseDTO;
import com.server.ShareDoo.service.rentalService.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(@RequestBody RentalRequestDTO rentalRequestDTO) {
        if (rentalService.isRentalAvailable(rentalRequestDTO.getUserId(), rentalRequestDTO.getProductId())) {
            RentalResponseDTO response = rentalService.createRental(rentalRequestDTO);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}