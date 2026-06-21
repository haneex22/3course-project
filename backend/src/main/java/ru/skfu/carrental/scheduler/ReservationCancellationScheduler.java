package ru.skfu.carrental.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Component
public class ReservationCancellationScheduler {

    private static final Logger log = Logger.getLogger(ReservationCancellationScheduler.class.getName());
    private static final int PENDING_TIMEOUT_MINUTES = 15;

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;

    public ReservationCancellationScheduler(ReservationRepository reservationRepository,
                                             CarRepository carRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
    }

    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void cancelExpiredPendingReservations() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(PENDING_TIMEOUT_MINUTES);
        List<Reservation> expired = reservationRepository.findPendingReservationsOlderThan(cutoff);

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            if (reservation.getCar() != null) {
                reservation.getCar().setStatus(CarStatus.AVAILABLE);
                carRepository.save(reservation.getCar());
            }
            reservationRepository.save(reservation);
            log.info("Auto-cancelled PENDING reservation: " + reservation.getId());
        }

        if (!expired.isEmpty()) {
            log.info("Auto-cancelled " + expired.size() + " expired reservations");
        }
    }

    @Scheduled(fixedRate = 1_800_000)
    @Transactional
    public void completeExpiredActiveReservations() {
        List<Reservation> expired = reservationRepository.findExpiredActiveReservations(LocalDateTime.now());

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.COMPLETED);
            if (reservation.getCar() != null) {
                reservation.getCar().setStatus(CarStatus.AVAILABLE);
                carRepository.save(reservation.getCar());
            }
            reservationRepository.save(reservation);
            log.info("Auto-completed reservation " + reservation.getId() + ", car returned to AVAILABLE");
        }

        if (!expired.isEmpty()) {
            log.info("Auto-completed " + expired.size() + " finished reservations");
        }
    }
}
