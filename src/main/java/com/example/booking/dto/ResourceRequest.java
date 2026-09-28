package com.example.booking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ResourceRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 120) String name,

        @Size(max = 1000) String description,

        @NotBlank(message = "Type is required")
        @Size(max = 60) String type,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
        BigDecimal price,

        boolean available
) {}
