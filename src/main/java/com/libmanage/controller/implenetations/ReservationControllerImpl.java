package com.libmanage.controller.implenetations;

import com.libmanage.controller.interfaces.ReservationController;
import com.libmanage.dto.CreateReservationRequest;
import com.libmanage.dto.ReservationDTO;
import com.libmanage.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReservationControllerImpl implements ReservationController {

    private final ReservationService reservationService;

    public ReservationControllerImpl(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @Override
    public ResponseEntity<List<ReservationDTO>> getUserReservations() {
        List<ReservationDTO> reservations = reservationService.getUserReservations();
        return ResponseEntity.ok(reservations);
    }

    @Override
    public ResponseEntity<ReservationDTO> createReservation(CreateReservationRequest request) {
        ReservationDTO reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @Override
    public ResponseEntity<Void> cancelReservation(Integer id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
