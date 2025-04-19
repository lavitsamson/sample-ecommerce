package com.ecommerce.config;

import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Filter for processing JWT tokens in incoming requests
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private final JwtConfig jwtConfig;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromRequest(request);

        if (token != null) {
            processToken(token, request);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX_LENGTH);
        }
        return null;
    }

    private void processToken(String token, HttpServletRequest request) {
        String email = jwtConfig.extractEmail(token);
        if (email != null && isAuthenticationRequired()) {
            authenticateUser(email, token, request);
        }
    }

    private boolean isAuthenticationRequired() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private void authenticateUser(String email, String token, HttpServletRequest request) {
        userService.findByEmail(email).ifPresent(user -> {
            if (jwtConfig.validateToken(token)) {
                setAuthentication(user, request);
            }
        });
    }

    private void setAuthentication(User user, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(user);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
    }
}