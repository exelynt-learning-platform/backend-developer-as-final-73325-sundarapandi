package com.example.booking.repository;

import com.example.booking.entity.Reservation;
import com.example.booking.entity.ReservationStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
        select r from Reservation r
        where (:username is null or r.user.username = :username)
          and (:status is null or r.status = :status)
          and (:minPrice is null or r.price >= :minPrice)
          and (:maxPrice is null or r.price <= :maxPrice)
        """)
    Page<Reservation> search(
            @Param("username") String username,
            @Param("status") ReservationStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);

    @Query("""
        select count(r) > 0 from Reservation r
        where r.resource.id = :resourceId
          and r.status <> com.example.booking.entity.ReservationStatus.CANCELLED
          and r.startTime < :endTime
          and r.endTime > :startTime
          and (:excludeId is null or r.id <> :excludeId)
        """)
    boolean existsOverlapping(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId);
}
