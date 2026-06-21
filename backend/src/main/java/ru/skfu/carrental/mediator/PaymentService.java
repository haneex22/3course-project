package ru.skfu.carrental.mediator;

import ru.skfu.carrental.entity.Reservation;

public interface PaymentService {
    String processPayment(Reservation reservation);
}
