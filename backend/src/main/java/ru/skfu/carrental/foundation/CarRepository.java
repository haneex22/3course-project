package ru.skfu.carrental.foundation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.enums.CarStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findByStatus(CarStatus status);

    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' " +
           "AND (:carClass IS NULL OR c.carClass = :carClass) " +
           "AND c.id NOT IN (" +
           "  SELECT r.car.id FROM Reservation r " +
           "  WHERE r.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "  AND NOT (r.endDateTime <= :startDate OR r.startDateTime >= :endDate)" +
           ")")
    List<Car> findAvailableCars(
            @Param("carClass") String carClass,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
