package com.libmanage.config;

import com.libmanage.model.User;
import com.libmanage.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof AnonymousAuthenticationToken && request.getUserPrincipal() != null) {
            String username = request.getUserPrincipal().getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            RoleGrantedAuthority authority = new RoleGrantedAuthority("ROLE_" + user.getRole().getRoleName());

            CustomAuthenticationToken customAuth = new CustomAuthenticationToken(
                    user.getUsername(),
                    null,
                    user.id(),
                    List.of(authority)
            );

            SecurityContextHolder.getContext().setAuthentication(customAuth);
        }

        filterChain.doFilter(request, response);
    }
}
