package com.libmanage.controller.interfaces;

import com.libmanage.dto.ReservationDTO;
import com.libmanage.dto.UserDTO;
import com.libmanage.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public interface AdminController {

    // Удаление пользователя по ID
    @DeleteMapping("/users/{id}")
    ResponseEntity<Void> deleteUser(@PathVariable Integer id);

    @GetMapping("/reservations")
    ResponseEntity<List<ReservationDTO>> getAllReservations();

    @GetMapping("/users")
    ResponseEntity<List<UserDTO>> getAllUsers();

    @PostMapping("/reservations/{id}/status")
    ResponseEntity<Void> changeReservationStatus(@PathVariable Integer id,
                                                        @RequestParam String status);
}