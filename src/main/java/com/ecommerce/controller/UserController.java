package com.ecommerce.controller;

import com.ecommerce.config.JwtConfig;
import com.ecommerce.dto.LoginRequestDTO;
import com.ecommerce.dto.UserDTO;
import com.ecommerce.model.User;
import com.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.util.Map;

import java.net.URI;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.register(userDTO);
        UserDTO responseDTO = convertToDTO(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getName(),
                user.getEmail(),
                null
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<User> user = userService.findByEmail(loginRequest.getEmail());
        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            String token = jwtConfig.generateToken(user.get().getEmail());
            return ResponseEntity.ok().body(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

}
