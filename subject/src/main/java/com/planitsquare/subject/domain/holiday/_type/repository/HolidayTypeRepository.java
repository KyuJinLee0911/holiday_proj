package com.planitsquare.subject.domain.holiday._type.repository;

import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, Long> {
    List<HolidayType> findAllByHoliday_IdIn(List<Long> ids);
}
