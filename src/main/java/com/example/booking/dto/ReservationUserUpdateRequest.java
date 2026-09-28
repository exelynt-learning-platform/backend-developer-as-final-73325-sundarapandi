package com.example.booking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationUserUpdateRequest(
        @NotNull Long resourceId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 10, fraction = 2) BigDecimal price
) {}
