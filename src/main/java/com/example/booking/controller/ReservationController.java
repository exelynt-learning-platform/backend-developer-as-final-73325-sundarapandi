package com.example.booking.controller;

import com.example.booking.dto.*;
import com.example.booking.entity.ReservationStatus;
import com.example.booking.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations")
public class ReservationController {
    private final ReservationService service;

    @GetMapping
    public Page<ReservationResponse> search(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            Authentication authentication) {

        boolean admin = isAdmin(authentication);
        String username = admin ? null : authentication.getName();
        return service.search(username, status, minPrice, maxPrice, page, size, sort);
    }

    @GetMapping("/{id}")
    public ReservationResponse findById(@PathVariable Long id, Authentication authentication) {
        return service.findById(id, authentication.getName(), isAdmin(authentication));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationRequest request,
                                      Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    public ReservationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ReservationAdminUpdateRequest request,
            Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "USER must use the user update endpoint");
        }
        return service.adminUpdate(id, request);
    }

    @PatchMapping("/{id}")
    public ReservationResponse userUpdate(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUserUpdateRequest request,
            Authentication authentication) {
        return service.userUpdate(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.delete(id, authentication.getName(), isAdmin(authentication));
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }
}
