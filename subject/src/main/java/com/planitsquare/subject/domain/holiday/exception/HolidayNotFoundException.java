package com.planitsquare.subject.domain.holiday.exception;

import com.planitsquare.subject.global.common.exception.ApiException;
import org.springframework.http.HttpStatus;

public class HolidayNotFoundException extends ApiException {
    private static final String MESSAGE = "공휴일 정보가 없습니다.";

    public HolidayNotFoundException() {
        super(HttpStatus.NOT_FOUND, MESSAGE, "404");
    }
}
