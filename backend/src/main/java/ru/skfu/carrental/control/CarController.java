package ru.skfu.carrental.control;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.skfu.carrental.dto.request.CarStatusUpdateRequest;
import ru.skfu.carrental.dto.response.BusyPeriodResponse;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.mediator.CarService;
import ru.skfu.carrental.mediator.ReservationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarService carService;
    private final ReservationService reservationService;

    public CarController(CarService carService, ReservationService reservationService) {
        this.carService = carService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<CarResponse>> getAvailableCars(
            @RequestParam(required = false) String carClass,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime endDate) {
        return ResponseEntity.ok(carService.findAvailableCars(carClass, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable UUID id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping("/{id}/busy")
    public ResponseEntity<List<BusyPeriodResponse>> getBusyPeriods(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getBusyPeriods(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<CarResponse> updateCarStatus(@PathVariable UUID id,
                                                        @Valid @RequestBody CarStatusUpdateRequest request) {
        return ResponseEntity.ok(carService.updateCarStatus(id, request));
    }
}
