package com.planitsquare.subject.domain.holiday.controller;

import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.service.HolidayService;
import com.planitsquare.subject.global.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/holidays")
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HolidayResponse>>> search(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String countryCode,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to,
            @RequestParam(required = false) String type,
            Pageable pageable
    ) {
        HolidaySearchCondition condition = new HolidaySearchCondition(year, countryCode, from, to, type);
        Page<HolidayResponse> responses = holidayService.searchHolidays(condition, pageable);
        return ApiResponse.ok(responses);
    }
}
