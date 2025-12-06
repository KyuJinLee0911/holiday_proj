package com.planitsquare.subject.domain.holiday._type.service;

import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.repository.HolidayTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayTypeReader {
    private final HolidayTypeRepository repository;

    public List<HolidayType> getAllTypes(List<Long> holidayIds) {
        return repository.findAllByHoliday_IdIn(holidayIds);
    }
}
