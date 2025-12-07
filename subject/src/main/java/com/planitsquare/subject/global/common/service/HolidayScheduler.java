package com.planitsquare.subject.global.common.service;

import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryReader;
import com.planitsquare.subject.domain.holiday.dto.request.UpdateHolidayRequest;
import com.planitsquare.subject.domain.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayScheduler {
    private final HolidayService holidayService;
    private final CountryReader countryReader;

    @Scheduled(cron = "0 0 1 2 1 *", zone = "Asia/Seoul")
    @Transactional
    public void syncPreviousAndCurrentYear() {
        ZonedDateTime nowKst = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        int currentYear = nowKst.getYear();
        int previousYear = currentYear - 1;

        List<Country> countries = countryReader.getAll();

        for (Country country : countries) {
            String countryCode = country.getCountryCode();

            holidayService.refresh(new UpdateHolidayRequest(previousYear, countryCode));
            holidayService.refresh(new UpdateHolidayRequest(currentYear, countryCode));
        }
    }
}
