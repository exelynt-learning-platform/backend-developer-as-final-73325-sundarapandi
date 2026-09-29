package com.example.booking.dto;

import com.example.booking.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationAdminUpdateRequest(
        @NotNull(message = "Resource ID is required")
        Long resourceId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull(message = "End time is required")
        LocalDateTime endTime,

        @NotNull(message = "Status is required")
        ReservationStatus status
) {}
