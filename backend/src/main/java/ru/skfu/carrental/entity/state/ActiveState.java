package ru.skfu.carrental.entity.state;

import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;

public class ActiveState implements ReservationState {

    @Override
    public void confirmPayment(Reservation reservation) {
        throw new IllegalStateException("Cannot confirm payment: car is already handed over");
    }

    @Override
    public void handoverCar(Reservation reservation) {
        throw new IllegalStateException("Car is already handed over");
    }

    @Override
    public void cancel(Reservation reservation) {
        throw new IllegalStateException("Cannot cancel an active rental");
    }

    public void completRental(Reservation reservation) {
        reservation.setStatus(ReservationStatus.COMPLETED);
    }

    @Override
    public String getStatusName() {
        return "ACTIVE";
    }
}
