package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayReader holidayReader;

    @Transactional
    public Page<HolidayResponse> searchHolidays(HolidaySearchCondition condition, Pageable pageable) {
        return holidayReader.search(condition, pageable);
    }
}
