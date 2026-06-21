package ru.skfu.carrental.control;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.skfu.carrental.dto.request.CarCreateRequest;
import ru.skfu.carrental.dto.request.HandoverRequest;
import ru.skfu.carrental.dto.request.ReturnRequest;
import ru.skfu.carrental.dto.response.AdminBookingResponse;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.dto.response.RentalAgreementResponse;
import ru.skfu.carrental.dto.response.UnverifiedClientResponse;
import ru.skfu.carrental.entity.RentalAgreement;
import ru.skfu.carrental.entity.User;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.UserRepository;
import ru.skfu.carrental.mediator.CarService;
import ru.skfu.carrental.mediator.ReservationService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class AdminController {

    private final CarService carService;
    private final ClientProfileRepository clientProfileRepository;
    private final ReservationService reservationService;
    private final UserRepository userRepository;

    public AdminController(CarService carService,
                           ClientProfileRepository clientProfileRepository,
                           ReservationService reservationService,
                           UserRepository userRepository) {
        this.carService = carService;
        this.clientProfileRepository = clientProfileRepository;
        this.reservationService = reservationService;
        this.userRepository = userRepository;
    }

    // ---- Cars ----

    @PostMapping("/cars")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarResponse> addCar(@Valid @RequestBody CarCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(request));
    }

    @GetMapping("/cars")
    public ResponseEntity<List<CarResponse>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @PutMapping("/cars/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarResponse> updateCar(@PathVariable UUID id, @Valid @RequestBody CarCreateRequest request) {
        return ResponseEntity.ok(carService.updateCar(id, request));
    }

    @DeleteMapping("/cars/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable UUID id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Bookings ----

    @GetMapping("/bookings")
    public ResponseEntity<List<AdminBookingResponse>> getAllBookings() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/bookings/{id}")
    public ResponseEntity<AdminBookingResponse> getBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservationByIdForAdmin(id));
    }

    @PostMapping("/bookings/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bookings/{id}/handover")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RentalAgreementResponse> handoverCar(
            @PathVariable UUID id,
            @Valid @RequestBody HandoverRequest request,
            @AuthenticationPrincipal User user) {
        RentalAgreement agreement = reservationService.handoverCar(
                id, request.getInitialMileage(), request.getInitialFuelLevel(), user.getId());
        return ResponseEntity.ok(toAgreementResponse(agreement));
    }

    @PostMapping("/bookings/{id}/return")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<RentalAgreementResponse> returnCar(
            @PathVariable UUID id,
            @Valid @RequestBody ReturnRequest request,
            @AuthenticationPrincipal User user) {
        RentalAgreement agreement = reservationService.returnCar(
                id, request.getFinalMileage(), request.getFinalFuelLevel(), user.getId());
        return ResponseEntity.ok(toAgreementResponse(agreement));
    }

    // ---- Client verification ----

    @GetMapping("/clients/unverified")
    public ResponseEntity<List<UnverifiedClientResponse>> getUnverifiedClients() {
        List<UnverifiedClientResponse> clients = clientProfileRepository.findAllUnverified()
                .stream()
                .map(cp -> {
                    UnverifiedClientResponse dto = new UnverifiedClientResponse();
                    dto.setUserId(cp.getUserId());
                    if (cp.getUser() != null) {
                        dto.setEmail(cp.getUser().getEmail());
                        dto.setRegistrationDate(cp.getUser().getRegistrationDate());
                    }
                    dto.setPassportSeries(cp.getPassportSeries());
                    dto.setPassportNumber(cp.getPassportNumber());
                    dto.setLicenseNumber(cp.getLicenseNumber());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/clients/{userId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> verifyClient(@PathVariable UUID userId) {
        return clientProfileRepository.findById(userId)
                .map(profile -> {
                    profile.setVerified(true);
                    clientProfileRepository.save(profile);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private RentalAgreementResponse toAgreementResponse(RentalAgreement a) {
        RentalAgreementResponse r = new RentalAgreementResponse();
        r.setId(a.getId());
        r.setReservationId(a.getReservation() != null ? a.getReservation().getId() : null);
        r.setAgreementNumber(a.getAgreementNumber());
        r.setSignedAt(a.getSignedAt());
        r.setInitialMileage(a.getInitialMileage());
        r.setInitialFuelLevel(a.getInitialFuelLevel());
        r.setFinalMileage(a.getFinalMileage() != null ? a.getFinalMileage() : 0);
        r.setFinalFuelLevel(a.getFinalFuelLevel() != null ? a.getFinalFuelLevel() : 0);
        r.setActive(a.isActive());
        if (a.getReservation() != null && a.getReservation().getCar() != null) {
            r.setCarModelName(a.getReservation().getCar().getModelName());
        }
        if (a.getReservation() != null) {
            userRepository.findById(a.getReservation().getClientId())
                    .ifPresent(u -> r.setClientEmail(u.getEmail()));
        }
        return r;
    }
}
