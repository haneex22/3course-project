package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.RentalAgreement;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.RentalAgreementRepository;
import ru.skfu.carrental.foundation.ReservationRepository;
import ru.skfu.carrental.foundation.UserRepository;
import ru.skfu.carrental.mediator.ReservationServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceHandoverReturnTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private CarRepository carRepository;
    @Mock private ClientProfileRepository clientProfileRepository;
    @Mock private UserRepository userRepository;
    @Mock private RentalAgreementRepository rentalAgreementRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private UUID reservationId;
    private UUID managerId;
    private Reservation confirmedReservation;
    private Reservation activeReservation;
    private Car car;
    private RentalAgreement existingAgreement;

    @BeforeEach
    void setUp() {
        reservationId = UUID.randomUUID();
        managerId = UUID.randomUUID();

        car = new Car();
        car.setId(UUID.randomUUID());
        car.setModelName("Volkswagen Polo");
        car.setStatus(CarStatus.AVAILABLE);
        car.setBaseDailyRate(new BigDecimal("2500.00"));
        car.setCarClass("ECONOMY");
        car.setLicensePlate("А123БВ26");
        car.setVin("VINTEST123");

        confirmedReservation = new Reservation();
        confirmedReservation.setId(reservationId);
        confirmedReservation.setCar(car);
        confirmedReservation.setStatus(ReservationStatus.CONFIRMED);

        activeReservation = new Reservation();
        activeReservation.setId(reservationId);
        activeReservation.setCar(car);
        activeReservation.setStatus(ReservationStatus.ACTIVE);

        existingAgreement = new RentalAgreement();
        existingAgreement.setId(UUID.randomUUID());
        existingAgreement.setReservation(activeReservation);
        existingAgreement.setAgreementNumber("AG-TEST1234");
        existingAgreement.setInitialMileage(15000);
        existingAgreement.setInitialFuelLevel(90);
        existingAgreement.setActive(true);
    }

    // ---- handoverCar tests ----

    @Test
    void handoverCar_success_createsAgreementAndSetsCarRented() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(confirmedReservation));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rentalAgreementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalAgreement result = reservationService.handoverCar(reservationId, 15000, 90, managerId);

        assertThat(result).isNotNull();
        assertThat(result.getAgreementNumber()).startsWith("AG-");
        assertThat(result.getInitialMileage()).isEqualTo(15000);
        assertThat(result.getInitialFuelLevel()).isEqualTo(90);
        assertThat(result.isActive()).isTrue();
        assertThat(car.getStatus()).isEqualTo(CarStatus.RENTED);
        assertThat(car.getCurrentMileage()).isEqualTo(15000);
        assertThat(car.getFuelLevelPercentage()).isEqualTo(90);

        verify(rentalAgreementRepository).save(any(RentalAgreement.class));
        verify(carRepository).save(car);
    }

    @Test
    void handoverCar_notConfirmed_throwsException() {
        // Reservation is PENDING, not CONFIRMED
        Reservation pending = new Reservation();
        pending.setId(reservationId);
        pending.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> reservationService.handoverCar(reservationId, 10000, 80, managerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("подтверждённое");

        verify(rentalAgreementRepository, never()).save(any());
    }

    @Test
    void handoverCar_wrongStatus_throwsException() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(activeReservation));

        assertThatThrownBy(() -> reservationService.handoverCar(reservationId, 10000, 80, managerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("подтверждённое");
    }

    @Test
    void handoverCar_reservationNotFound_throwsException() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.handoverCar(reservationId, 10000, 80, managerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найдено");
    }

    // ---- returnCar tests ----

    @Test
    void returnCar_success_completesRentalAndSetsCarAvailable() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(activeReservation));
        when(rentalAgreementRepository.findByReservationId(reservationId)).thenReturn(Optional.of(existingAgreement));
        when(rentalAgreementRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RentalAgreement result = reservationService.returnCar(reservationId, 15750, 65, managerId);

        assertThat(result).isNotNull();
        assertThat(result.getFinalMileage()).isEqualTo(15750);
        assertThat(result.getFinalFuelLevel()).isEqualTo(65);
        assertThat(result.isActive()).isFalse();
        assertThat(car.getStatus()).isEqualTo(CarStatus.AVAILABLE);
        assertThat(car.getCurrentMileage()).isEqualTo(15750);
        assertThat(car.getFuelLevelPercentage()).isEqualTo(65);

        verify(rentalAgreementRepository).save(existingAgreement);
        verify(carRepository).save(car);
        verify(reservationRepository).save(activeReservation);
    }

    @Test
    void returnCar_notActive_throwsException() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(confirmedReservation));

        assertThatThrownBy(() -> reservationService.returnCar(reservationId, 16000, 70, managerId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("активную аренду");
    }

    @Test
    void returnCar_noAgreement_throwsException() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(activeReservation));
        when(rentalAgreementRepository.findByReservationId(reservationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.returnCar(reservationId, 16000, 70, managerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Договор аренды не найден");
    }

    @Test
    void returnCar_reservationNotFound_throwsException() {
        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.returnCar(reservationId, 16000, 70, managerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не найдено");
    }
}
