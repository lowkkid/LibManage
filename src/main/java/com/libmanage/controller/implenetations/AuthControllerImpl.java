package com.libmanage.controller.implenetations;

import com.libmanage.controller.interfaces.AuthController;
import com.libmanage.dto.LoginRequest;
import com.libmanage.dto.RegisterRequest;
import com.libmanage.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class AuthControllerImpl implements AuthController {

    private final UserService userService;

    public AuthControllerImpl(UserService userService) {
        this.userService = userService;
    }

    public ResponseEntity<?> login(LoginRequest request) {
        return ResponseEntity.ok(userService.authenticate(request.getUsername(), request.getPassword()));
    }

    Logger log = Logger.getLogger(this.getClass().getName());

    public ResponseEntity<?> register(RegisterRequest request) {
        log.info(request.toString());
        userService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
