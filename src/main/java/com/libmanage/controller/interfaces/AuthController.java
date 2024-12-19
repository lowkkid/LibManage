package com.libmanage.controller.interfaces;

import com.libmanage.dto.LoginRequest;
import com.libmanage.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
public interface AuthController {
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest request);

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest request);
}