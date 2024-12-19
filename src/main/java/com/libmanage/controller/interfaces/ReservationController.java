package com.libmanage.controller.interfaces;


import com.libmanage.dto.CreateReservationRequest;
import com.libmanage.dto.ReservationDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/reservations")
public interface ReservationController {

    @GetMapping("/my")
    ResponseEntity<List<ReservationDTO>> getUserReservations();

    @PostMapping
    ResponseEntity<ReservationDTO> createReservation(@RequestBody CreateReservationRequest request);

    @PostMapping("/{id}/cancel")
    ResponseEntity<Void> cancelReservation(@PathVariable Integer id);
}