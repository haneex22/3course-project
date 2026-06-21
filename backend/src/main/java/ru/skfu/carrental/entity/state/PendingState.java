package ru.skfu.carrental.entity.state;

import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;

public class PendingState implements ReservationState {

    @Override
    public void confirmPayment(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CONFIRMED);
    }

    @Override
    public void handoverCar(Reservation reservation) {
        throw new IllegalStateException("Cannot handover car: reservation is not confirmed yet");
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public void completeRental(Reservation reservation) {
        throw new IllegalStateException("Cannot complete rental: reservation is not active");
    }

    @Override
    public String getStatusName() {
        return "PENDING";
    }
}
