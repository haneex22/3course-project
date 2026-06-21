package ru.skfu.carrental.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class CarResponse {
    private UUID id;
    private String modelName;
    private String carClass;
    private BigDecimal baseDailyRate;
    private String status;
    private String imageUrl;
    private String licensePlate;
    private String vin;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getCarClass() { return carClass; }
    public void setCarClass(String carClass) { this.carClass = carClass; }
    public BigDecimal getBaseDailyRate() { return baseDailyRate; }
    public void setBaseDailyRate(BigDecimal baseDailyRate) { this.baseDailyRate = baseDailyRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
}
