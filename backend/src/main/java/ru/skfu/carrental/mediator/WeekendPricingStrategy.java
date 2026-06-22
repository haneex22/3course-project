package ru.skfu.carrental.mediator;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class WeekendPricingStrategy implements PricingStrategy {

    private static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.20");

    @Override
    public BigDecimal calculate(BigDecimal dailyRate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long days = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
        if (days < 1) {
            days = 1;
        }
        return dailyRate.multiply(BigDecimal.valueOf(days)).multiply(WEEKEND_MULTIPLIER);
    }

    public static boolean hasWeekend(LocalDateTime start, LocalDateTime end) {
        return start.toLocalDate().datesUntil(end.toLocalDate().plusDays(1))
                .anyMatch(d -> d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY);
    }
}
