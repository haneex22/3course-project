package ru.skfu.carrental;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.skfu.carrental.dto.response.ErrorResponse;
import ru.skfu.carrental.exception.*;
import ru.skfu.carrental.exception.GlobalExceptionHandler;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleCarNotAvailable_returnsConflict() {
        var ex = new CarNotAvailableException("Car is not available");

        ResponseEntity<ErrorResponse> response = handler.handleCarNotAvailable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Car is not available");
    }

    @Test
    void handleUserNotVerified_returnsForbidden() {
        var ex = new UserNotVerifiedException("Not verified");

        ResponseEntity<ErrorResponse> response = handler.handleUserNotVerified(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void handleGatewayError_returnsBadGateway() {
        var ex = new ExternalGatewayException("Gateway error");

        ResponseEntity<ErrorResponse> response = handler.handleGatewayError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void handleEmailExists_returnsConflict() {
        var ex = new EmailAlreadyExistsException("Email taken");

        ResponseEntity<ErrorResponse> response = handler.handleEmailExists(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handleIllegalArgument_returnsBadRequest() {
        var ex = new IllegalArgumentException("Invalid input");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void handleRuntime_returnsInternalServerError() {
        var ex = new RuntimeException("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleRuntime(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleDataIntegrity_licensePlateConflict_returnsSpecificMessage() {
        var ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"uk_cars_license_plate\"");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("гос. номером");
    }

    @Test
    void handleDataIntegrity_vinConflict_returnsSpecificMessage() {
        var ex = new DataIntegrityViolationException(
                "ERROR: duplicate key value violates unique constraint \"uk_cars_vin\"");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrity(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("VIN");
    }
}
