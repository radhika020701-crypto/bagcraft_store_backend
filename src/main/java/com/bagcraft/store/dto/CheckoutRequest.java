package com.bagcraft.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CheckoutRequest {
    @NotBlank
    private String shippingAddress;
}
