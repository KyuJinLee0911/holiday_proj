package com.planitsquare.subject.domain.holiday.repository;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {
    Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable);
}
