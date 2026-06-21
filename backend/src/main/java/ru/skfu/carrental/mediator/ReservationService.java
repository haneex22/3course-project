package ru.skfu.carrental.mediator;

import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.dto.response.BusyPeriodResponse;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.Reservation;

import java.util.List;
import java.util.UUID;

import ru.skfu.carrental.dto.response.AdminBookingResponse;
import ru.skfu.carrental.entity.RentalAgreement;

public interface ReservationService {
    Reservation bookCar(UUID clientId, BookingRequest request);
    List<ReservationResponse> getClientReservations(UUID clientId);
    ReservationResponse getReservationById(UUID id, UUID clientId);
    void cancelReservation(UUID reservationId);
    void cancelReservation(UUID reservationId, UUID clientId);
    List<BusyPeriodResponse> getBusyPeriods(UUID carId);
    ReservationResponse toResponse(Reservation r);

    // Admin methods
    List<AdminBookingResponse> getAllReservations();
    AdminBookingResponse getReservationByIdForAdmin(UUID id);

    // Car handover & return (UC-007, UC-008)
    RentalAgreement handoverCar(UUID reservationId, long initialMileage, int initialFuelLevel, UUID managerId);
    RentalAgreement returnCar(UUID reservationId, long finalMileage, int finalFuelLevel, UUID managerId);
}
