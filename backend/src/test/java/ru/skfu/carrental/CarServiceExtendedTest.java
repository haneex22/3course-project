package ru.skfu.carrental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;
import ru.skfu.carrental.mediator.CarServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceExtendedTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void updateCarStatus_toMaintenance_returnsUpdatedCar() {
        UUID carId = UUID.randomUUID();
        Car car = new Car();
        car.setId(carId);
        car.setStatus(CarStatus.AVAILABLE);
        car.setModelName("Test Car");
        car.setBaseDailyRate(new BigDecimal("2500.00"));
        car.setLicensePlate("А123БВ");
        car.setVin("VIN123");

        var request = new ru.skfu.carrental.dto.request.CarStatusUpdateRequest();
        request.setStatus("MAINTENANCE");

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var response = carService.updateCarStatus(carId, request);

        assertThat(response.getStatus()).isEqualTo("MAINTENANCE");
        verify(carRepository).save(argThat(c -> c.getStatus() == CarStatus.MAINTENANCE));
    }

    @Test
    void getAllCars_returnsAllCars() {
        when(carRepository.findAll()).thenReturn(java.util.List.of());

        var result = carService.getAllCars();

        assertThat(result).isEmpty();
        verify(carRepository).findAll();
    }

    @Test
    void createCar_success_returnsCreatedCar() {
        var request = new ru.skfu.carrental.dto.request.CarCreateRequest();
        request.setModelName("New Car");
        request.setCarClass("ECONOMY");
        request.setBaseDailyRate(new BigDecimal("3000.00"));
        request.setLicensePlate("НовыйНомер");
        request.setVin("NEWVIN123");

        when(carRepository.save(any())).thenAnswer(inv -> {
            Car saved = inv.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        var response = carService.createCar(request);

        assertThat(response.getModelName()).isEqualTo("New Car");
        assertThat(response.getStatus()).isEqualTo("AVAILABLE");
        verify(carRepository).save(any());
    }
}
