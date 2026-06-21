package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.mediator.CarServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private Car availableCar;
    private Car businessCar;

    @BeforeEach
    void setUp() {
        availableCar = new Car();
        availableCar.setId(UUID.randomUUID());
        availableCar.setModelName("Volkswagen Polo");
        availableCar.setCarClass("ECONOMY");
        availableCar.setBaseDailyRate(new BigDecimal("2500.00"));
        availableCar.setStatus(CarStatus.AVAILABLE);
        availableCar.setLicensePlate("А123БВ26");
        availableCar.setVin("WVWZZZ1JZXW000001");

        businessCar = new Car();
        businessCar.setId(UUID.randomUUID());
        businessCar.setModelName("BMW 3 Series");
        businessCar.setCarClass("BUSINESS");
        businessCar.setBaseDailyRate(new BigDecimal("8000.00"));
        businessCar.setStatus(CarStatus.AVAILABLE);
        businessCar.setLicensePlate("И012КЛ26");
        businessCar.setVin("WBAPH71000E000001");
    }

    @Test
    void findAvailableCars_noFilters_returnsAllAvailable() {
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(availableCar, businessCar));

        List<CarResponse> result = carService.findAvailableCars(null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAvailableCars_withClassFilter_returnsFiltered() {
        when(carRepository.findByStatus(CarStatus.AVAILABLE)).thenReturn(List.of(availableCar, businessCar));

        List<CarResponse> result = carService.findAvailableCars("ECONOMY", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCarClass()).isEqualTo("ECONOMY");
    }

    @Test
    void findAvailableCars_withDates_callsAvailableQuery() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(3);
        when(carRepository.findAvailableCars("ECONOMY", start, end)).thenReturn(List.of(availableCar));

        List<CarResponse> result = carService.findAvailableCars("ECONOMY", start, end);

        assertThat(result).hasSize(1);
    }

    @Test
    void getCarById_existingId_returnsCar() {
        when(carRepository.findById(availableCar.getId())).thenReturn(Optional.of(availableCar));

        CarResponse response = carService.getCarById(availableCar.getId());

        assertThat(response.getModelName()).isEqualTo("Volkswagen Polo");
    }

    @Test
    void getCarById_notFound_throwsException() {
        UUID unknownId = UUID.randomUUID();
        when(carRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.getCarById(unknownId))
                .isInstanceOf(CarNotAvailableException.class);
    }
}
