package ru.skfu.carrental.entity.state;

import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;

public class ConfirmedState implements ReservationState {

    @Override
    public void confirmPayment(Reservation reservation) {
        throw new IllegalStateException("Payment already confirmed");
    }

    @Override
    public void handoverCar(Reservation reservation) {
        reservation.setStatus(ReservationStatus.ACTIVE);
    }

    @Override
    public void cancel(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Override
    public String getStatusName() {
        return "CONFIRMED";
    }
}
