package ru.skfu.carrental.mediator;

import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.dto.response.BusyPeriodResponse;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.Reservation;

import java.util.List;
import java.util.UUID;

public interface ReservationService {
    Reservation bookCar(UUID clientId, BookingRequest request);
    List<ReservationResponse> getClientReservations(UUID clientId);
    ReservationResponse getReservationById(UUID id, UUID clientId);
    void cancelReservation(UUID reservationId);
    void cancelReservation(UUID reservationId, UUID clientId);
    List<BusyPeriodResponse> getBusyPeriods(UUID carId);
}
