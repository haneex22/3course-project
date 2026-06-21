package ru.skfu.carrental.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ReservationCancellationScheduler {

    private static final Logger log = Logger.getLogger(ReservationCancellationScheduler.class.getName());

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final int pendingTimeoutMinutes;

    public ReservationCancellationScheduler(ReservationRepository reservationRepository,
                                             CarRepository carRepository,
                                             @Value("${app.scheduler.pending-timeout-minutes}") int pendingTimeoutMinutes) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.pendingTimeoutMinutes = pendingTimeoutMinutes;
    }

    @Scheduled(fixedRateString = "${app.scheduler.pending-check-rate-ms}")
    @Transactional
    public void cancelExpiredPendingReservations() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(pendingTimeoutMinutes);
        List<Reservation> expired = reservationRepository.findPendingReservationsOlderThan(cutoff);

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            if (reservation.getCar() != null) {
                reservation.getCar().setStatus(CarStatus.AVAILABLE);
            }
            log.info("Auto-cancelled PENDING reservation: " + reservation.getId());
        }

        if (!expired.isEmpty()) {
            reservationRepository.saveAll(expired);
            List<Car> carsToUpdate = expired.stream()
                .map(Reservation::getCar)
                .filter(c -> c != null)
                .collect(Collectors.toList());
            carRepository.saveAll(carsToUpdate);
            log.info("Auto-cancelled " + expired.size() + " expired reservations");
        }
    }

    @Scheduled(fixedRateString = "${app.scheduler.active-check-rate-ms}")
    @Transactional
    public void completeExpiredActiveReservations() {
        List<Reservation> expired = reservationRepository.findExpiredActiveReservations(LocalDateTime.now());

        if (!expired.isEmpty()) {
            for (Reservation reservation : expired) {
                reservation.setStatus(ReservationStatus.COMPLETED);
                if (reservation.getCar() != null) {
                    reservation.getCar().setStatus(CarStatus.AVAILABLE);
                }
                log.info("Auto-completed reservation " + reservation.getId() + ", car returned to AVAILABLE");
            }
            reservationRepository.saveAll(expired);
            List<Car> carsToUpdate = expired.stream()
                .map(Reservation::getCar)
                .filter(c -> c != null)
                .collect(Collectors.toList());
            carRepository.saveAll(carsToUpdate);
            log.info("Auto-completed " + expired.size() + " finished reservations");
        }
    }
}
