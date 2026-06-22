package ru.skfu.carrental.mediator;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = Logger.getLogger(NotificationServiceImpl.class.getName());

    @Override
    @Async
    public void sendBookingConfirmationAsync(UUID clientId, UUID reservationId) {
        // Stub: in production, send push notification or email
        LOG.info("Booking confirmation sent to client " + clientId + " for reservation " + reservationId);
    }
}
