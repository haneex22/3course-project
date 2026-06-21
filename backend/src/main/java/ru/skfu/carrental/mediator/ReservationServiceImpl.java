package ru.skfu.carrental.mediator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.dto.response.BusyPeriodResponse;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.ClientProfile;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.exception.UserNotVerifiedException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final CarRepository carRepository;
    private final ClientProfileRepository clientProfileRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                   CarRepository carRepository,
                                   ClientProfileRepository clientProfileRepository) {
        this.reservationRepository = reservationRepository;
        this.carRepository = carRepository;
        this.clientProfileRepository = clientProfileRepository;
    }

    @Override
    public Reservation bookCar(UUID clientId, BookingRequest request) {
        clientProfileRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        if (request.getEndDateTime().isBefore(request.getStartDateTime()) ||
                request.getEndDateTime().isEqual(request.getStartDateTime())) {
            throw new CarNotAvailableException("Дата окончания должна быть позже даты начала");
        }
        if (request.getStartDateTime().toLocalDate().isBefore(LocalDate.now())) {
            throw new CarNotAvailableException("Дата начала не может быть в прошлом");
        }

        boolean conflict = reservationRepository.existsConflictingReservation(
                request.getCarId(), request.getStartDateTime(), request.getEndDateTime()
        );
        if (conflict) {
            throw new CarNotAvailableException("Автомобиль уже забронирован на выбранные даты");
        }

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new CarNotAvailableException("Автомобиль не найден"));

        if (car.getStatus() == CarStatus.MAINTENANCE) {
            throw new CarNotAvailableException("Автомобиль на техническом обслуживании");
        }

        PricingStrategy strategy = selectStrategy(request);
        BigDecimal amount = strategy.calculate(car.getBaseDailyRate(), request.getStartDateTime(), request.getEndDateTime());

        Reservation reservation = new Reservation();
        reservation.setClientId(clientId);
        reservation.setCar(car);
        reservation.setStartDateTime(request.getStartDateTime());
        reservation.setEndDateTime(request.getEndDateTime());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setAmount(amount);
        reservation.setCurrency("RUB");

        // Статус машины НЕ меняем: доступность определяется датами броней,
        // поэтому авто остаётся в каталоге и его можно бронировать на свободные даты.
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getClientReservations(UUID clientId) {
        return reservationRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(UUID id, UUID clientId) {
        Reservation r = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        if (!r.getClientId().equals(clientId)) {
            throw new RuntimeException("Access denied");
        }
        return toResponse(r);
    }

    @Override
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        doCancel(reservation);
    }

    @Override
    public void cancelReservation(UUID reservationId, UUID clientId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));
        if (!reservation.getClientId().equals(clientId)) {
            throw new CarNotAvailableException("Это бронирование вам не принадлежит");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new CarNotAvailableException("Это бронирование нельзя отменить");
        }
        doCancel(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusyPeriodResponse> getBusyPeriods(UUID carId) {
        return reservationRepository.findActiveByCarId(carId, java.time.LocalDateTime.now())
                .stream()
                .map(r -> new BusyPeriodResponse(r.getStartDateTime(), r.getEndDateTime()))
                .collect(Collectors.toList());
    }

    private void doCancel(Reservation reservation) {
        reservation.getState().cancel(reservation);
        if (reservation.getCar() != null) {
            reservation.getCar().setStatus(CarStatus.AVAILABLE);
            carRepository.save(reservation.getCar());
        }
        reservationRepository.save(reservation);
    }

    private PricingStrategy selectStrategy(BookingRequest request) {
        return WeekendPricingStrategy.hasWeekend(request.getStartDateTime(), request.getEndDateTime())
                ? new WeekendPricingStrategy()
                : new StandardPricingStrategy();
    }

    public ReservationResponse toResponse(Reservation r) {
        ReservationResponse resp = new ReservationResponse();
        resp.setId(r.getId());
        resp.setCarModelName(r.getCar() != null ? r.getCar().getModelName() : "");
        resp.setCarId(r.getCar() != null ? r.getCar().getId() : null);
        resp.setStartDateTime(r.getStartDateTime());
        resp.setEndDateTime(r.getEndDateTime());
        resp.setStatus(r.getStatus().name());
        resp.setAmount(r.getAmount());
        resp.setCurrency(r.getCurrency());
        resp.setCreatedAt(r.getCreatedAt());
        return resp;
    }
}
