package com.planitsquare.subject.domain.holiday._county.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.repository.HolidayCountyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayCountyReader {
    private final HolidayCountyRepository repository;

    public List<HolidayCounty> getAllCounties(List<Long> holidayIds) {
        return repository.findAllByHoliday_IdIn(holidayIds);
    }

    public List<HolidayCounty> getByCountryAndYear(Country country, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return repository.findByHoliday_CountryAndHoliday_DateBetween(country, start, end);
    }
}
