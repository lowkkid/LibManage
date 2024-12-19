package com.libmanage.service;

import com.libmanage.dto.ReservationDTO;
import com.libmanage.dto.UserDTO;
import com.libmanage.exception.EntityNotFoundException;
import com.libmanage.model.Reservation;
import com.libmanage.model.ReservationStatus;
import com.libmanage.model.User;
import com.libmanage.repository.ReservationRepository;
import com.libmanage.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public AdminService(UserRepository userRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<UserDTO> getUserList() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationDTO::fromEntity)
                .toList();
    }

    public void changeReservationStatus(Integer reservationId, String status) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        reservation.setStatus(ReservationStatus.valueOf(status));
        reservationRepository.save(reservation);
    }
}

