package ru.skfu.carrental.mediator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculate(BigDecimal dailyRate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long days = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (days < 1) days = 1;
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }
}
