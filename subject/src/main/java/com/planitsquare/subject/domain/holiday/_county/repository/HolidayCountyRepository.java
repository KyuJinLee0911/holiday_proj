package com.planitsquare.subject.domain.holiday._county.repository;

import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, Long> {
    List<HolidayCounty> findAllByHoliday_IdIn(List<Long> ids);
}
