package com.libmanage.controller.implenetations;

import com.libmanage.controller.interfaces.AdminController;
import com.libmanage.dto.ReservationDTO;
import com.libmanage.dto.UserDTO;
import com.libmanage.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class AdminControllerImpl implements AdminController {

    private final AdminService adminService;

    public AdminControllerImpl(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public ResponseEntity<Void> deleteUser(Integer id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = adminService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = adminService.getUserList();
        for (UserDTO user : users) {
            logger.info(user.toString());
        }
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<Void> changeReservationStatus(Integer id, String status) {
        adminService.changeReservationStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
