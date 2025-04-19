package com.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO for handling login requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {
    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}