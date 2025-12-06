package com.planitsquare.subject.domain.holiday.repository;

import com.planitsquare.subject.domain.country.dto.CountryDTO;
import com.planitsquare.subject.domain.country.entity.Country;
import com.planitsquare.subject.domain.holiday._county.entity.HolidayCounty;
import com.planitsquare.subject.domain.holiday._type.entity.HolidayType;
import com.planitsquare.subject.domain.holiday.dto.HolidayDTO;
import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.entity.Holiday;
import com.planitsquare.subject.global.common.configuration.QuerydslConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QuerydslConfig.class})
public class HolidayRepositoryImplTest {

    @Autowired
    EntityManager em;

    @Autowired
    HolidayRepositoryImpl holidayRepository;

    @BeforeEach
    void setUp() {
        CountryDTO krDTO = new CountryDTO("KR", "Korea");
        CountryDTO usDTO = new CountryDTO("US", "United States");
        Country kr = Country.from(krDTO);
        Country us = Country.from(usDTO);
        em.persist(kr);
        em.persist(us);
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

        HolidayDTO christmasDTO = new HolidayDTO(
                LocalDate.of(2024, 12, 25),
                "성탄절",
                "Christmas",
                kr.getCountryCode(),
                true,
                true,
                null,
                null,
                List.of("Public", "School")
        );

        Holiday krNewYear = Holiday.from(krNewYearDTO, kr);
        Holiday usNewYear = Holiday.from(usNewYearDTO, us);
        Holiday krChristmas = Holiday.from(christmasDTO, kr);

        em.persist(krNewYear);
        em.persist(usNewYear);
        em.persist(krChristmas);

        em.persist(HolidayType.of(krNewYear, "Public"));
        em.persist(HolidayType.of(krNewYear, "Bank"));
        em.persist(HolidayType.of(usNewYear, "Public"));
        em.persist(HolidayType.of(krChristmas, "Public"));
        em.persist(HolidayType.of(krChristmas, "School"));

        em.persist(HolidayCounty.of(krNewYear, "KR-11"));

        em.flush();
        em.clear();
    }

    @Test
    void 연도와_국가코드로_검색하면_해당_조건의_공휴일만_조회된다() {
        // given
        HolidaySearchCondition condition = new HolidaySearchCondition(2025, "KR", null, null, null);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<HolidayResponse> page = holidayRepository.search(condition, pageRequest);

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
        HolidayResponse response = page.getContent().get(0);
        assertThat(response.countryCode()).isEqualTo("KR");
        assertThat(response.date()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(response.types()).containsExactlyInAnyOrder("Public", "Bank");
        assertThat(response.counties()).containsExactly("KR-11");
    }

    @Test
    void type_조건으로_조회하면_해당_type을_포함한_공휴일만_조회된다() {
        // given
        HolidaySearchCondition condition = new HolidaySearchCondition(null, null, null, null, "School");

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<HolidayResponse> page = holidayRepository.search(condition, pageRequest);

        // then
        assertThat(page.getTotalElements()).isEqualTo(1);
        HolidayResponse response = page.getContent().get(0);
        assertThat(response.countryCode()).isEqualTo("KR");
        assertThat(response.date()).isEqualTo(LocalDate.of(2024, 12, 25));
        assertThat(response.types()).contains("School");
    }

    @Test
    void from_to_기간으로_조회하면_해당_기간_내의_공휴일만_조회된다() {
        // given
        HolidaySearchCondition condition = new HolidaySearchCondition(
                null,
                null,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                null);

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<HolidayResponse> page = holidayRepository.search(condition, pageRequest);

        // then
        assertThat(page.getTotalElements()).isEqualTo(2);
        List<LocalDate> dates = page.map(HolidayResponse::date).getContent();
        assertThat(dates).containsOnly(LocalDate.of(2025, 1, 1));
    }
}
