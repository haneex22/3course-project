package ru.skfu.carrental.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class RentalAgreementResponse {
    private UUID id;
    private UUID reservationId;
    private String agreementNumber;
    private LocalDateTime signedAt;
    private long initialMileage;
    private int initialFuelLevel;
    private long finalMileage;
    private int finalFuelLevel;
    private boolean active;
    private String carModelName;
    private String clientEmail;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReservationId() { return reservationId; }
    public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }
    public String getAgreementNumber() { return agreementNumber; }
    public void setAgreementNumber(String agreementNumber) { this.agreementNumber = agreementNumber; }
    public LocalDateTime getSignedAt() { return signedAt; }
    public void setSignedAt(LocalDateTime signedAt) { this.signedAt = signedAt; }
    public long getInitialMileage() { return initialMileage; }
    public void setInitialMileage(long initialMileage) { this.initialMileage = initialMileage; }
    public int getInitialFuelLevel() { return initialFuelLevel; }
    public void setInitialFuelLevel(int initialFuelLevel) { this.initialFuelLevel = initialFuelLevel; }
    public long getFinalMileage() { return finalMileage; }
    public void setFinalMileage(long finalMileage) { this.finalMileage = finalMileage; }
    public int getFinalFuelLevel() { return finalFuelLevel; }
    public void setFinalFuelLevel(int finalFuelLevel) { this.finalFuelLevel = finalFuelLevel; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getCarModelName() { return carModelName; }
    public void setCarModelName(String carModelName) { this.carModelName = carModelName; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
}
