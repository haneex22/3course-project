package ru.skfu.carrental.mediator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PricingStrategy {
    BigDecimal calculate(BigDecimal dailyRate, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
