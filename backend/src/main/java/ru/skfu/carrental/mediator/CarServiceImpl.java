package ru.skfu.carrental.mediator;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skfu.carrental.dto.request.CarCreateRequest;
import ru.skfu.carrental.dto.request.CarStatusUpdateRequest;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    public CarServiceImpl(CarRepository carRepository, ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> findAvailableCars(String carClass, LocalDateTime startDate, LocalDateTime endDate) {
        List<Car> cars;
        if (startDate != null && endDate != null) {
            cars = carRepository.findAvailableCars(carClass, startDate, endDate);
        } else {
            cars = carClass != null
                    ? carRepository.findByStatus(CarStatus.AVAILABLE).stream()
                        .filter(c -> c.getCarClass().equalsIgnoreCase(carClass))
                        .collect(Collectors.toList())
                    : carRepository.findByStatus(CarStatus.AVAILABLE);
        }
        return cars.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CarResponse getCarById(UUID id) {
        Car car = getCarEntityById(id);
        return toResponse(car);
    }

    @Override
    public Car getCarEntityById(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new CarNotAvailableException("Car not found: " + id));
    }

    @Override
    public CarResponse updateCarStatus(UUID id, CarStatusUpdateRequest request) {
        Car car = getCarEntityById(id);
        car.setStatus(CarStatus.valueOf(request.getStatus()));
        return toResponse(carRepository.save(car));
    }

    @Override
    public CarResponse createCar(CarCreateRequest request) {
        Car car = new Car();
        applyCarRequest(car, request);
        car.setStatus(CarStatus.AVAILABLE);
        return toResponse(carRepository.save(car));
    }

    @Override
    public CarResponse updateCar(UUID id, CarCreateRequest request) {
        Car car = getCarEntityById(id);
        applyCarRequest(car, request);
        return toResponse(carRepository.save(car));
    }

    private void applyCarRequest(Car car, CarCreateRequest request) {
        car.setVin(request.getVin());
        car.setLicensePlate(request.getLicensePlate());
        car.setModelName(request.getModelName());
        car.setCarClass(request.getCarClass());
        car.setBaseDailyRate(request.getBaseDailyRate());
        car.setImageUrl(request.getImageUrl());
    }

    @Override
    public void deleteCar(UUID id) {
        Car car = getCarEntityById(id);
        // Удаляем все бронирования, связанные с авто (FK constraint не даст удалить иначе)
        reservationRepository.deleteByCarId(id);
        carRepository.delete(car);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private CarResponse toResponse(Car car) {
        CarResponse r = new CarResponse();
        r.setId(car.getId());
        r.setModelName(car.getModelName());
        r.setCarClass(car.getCarClass());
        r.setBaseDailyRate(car.getBaseDailyRate());
        r.setStatus(car.getStatus().name());
        r.setImageUrl(car.getImageUrl());
        r.setLicensePlate(car.getLicensePlate());
        r.setVin(car.getVin());
        return r;
    }
}
