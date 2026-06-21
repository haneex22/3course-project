package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.dto.request.BookingRequest;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.ClientProfile;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.exception.UserNotVerifiedException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ClientProfileRepository;
import ru.skfu.carrental.foundation.ReservationRepository;
import ru.skfu.carrental.mediator.ReservationServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private CarRepository carRepository;
    @Mock private ClientProfileRepository clientProfileRepository;
    @Mock private ru.skfu.carrental.foundation.UserRepository userRepository;
    @Mock private ru.skfu.carrental.foundation.RentalAgreementRepository rentalAgreementRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private UUID clientId;
    private Car car;
    private ClientProfile verifiedProfile;
    private BookingRequest request;

    @BeforeEach
    void setUp() {
        clientId = UUID.fromString("00000000-0000-0000-0000-000000000003");

        car = new Car();
        car.setId(UUID.randomUUID());
        car.setModelName("Volkswagen Polo");
        car.setStatus(CarStatus.AVAILABLE);
        car.setBaseDailyRate(new BigDecimal("2500.00"));
        car.setCarClass("ECONOMY");

        verifiedProfile = new ClientProfile();
        verifiedProfile.setUserId(clientId);
        verifiedProfile.setVerified(true);

        request = new BookingRequest();
        request.setCarId(car.getId());
        request.setStartDateTime(LocalDateTime.now().plusDays(1));
        request.setEndDateTime(LocalDateTime.now().plusDays(3));
    }

    @Test
    void bookCar_success_createsReservation() {
        when(clientProfileRepository.findById(clientId)).thenReturn(Optional.of(verifiedProfile));
        when(reservationRepository.existsConflictingReservation(any(), any(), any())).thenReturn(false);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Reservation result = reservationService.bookCar(clientId, request);

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(result.getClientId()).isEqualTo(clientId);
        // Статус машины НЕ меняется: доступность определяется датами броней,
        // поэтому авто остаётся в каталоге для бронирования на свободные даты
        assertThat(car.getStatus()).isEqualTo(CarStatus.AVAILABLE);
    }

    @Test
    void bookCar_unverifiedClient_throwsUserNotVerifiedException() {
        verifiedProfile.setVerified(false);
        when(clientProfileRepository.findById(clientId)).thenReturn(Optional.of(verifiedProfile));

        assertThatThrownBy(() -> reservationService.bookCar(clientId, request))
                .isInstanceOf(UserNotVerifiedException.class);
    }

    @Test
    void bookCar_maintenanceCar_throwsCarNotAvailableException() {
        car.setStatus(CarStatus.MAINTENANCE);
        when(clientProfileRepository.findById(clientId)).thenReturn(Optional.of(verifiedProfile));
        when(reservationRepository.existsConflictingReservation(any(), any(), any())).thenReturn(false);
        when(carRepository.findById(car.getId())).thenReturn(Optional.of(car));

        assertThatThrownBy(() -> reservationService.bookCar(clientId, request))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("техническом обслуживании");
    }

    @Test
    void bookCar_dateConflict_throwsCarNotAvailableException() {
        when(clientProfileRepository.findById(clientId)).thenReturn(Optional.of(verifiedProfile));
        when(reservationRepository.existsConflictingReservation(any(), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> reservationService.bookCar(clientId, request))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("уже забронирован");
    }
}
