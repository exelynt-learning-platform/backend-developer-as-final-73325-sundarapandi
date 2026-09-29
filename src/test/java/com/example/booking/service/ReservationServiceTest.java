package com.example.booking.service;

import com.example.booking.dto.ReservationRequest;
import com.example.booking.entity.Resource;
import com.example.booking.entity.User;
import com.example.booking.exception.BadRequestException;
import com.example.booking.repository.ReservationRepository;
import com.example.booking.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository repository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private CustomUserDetailsService userService;

    @InjectMocks
    private ReservationService service;

    @Test
    void createUsesResourcePriceInsteadOfClientSuppliedPrice() {
        Resource resource = Resource.builder()
                .id(1L)
                .name("Meeting Room")
                .type("ROOM")
                .price(new BigDecimal("500.00"))
                .available(true)
                .build();

        User user = User.builder()
                .id(2L)
                .username("user")
                .build();

        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.plusHours(2);

        when(resourceService.get(1L)).thenReturn(resource);
        when(userService.getDomainUser("user")).thenReturn(user);
        when(repository.existsOverlapping(eq(1L), eq(start), eq(end), isNull())).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(
                new ReservationRequest(1L, start, end),
                "user"
        );

        assertEquals(new BigDecimal("500.00"), response.price());
        verify(repository).save(argThat(r ->
                r.getPrice().compareTo(new BigDecimal("500.00")) == 0
        ));
    }

    @Test
    void createRejectsStartTimeInThePast() {
        Resource resource = Resource.builder()
                .id(1L)
                .name("Meeting Room")
                .type("ROOM")
                .price(new BigDecimal("500.00"))
                .available(true)
                .build();

        when(resourceService.get(1L)).thenReturn(resource);
        when(userService.getDomainUser("user")).thenReturn(
                User.builder().username("user").build()
        );

        LocalDateTime start = LocalDateTime.now().minusMinutes(5);
        LocalDateTime end = LocalDateTime.now().plusHours(1);

        assertThrows(BadRequestException.class, () ->
                service.create(new ReservationRequest(1L, start, end), "user")
        );

        verify(repository, never()).save(any());
    }

    @Test
    void createRejectsEndTimeBeforeOrEqualToStartTime() {
        Resource resource = Resource.builder()
                .id(1L)
                .name("Meeting Room")
                .type("ROOM")
                .price(new BigDecimal("500.00"))
                .available(true)
                .build();

        when(resourceService.get(1L)).thenReturn(resource);
        when(userService.getDomainUser("user")).thenReturn(
                User.builder().username("user").build()
        );

        LocalDateTime start = LocalDateTime.now().plusHours(2);
        LocalDateTime end = start.minusMinutes(1);

        assertThrows(BadRequestException.class, () ->
                service.create(new ReservationRequest(1L, start, end), "user")
        );

        verify(repository, never()).save(any());
    }
}
