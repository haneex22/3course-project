package ru.skfu.carrental.dto.response;

import java.time.LocalDateTime;

public class BusyPeriodResponse {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public BusyPeriodResponse() {}

    public BusyPeriodResponse(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }
}
