package ru.skfu.carrental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skfu.carrental.dto.response.ReservationResponse;
import ru.skfu.carrental.entity.Car;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.CarStatus;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.exception.CarNotAvailableException;
import ru.skfu.carrental.foundation.CarRepository;
import ru.skfu.carrental.foundation.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceExtendedTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private ru.skfu.carrental.foundation.ClientProfileRepository clientProfileRepository;

    @InjectMocks
    private ru.skfu.carrental.mediator.ReservationServiceImpl reservationService;

    @Test
    void getClientReservations_returnsOrderedList() {
        UUID clientId = UUID.randomUUID();
        Car car = new Car();
        car.setModelName("Test Car");
        car.setLicensePlate("А123");

        Reservation r1 = new Reservation();
        r1.setId(UUID.randomUUID());
        r1.setClientId(clientId);
        r1.setCar(car);
        r1.setStatus(ReservationStatus.PENDING);
        r1.setStartDateTime(LocalDateTime.now().plusDays(1));
        r1.setEndDateTime(LocalDateTime.now().plusDays(3));

        when(reservationRepository.findByClientIdOrderByCreatedAtDesc(clientId))
                .thenReturn(List.of(r1));

        List<ReservationResponse> result = reservationService.getClientReservations(clientId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCarModelName()).isEqualTo("Test Car");
        assertThat(result.get(0).getStatus()).isEqualTo("PENDING");
    }

    @Test
    void cancelReservation_otherClient_throwsException() {
        UUID clientId = UUID.randomUUID();
        UUID otherClientId = UUID.randomUUID();

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setClientId(otherClientId);
        r.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(r.getId())).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.cancelReservation(r.getId(), clientId))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("не принадлежит");
    }

    @Test
    void cancelReservation_alreadyCancelled_throwsException() {
        UUID clientId = UUID.randomUUID();

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setClientId(clientId);
        r.setStatus(ReservationStatus.CANCELLED);

        when(reservationRepository.findById(r.getId())).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.cancelReservation(r.getId(), clientId))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("нельзя отменить");
    }

    @Test
    void cancelReservation_alreadyCompleted_throwsException() {
        UUID clientId = UUID.randomUUID();

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setClientId(clientId);
        r.setStatus(ReservationStatus.COMPLETED);

        when(reservationRepository.findById(r.getId())).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> reservationService.cancelReservation(r.getId(), clientId))
                .isInstanceOf(CarNotAvailableException.class)
                .hasMessageContaining("нельзя отменить");
    }

    @Test
    void cancelReservation_internal_success() {
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setStatus(ReservationStatus.PENDING);
        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setStatus(CarStatus.AVAILABLE);
        r.setCar(car);

        when(reservationRepository.findById(r.getId())).thenReturn(Optional.of(r));
        when(carRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        reservationService.cancelReservation(r.getId());

        assertThat(r.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        verify(reservationRepository).save(r);
    }

    @Test
    void getBusyPeriods_returnsActiveReservations() {
        UUID carId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setStartDateTime(now.plusDays(1));
        r.setEndDateTime(now.plusDays(3));

        when(reservationRepository.findActiveByCarId(eq(carId), any())).thenReturn(List.of(r));

        var result = reservationService.getBusyPeriods(carId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStartDateTime()).isEqualTo(now.plusDays(1));
    }
}
