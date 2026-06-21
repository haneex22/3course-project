package ru.skfu.carrental.entity.state;

import ru.skfu.carrental.entity.Reservation;

public interface ReservationState {
    void confirmPayment(Reservation reservation);
    void handoverCar(Reservation reservation);
    void cancel(Reservation reservation);
    void completeRental(Reservation reservation);
    String getStatusName();
}
