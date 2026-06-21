package ru.skfu.carrental.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class HandoverRequest {

    @NotNull
    @Min(0)
    private Long initialMileage;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer initialFuelLevel;

    public Long getInitialMileage() { return initialMileage; }
    public void setInitialMileage(Long initialMileage) { this.initialMileage = initialMileage; }
    public Integer getInitialFuelLevel() { return initialFuelLevel; }
    public void setInitialFuelLevel(Integer initialFuelLevel) { this.initialFuelLevel = initialFuelLevel; }
}
