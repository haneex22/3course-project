package ru.skfu.carrental.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByClientIdOrderByCreatedAtDesc(UUID clientId);

    List<Reservation> findByClientIdAndStatusIn(UUID clientId, List<ReservationStatus> statuses);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.car.id = :carId " +
           "AND r.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND NOT (r.endDateTime <= :startDate OR r.startDateTime >= :endDate)")
    boolean existsConflictingReservation(
            @Param("carId") UUID carId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT r FROM Reservation r WHERE r.status = 'PENDING' AND r.createdAt < :cutoffTime")
    List<Reservation> findPendingReservationsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT r FROM Reservation r WHERE r.status IN ('CONFIRMED', 'ACTIVE') AND r.endDateTime < :now")
    List<Reservation> findExpiredActiveReservations(@Param("now") LocalDateTime now);

    @Query("SELECT r FROM Reservation r WHERE r.car.id = :carId " +
           "AND r.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND r.endDateTime >= :now ORDER BY r.startDateTime")
    List<Reservation> findActiveByCarId(@Param("carId") UUID carId, @Param("now") LocalDateTime now);
}
