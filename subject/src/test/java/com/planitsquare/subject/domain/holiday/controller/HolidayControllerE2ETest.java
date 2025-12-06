package com.planitsquare.subject.domain.holiday.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.country.service.CountryStore;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._county.service.HolidayCountyStore;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday._type.service.HolidayTypeStore;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.domain.holiday.service.HolidayStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HolidayControllerE2ETest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CountryStore countryStore;

    @Autowired
    HolidayStore holidayStore;

    @Autowired
    HolidayTypeStore holidayTypeStore;

    @Autowired
    HolidayCountyStore holidayCountyStore;

    @BeforeEach
    void setUp() {
        holidayCountyStore.removeAllInBatch();
        holidayTypeStore.removeAllInBatch();
        holidayStore.removeAllInBatch();

        CountryDTO krDTO = new CountryDTO("KR", "Korea");
        CountryDTO usDTO = new CountryDTO("US", "United States");
        Country kr = Country.from(krDTO);
        Country us = Country.from(usDTO);
        countryStore.saveAll(List.of(kr, us));

        HolidayDTO krNewYearDTO = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "새해",
                "New Year's Day",
                kr.getCountryCode(),
                true,
                true,
                List.of("KR-11"),
                null,
                List.of("Public", "Bank")
        );

        HolidayDTO usNewYearDTO = new HolidayDTO(
                LocalDate.of(2025, 1, 1),
                "New Year's Day",
                "New Year's Day",
                us.getCountryCode(),
                true,
                true,
                null,
                null,
                List.of("Public")
        );

        Holiday krNewYear = Holiday.from(krNewYearDTO, kr);
        Holiday usNewYear = Holiday.from(usNewYearDTO, us);

        holidayStore.saveAll(List.of(krNewYear, usNewYear));
        holidayTypeStore.saveAll(
                List.of(
                        HolidayType.of(krNewYear, "Public"),
                        HolidayType.of(krNewYear, "Bank"),
                        HolidayType.of(usNewYear, "Public")
                )
        );
        holidayCountyStore.saveAll(List.of(HolidayCounty.of(krNewYear, "KR-11")));
    }

    @Nested
    @DisplayName("공휴일 검색 API (/api/holidays)")
    class SearchApi {
        @Test
        void 연도와_국가로_HTTP_요청하면_JSON_페이지_응답을_받는다() throws Exception {
            mockMvc.perform(get("/api/holidays")
                            .param("year", "2025")
                            .param("countryCode", "KR")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].countryCode").value("KR"))
                    .andExpect(jsonPath("$.data.content[0].date").value("2025-01-01"))
                    .andExpect(jsonPath("$.data.content[0].types", containsInAnyOrder("Public", "Bank")))
                    .andExpect(jsonPath("$.data.content[0].counties", contains("KR-11")))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.number").value(0))
                    .andExpect(jsonPath("$.data.size").value(10));
        }

        @Test
        void type_파라미터로_public만_검색하면_public을_포함하는_공휴일만_반환된다() throws Exception {
            mockMvc.perform(get("/api/holidays")
                            .param("type", "Public")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(2)))
                    .andExpect(jsonPath("$.data.content[*].types", everyItem(hasItem("Public"))));
        }
    }
}
