package ru.skfu.carrental.entity;

import jakarta.persistence.*;
import ru.skfu.carrental.entity.enums.CarStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "current_mileage", nullable = false)
    private long currentMileage = 0;

    @Column(name = "fuel_level_percentage")
    private int fuelLevelPercentage = 100;

    @Column(name = "car_class", nullable = false, length = 50)
    private String carClass = "ECONOMY";

    @Column(name = "base_daily_rate", nullable = false)
    private BigDecimal baseDailyRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status = CarStatus.AVAILABLE;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public long getCurrentMileage() { return currentMileage; }
    public void setCurrentMileage(long currentMileage) { this.currentMileage = currentMileage; }
    public int getFuelLevelPercentage() { return fuelLevelPercentage; }
    public void setFuelLevelPercentage(int fuelLevelPercentage) { this.fuelLevelPercentage = fuelLevelPercentage; }
    public String getCarClass() { return carClass; }
    public void setCarClass(String carClass) { this.carClass = carClass; }
    public BigDecimal getBaseDailyRate() { return baseDailyRate; }
    public void setBaseDailyRate(BigDecimal baseDailyRate) { this.baseDailyRate = baseDailyRate; }
    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
