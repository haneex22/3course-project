package ru.skfu.carrental.mediator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.util.UUID;

@Service
public class BookingFacade {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    private final ReservationRepository reservationRepository;

    public BookingFacade(ReservationService reservationService,
                          PaymentService paymentService,
                          NotificationService notificationService,
                          ReservationRepository reservationRepository) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationResponse processCompleteBooking(UUID clientId, BookingRequest request) {
        // 1. Validate rules and create PENDING reservation
        Reservation reservation = reservationService.bookCar(clientId, request);

        // 2. Simulate payment
        String transactionId = paymentService.processPayment(reservation);

        // 3. Confirm reservation via State pattern
        reservation.getState().confirmPayment(reservation);
        reservation = reservationRepository.save(reservation);

        // 4. Async notification
        notificationService.sendBookingConfirmationAsync(clientId, reservation.getId());

        return ((ReservationServiceImpl) reservationService).toResponse(reservation);
    }
}
