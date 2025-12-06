package com.planitsquare.subject.domain.holiday.service;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryReader;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.global.common.utils.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class HolidayServiceTest {
    @Mock
    HolidayReader holidayReader;
    @Mock
    HolidayStore holidayStore;
    @Mock
    HolidayCountyStore holidayCountyStore;
    @Mock
    HolidayTypeStore holidayTypeStore;
    @Mock
    CountryReader countryReader;
    @Mock
    ExternalApiClient externalApiClient;

    @InjectMocks
    HolidayService holidayService;


    @Test
    void 같은_공휴일이_중복되어_들어와도_Holiday는_한번만_저장된다() {
        // given
        CountryDTO countryDTO = new CountryDTO("KR", "Korea");
        Country country = Country.from(countryDTO);

        HolidayDTO dto1 = new HolidayDTO(
                LocalDate.of(2020, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                List.of("KR-11"),
                null,
                List.of("Public", "Bank")
        );

        HolidayDTO dto2 = new HolidayDTO(
                LocalDate.of(2020, 01, 01),
                "새해",
                "New Year's Day",
                "KR",
                false,
                true,
                List.of("KR-12"),
                null,
                List.of("Public")
        );

        List<HolidayDTO> dtos = List.of(dto1, dto2);

        // when
        holidayService.saveAllEntities(dtos, country);

        // then
        verify(holidayStore).saveAll(argThat(list -> list.size() == 1));
        verify(holidayCountyStore).saveAll(argThat(list -> list.size() == 2));
        verify(holidayTypeStore).saveAll(argThat(list -> list.size() == 2));
    }
}
