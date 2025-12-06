package com.planitsquare.subject.domain.holiday.dto.response;

import java.time.LocalDate;
import java.util.List;

public record HolidayResponse(
        Long holidayId,
        LocalDate date,
        String name,
        String localName,
        String countryCode,
        Integer launchYear,
        List<String> counties,
        List<String> types
) {
}
