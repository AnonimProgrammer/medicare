package com.ironhack.application.dto.query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import com.ironhack.domain.AppointmentStatus;

public record AppointmentQueryCriteria(List<AppointmentStatus> statuses, LocalDate date, Instant from, Instant to) {

    public AppointmentQueryCriteria {
        statuses = statuses == null ? List.of() : List.copyOf(statuses);
    }

    public void validateRange() {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'.");
        }
    }
}
