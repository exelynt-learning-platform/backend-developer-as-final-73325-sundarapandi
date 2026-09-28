package com.example.booking.service;

import com.example.booking.dto.*;
import com.example.booking.entity.*;
import com.example.booking.exception.*;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {
    private final ReservationRepository repository;
    private final ResourceService resourceService;
    private final CustomUserDetailsService userService;

    @Transactional(readOnly = true)
    public Page<ReservationResponse> search(
            String username, ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice,
            int page, int size, String sort) {

        if (page < 0) throw new BadRequestException("page must be >= 0");
        if (size < 1 || size > 100) throw new BadRequestException("size must be between 1 and 100");
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0)
            throw new BadRequestException("minPrice cannot be greater than maxPrice");

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return repository.search(username, status, minPrice, maxPrice, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ReservationResponse findById(Long id, String username, boolean admin) {
        Reservation r = get(id);
        checkOwnership(r, username, admin);
        return toResponse(r);
    }

    public ReservationResponse create(ReservationRequest request, String username) {
        Resource resource = resourceService.get(request.resourceId());
        User user = userService.getDomainUser(username);
        validateTimes(request.startTime(), request.endTime());
        ensureAvailable(resource, request.startTime(), request.endTime(), null);
        Reservation r = Reservation.builder()
                .resource(resource).user(user)
                .startTime(request.startTime()).endTime(request.endTime())
                .price(request.price()).status(ReservationStatus.PENDING)
                .createdAt(LocalDateTime.now()).build();
        return toResponse(repository.save(r));
    }

    public ReservationResponse adminUpdate(Long id, ReservationAdminUpdateRequest request) {
        Reservation r = get(id);
        Resource resource = resourceService.get(request.resourceId());
        validateTimes(request.startTime(), request.endTime());
        ensureAvailable(resource, request.startTime(), request.endTime(), id);
        r.setResource(resource);
        r.setStartTime(request.startTime());
        r.setEndTime(request.endTime());
        r.setPrice(request.price());
        r.setStatus(request.status());
        return toResponse(repository.save(r));
    }

    public ReservationResponse userUpdate(Long id, ReservationUserUpdateRequest request, String username) {
        Reservation r = get(id);
        checkOwnership(r, username, false);
        Resource resource = resourceService.get(request.resourceId());
        validateTimes(request.startTime(), request.endTime());
        ensureAvailable(resource, request.startTime(), request.endTime(), id);
        r.setResource(resource);
        r.setStartTime(request.startTime());
        r.setEndTime(request.endTime());
        r.setPrice(request.price());
        return toResponse(repository.save(r));
    }

    public void delete(Long id, String username, boolean admin) {
        Reservation r = get(id);
        checkOwnership(r, username, admin);
        repository.delete(r);
    }

    private Reservation get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found: " + id));
    }

    private void checkOwnership(Reservation r, String username, boolean admin) {
        if (!admin && !r.getUser().getUsername().equals(username))
            throw new AccessDeniedException("You are not allowed to access this reservation");
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end))
            throw new BadRequestException("startTime must be before endTime");
    }

    private void ensureAvailable(Resource resource, LocalDateTime start, LocalDateTime end, Long excludeId) {
        if (!resource.isAvailable())
            throw new BadRequestException("Resource is not available");
        if (repository.existsOverlapping(resource.getId(), start, end, excludeId))
            throw new ConflictException("Resource is already reserved during the requested time");
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.ASC, "id");
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        String direction = parts.length > 1 ? parts[1].trim() : "asc";
        var allowed = java.util.Set.of("id", "price", "startTime", "endTime", "createdAt", "status");
        if (!allowed.contains(field))
            throw new BadRequestException("Invalid sort field: " + field);
        return Sort.by("desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, field);
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getId(), r.getResource().getId(), r.getResource().getName(),
                r.getUser().getId(), r.getUser().getUsername(),
                r.getStartTime(), r.getEndTime(), r.getPrice(),
                r.getStatus(), r.getCreatedAt());
    }
}
