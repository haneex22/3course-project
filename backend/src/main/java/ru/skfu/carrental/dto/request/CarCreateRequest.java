package ru.skfu.carrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CarCreateRequest {
    @NotBlank
    private String vin;
    @NotBlank
    private String licensePlate;
    @NotBlank
    private String modelName;
    @NotBlank
    private String carClass;
    @NotNull @Positive
    private BigDecimal baseDailyRate;
    private String imageUrl;

    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getCarClass() { return carClass; }
    public void setCarClass(String carClass) { this.carClass = carClass; }
    public BigDecimal getBaseDailyRate() { return baseDailyRate; }
    public void setBaseDailyRate(BigDecimal baseDailyRate) { this.baseDailyRate = baseDailyRate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
