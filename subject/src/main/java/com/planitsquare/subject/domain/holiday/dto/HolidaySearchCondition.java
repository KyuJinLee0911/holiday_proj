package com.planitsquare.subject.domain.holiday.dto;

import java.time.LocalDate;

public record HolidaySearchCondition(
        Integer year,
        String countryCode,
        LocalDate from,
        LocalDate to,
        String type
) {
}
