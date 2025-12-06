package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.exception.HolidayNotFoundException;
import com.planitsquare.subject.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayReader {
    private final HolidayRepository holidayRepository;

    public long countExistingDataBetween(int from, int to, String countryCode) {
        LocalDate start = LocalDate.of(from, 1, 1);
        LocalDate end = LocalDate.of(to, 12, 31);
        return holidayRepository.countByDateBetweenAndCountry_CountryCode(start, end, countryCode);
    }

    public long countEveryData() {
        return holidayRepository.count();
    }

    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        Page<HolidayResponse> searchResult = holidayRepository.search(condition, pageable);
        if (searchResult.isEmpty()) {
            throw new HolidayNotFoundException();
        }
        return searchResult;
    }
}
