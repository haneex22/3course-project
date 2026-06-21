package ru.skfu.carrental.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReturnRequest {

    @NotNull
    @Min(0)
    private Long finalMileage;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer finalFuelLevel;

    public Long getFinalMileage() { return finalMileage; }
    public void setFinalMileage(Long finalMileage) { this.finalMileage = finalMileage; }
    public Integer getFinalFuelLevel() { return finalFuelLevel; }
    public void setFinalFuelLevel(Integer finalFuelLevel) { this.finalFuelLevel = finalFuelLevel; }
}
