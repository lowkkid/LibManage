package com.libmanage.service;

import com.libmanage.config.CustomAuthenticationToken;
import com.libmanage.config.RoleGrantedAuthority;
import com.libmanage.dto.RegisterRequest;
import com.libmanage.exception.EntityNotFoundException;
import com.libmanage.model.Role;
import com.libmanage.model.User;
import com.libmanage.repository.RoleRepository;
import com.libmanage.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;

    }


    public void registerUser(RegisterRequest registerRequest) {
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BadCredentialsException("Passwords do not match");
        }
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException("User already exists");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(roleRepository.findByRoleName("Читатель").orElseThrow(() -> new EntityNotFoundException("bad role")));
        userRepository.save(user);
    }


    Logger logger = Logger.getLogger(this.getClass().getName());
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        RoleGrantedAuthority authority = new RoleGrantedAuthority("ROLE_" + user.getRole().getRoleName());


        CustomAuthenticationToken authToken = new CustomAuthenticationToken(
                username, null, user.id(), List.of(authority)
        );

        logger.info(authToken.toString());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        logger.info(SecurityContextHolder.getContext().getAuthentication().getClass().getName());
        return user;
    }
}
