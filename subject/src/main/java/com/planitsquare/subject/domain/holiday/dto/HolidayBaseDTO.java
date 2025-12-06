package com.planitsquare.subject.domain.holiday.dto;

import java.time.LocalDate;

public record HolidayBaseDTO(
        Long holidayId,
        LocalDate date,
        String name,
        String localName,
        String countryCode,
        Integer launchYear
) {
}