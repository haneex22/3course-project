package ru.skfu.carrental;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.skfu.carrental.entity.Reservation;
import ru.skfu.carrental.entity.enums.ReservationStatus;
import ru.skfu.carrental.entity.state.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationStateTest {

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setAmount(new BigDecimal("5000.00"));
        reservation.setCurrency("RUB");
        reservation.setStartDateTime(LocalDateTime.now().plusDays(1));
        reservation.setEndDateTime(LocalDateTime.now().plusDays(3));
        reservation.setStatus(ReservationStatus.PENDING);
    }

    @Test
    void pendingState_confirmPayment_transitionsToConfirmed() {
        new PendingState().confirmPayment(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void pendingState_cancel_transitionsToCancelled() {
        new PendingState().cancel(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void pendingState_handoverCar_throwsIllegalState() {
        assertThatThrownBy(() -> new PendingState().handoverCar(reservation))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void confirmedState_handoverCar_transitionsToActive() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        new ConfirmedState().handoverCar(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.ACTIVE);
    }

    @Test
    void confirmedState_cancel_transitionsToCancelled() {
        reservation.setStatus(ReservationStatus.CONFIRMED);
        new ConfirmedState().cancel(reservation);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void activeState_cancel_throwsIllegalState() {
        reservation.setStatus(ReservationStatus.ACTIVE);
        assertThatThrownBy(() -> new ActiveState().cancel(reservation))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelledState_anyAction_throwsIllegalState() {
        reservation.setStatus(ReservationStatus.CANCELLED);
        assertThatThrownBy(() -> new CancelledState().cancel(reservation))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reservationGetState_returnsCorrectStateForEachStatus() {
        reservation.setStatus(ReservationStatus.PENDING);
        assertThat(reservation.getState()).isInstanceOf(PendingState.class);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        assertThat(reservation.getState()).isInstanceOf(ConfirmedState.class);

        reservation.setStatus(ReservationStatus.ACTIVE);
        assertThat(reservation.getState()).isInstanceOf(ActiveState.class);

        reservation.setStatus(ReservationStatus.CANCELLED);
        assertThat(reservation.getState()).isInstanceOf(CancelledState.class);
    }
}
