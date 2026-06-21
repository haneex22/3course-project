package ru.skfu.carrental.entity.state;

import ru.skfu.carrental.entity.Reservation;

public class CancelledState implements ReservationState {

    @Override
    public void confirmPayment(Reservation reservation) {
        throw new IllegalStateException("Cannot perform actions on a cancelled reservation");
    }

    @Override
    public void handoverCar(Reservation reservation) {
        throw new IllegalStateException("Cannot perform actions on a cancelled reservation");
    }

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Reservation is already cancelled");
    }

    @Override
    public String getStatusName() {
        return "CANCELLED";
    }
}
