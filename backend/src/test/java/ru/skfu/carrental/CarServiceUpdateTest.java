package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.dto.request.CarCreateRequest;
import ru.skfu.carrental.dto.response.CarResponse;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.exception.CarNotFoundException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;
import ru.skfu.carrental.mediator.CarServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceUpdateTest {

    @Mock
    private CarRepository carRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private CarServiceImpl carService;

    private UUID carId;
    private Car existingCar;

    @BeforeEach
    void setUp() {
        carId = UUID.randomUUID();

        existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setModelName("Volkswagen Polo");
        existingCar.setCarClass("ECONOMY");
        existingCar.setBaseDailyRate(new BigDecimal("2500.00"));
        existingCar.setStatus(CarStatus.AVAILABLE);
        existingCar.setLicensePlate("А123БВ26");
        existingCar.setVin("WVWZZZ1JZXW000001");
        existingCar.setCurrentMileage(25000);
        existingCar.setFuelLevelPercentage(90);
        existingCar.setImageUrl("https://example.com/old.jpg");
    }

    @Test
    void updateCar_success_updatesAllEditableFields() {
        CarCreateRequest request = new CarCreateRequest();
        request.setModelName("Volkswagen Polo New");
        request.setCarClass("BUSINESS");
        request.setBaseDailyRate(new BigDecimal("3500.00"));
        request.setLicensePlate("НовыйНомер");
        request.setVin("NEWVINTEST001");
        request.setImageUrl("https://example.com/new.jpg");

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CarResponse response = carService.updateCar(carId, request);

        assertThat(response.getModelName()).isEqualTo("Volkswagen Polo New");
        assertThat(response.getCarClass()).isEqualTo("BUSINESS");
        assertThat(response.getBaseDailyRate()).isEqualByComparingTo(new BigDecimal("3500.00"));
        assertThat(response.getLicensePlate()).isEqualTo("НовыйНомер");
        assertThat(response.getVin()).isEqualTo("NEWVINTEST001");
        assertThat(response.getImageUrl()).isEqualTo("https://example.com/new.jpg");
        // Non-editable fields should remain unchanged
        assertThat(response.getStatus()).isEqualTo("AVAILABLE");
    }

    @Test
    void updateCar_carNotFound_throwsException() {
        CarCreateRequest request = new CarCreateRequest();
        request.setModelName("Test");
        request.setCarClass("ECONOMY");
        request.setBaseDailyRate(new BigDecimal("3000.00"));
        request.setLicensePlate("ТестНомер");
        request.setVin("TESTVIN001");

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.updateCar(carId, request))
                .isInstanceOf(CarNotFoundException.class);
    }

    @Test
    void updateCar_keepsNonEditableFieldsUnchanged() {
        // Test with minimal request — only updating model name
        CarCreateRequest request = new CarCreateRequest();
        request.setModelName("Updated Model");
        request.setCarClass(existingCar.getCarClass());
        request.setBaseDailyRate(existingCar.getBaseDailyRate());
        request.setLicensePlate(existingCar.getLicensePlate());
        request.setVin(existingCar.getVin());

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CarResponse response = carService.updateCar(carId, request);

        assertThat(response.getModelName()).isEqualTo("Updated Model");
        assertThat(response.getLicensePlate()).isEqualTo("А123БВ26"); // unchanged
        assertThat(response.getStatus()).isEqualTo("AVAILABLE"); // unchanged
    }
}
