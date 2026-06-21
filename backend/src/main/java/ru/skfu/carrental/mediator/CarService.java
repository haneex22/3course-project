package ru.skfu.carrental.mediator;

import ru.skfu.carrental.dto.request.CarCreateRequest;
import ru.skfu.carrental.dto.request.CarStatusUpdateRequest;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.entity.Car;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CarService {
    List<CarResponse> findAvailableCars(String carClass, LocalDateTime startDate, LocalDateTime endDate);
    CarResponse getCarById(UUID id);
    CarResponse updateCarStatus(UUID id, CarStatusUpdateRequest request);
    CarResponse createCar(CarCreateRequest request);
    List<CarResponse> getAllCars();
    Car getCarEntityById(UUID id);
}
