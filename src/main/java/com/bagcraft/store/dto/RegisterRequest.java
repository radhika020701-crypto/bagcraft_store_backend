package com.bagcraft.store.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {
    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    private String fullName;

    private String phone;
    private String address;
}
