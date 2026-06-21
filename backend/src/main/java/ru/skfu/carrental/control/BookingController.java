package ru.skfu.carrental.control;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.User;
import ru.skfu.carrental.mediator.BookingFacade;
import ru.skfu.carrental.mediator.ReservationService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingFacade bookingFacade;
    private final ReservationService reservationService;

    public BookingController(BookingFacade bookingFacade, ReservationService reservationService) {
        this.bookingFacade = bookingFacade;
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createBooking(@Valid @RequestBody BookingRequest request,
                                                              @AuthenticationPrincipal User user) {
        ReservationResponse response = bookingFacade.processCompleteBooking(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationResponse>> getMyBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reservationService.getClientReservations(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getBookingById(@PathVariable UUID id,
                                                               @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reservationService.getReservationById(id, user.getId()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id,
                                              @AuthenticationPrincipal User user) {
        reservationService.cancelReservation(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
