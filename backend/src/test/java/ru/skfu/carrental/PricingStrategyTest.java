package ru.skfu.carrental;

import org.junit.jupiter.api.Test;
import ru.skfu.carrental.mediator.StandardPricingStrategy;
import ru.skfu.carrental.mediator.WeekendPricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

class PricingStrategyTest {

    private final StandardPricingStrategy standardStrategy = new StandardPricingStrategy();
    private final WeekendPricingStrategy weekendStrategy = new WeekendPricingStrategy();

    @Test
    void standardStrategy_singleDay_returnsOneDayRate() {
        BigDecimal dailyRate = new BigDecimal("2500.00");
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 16, 10, 0); // Mon
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 17, 10, 0);   // Tue

        BigDecimal result = standardStrategy.calculate(dailyRate, start, end);

        assertThat(result).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    void standardStrategy_threeDays_returnsTripleRate() {
        BigDecimal dailyRate = new BigDecimal("2500.00");
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 16, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 19, 10, 0);

        BigDecimal result = standardStrategy.calculate(dailyRate, start, end);

        assertThat(result).isEqualByComparingTo(new BigDecimal("7500.00"));
    }

    @Test
    void weekendStrategy_appliesTwentyPercentMarkup() {
        BigDecimal dailyRate = new BigDecimal("2500.00");
        // Saturday to Sunday
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 20, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 21, 10, 0);

        BigDecimal result = weekendStrategy.calculate(dailyRate, start, end);

        assertThat(result).isEqualByComparingTo(new BigDecimal("3000.00"));
    }

    @Test
    void weekendStrategy_threeDaysWithWeekend_appliesMarkupToAll() {
        BigDecimal dailyRate = new BigDecimal("2000.00");
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 19, 10, 0); // Fri
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 22, 10, 0);   // Mon

        BigDecimal result = weekendStrategy.calculate(dailyRate, start, end);

        assertThat(result).isEqualByComparingTo(new BigDecimal("7200.00")); // 3*2000*1.20
    }

    @Test
    void hasWeekend_weekdaysOnly_returnsFalse() {
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 15, 10, 0); // Mon
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 19, 10, 0);   // Fri

        assertThat(WeekendPricingStrategy.hasWeekend(start, end)).isFalse();
    }

    @Test
    void hasWeekend_includesSaturday_returnsTrue() {
        LocalDateTime start = LocalDateTime.of(2026, Month.JUNE, 19, 10, 0); // Fri
        LocalDateTime end = LocalDateTime.of(2026, Month.JUNE, 21, 10, 0);   // Sun

        assertThat(WeekendPricingStrategy.hasWeekend(start, end)).isTrue();
    }
}
