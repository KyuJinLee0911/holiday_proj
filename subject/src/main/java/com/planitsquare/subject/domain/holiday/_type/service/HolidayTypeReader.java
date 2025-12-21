package com.planitsquare.subject.domain.holiday._type.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.repository.HolidayTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayTypeReader {
    private final HolidayTypeRepository repository;

    public List<HolidayType> getAllTypes(List<Long> holidayIds) {
        return repository.findAllByHoliday_IdIn(holidayIds);
    }

    public List<HolidayType> getByCountryAndYear(Country country, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return repository.findByHoliday_CountryAndHoliday_DateBetween(country, start, end);
    }
}
