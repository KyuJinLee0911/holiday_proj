package com.planitsquare.subject.domain.holiday.repository;

import com.planitsquare.subject.domain.holiday._county.entity.QHolidayCounty;
import com.planitsquare.subject.domain.holiday._type.entity.QHolidayType;
import com.planitsquare.subject.domain.holiday.dto.HolidayBaseDTO;
import com.planitsquare.subject.domain.holiday.dto.HolidaySearchCondition;
import com.planitsquare.subject.domain.holiday.dto.response.HolidayResponse;
import com.planitsquare.subject.domain.holiday.entity.QHoliday;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HolidayResponse> search(HolidaySearchCondition condition, Pageable pageable) {
        QHoliday h = QHoliday.holiday;
        QHolidayType ht = QHolidayType.holidayType;
        QHolidayCounty hc = QHolidayCounty.holidayCounty;

        BooleanBuilder builder = new BooleanBuilder();

        if (condition.year() != null) {
            builder.and(h.date.year().eq(condition.year()));
        }

        if (condition.countryCode() != null) {
            builder.and(h.country.countryCode.eq(condition.countryCode()));
        }

        if (condition.from() != null) {
            builder.and(h.date.goe(condition.from()));
        }

        if (condition.to() != null) {
            builder.and(h.date.loe(condition.to()));
        }

        // type 조건 제한
        if (condition.type() != null) {
            builder.and(h.id.in(
                    JPAExpressions
                            .select(ht.holiday.id)
                            .from(ht)
                            .where(ht.typeCode.eq(condition.type()))
            ));
        }

        // Holiday Id 추출
        List<Long> holidayIds = queryFactory
                .select(h.id)
                .from(h)
                .where(builder)
                .orderBy(h.date.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        if (holidayIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 기본 정보 추출(County와 Type 제외한)
        List<HolidayBaseDTO> baseList = queryFactory
                .select(Projections.constructor(
                        HolidayBaseDTO.class,
                        h.id,
                        h.date,
                        h.name,
                        h.localName,
                        h.country.countryCode,
                        h.launchYear
                ))
                .from(h)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(h.date.asc())
                .fetch();

        // id와 기본 정보 맵핑
        Map<Long, HolidayBaseDTO> baseMap = baseList.stream()
                .collect(Collectors.toMap(HolidayBaseDTO::holidayId, dto -> dto));


        // Type 검색
        List<Tuple> typeRows = queryFactory
                .select(ht.holiday.id, ht.typeCode)
                .from(ht)
                .where(ht.holiday.id.in(holidayIds))
                .fetch();

        // Type을 holidayId와 맵핑
        Map<Long, List<String>> typeMap = new HashMap<>();
        for (Tuple row : typeRows) {
            Long hid = row.get(ht.holiday.id);
            String typeCode = row.get(ht.typeCode);
            typeMap.computeIfAbsent(hid, k -> new ArrayList<>())
                    .add(typeCode);
        }


        // County 검색
        List<Tuple> countyRows = queryFactory
                .select(hc.holiday.id, hc.countyCode)
                .from(hc)
                .where(hc.holiday.id.in(holidayIds))
                .fetch();

        // County를 holidayId와 맵핑
        Map<Long, List<String>> countyMap = new HashMap<>();

        for (Tuple row : countyRows) {
            Long hid = row.get(hc.holiday.id);
            String countyCode = row.get(hc.countyCode);
            countyMap.computeIfAbsent(hid, k -> new ArrayList<>())
                    .add(countyCode);
        }

        // 모두 합친 최종 반환 데이터
        List<HolidayResponse> content = new ArrayList<>();

        // base, county, type 조립
        for (Long hid : holidayIds) {
            HolidayBaseDTO base = baseMap.get(hid);
            if (base == null) continue;

            List<String> types = typeMap.getOrDefault(hid, List.of());
            List<String> counties = countyMap.getOrDefault(hid, List.of());

            content.add(new HolidayResponse(
                    base.holidayId(),
                    base.date(),
                    base.name(),
                    base.localName(),
                    base.countryCode(),
                    base.launchYear(),
                    counties,
                    types
            ));
        }

        Long total = queryFactory
                .select(h.count())
                .from(h)
                .where(builder)
                .fetchOne();

        long totalCount = (total != null) ? total : 0L;

        return new PageImpl<>(content, pageable, totalCount);
    }
}
