package ru.skfu.carrental.mediator;

import org.springframework.stereotype.Service;
import ru.skfu.carrental.entity.Reservation;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String processPayment(Reservation reservation) {
        // Mock payment gateway — always returns SUCCESS
        return UUID.randomUUID().toString();
    }
}
