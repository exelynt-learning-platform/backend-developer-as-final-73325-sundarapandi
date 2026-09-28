package com.example.booking.dto;

import java.math.BigDecimal;

public record ResourceResponse(
        Long id, String name, String description, String type,
        BigDecimal price, boolean available
) {}
