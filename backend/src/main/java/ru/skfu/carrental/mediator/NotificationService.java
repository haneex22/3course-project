package ru.skfu.carrental.mediator;

import java.util.UUID;

public interface NotificationService {
    void sendBookingConfirmationAsync(UUID clientId, UUID reservationId);
}
