package com.dajeom.calculator;

import com.dajeom.model.LuckReferenceContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRules;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 16차-1.
 *
 * 사용자의 조회 날짜/일시를
 * deterministic LuckReferenceContext로 변환한다.
 *
 * =================================================
 *
 * 지원:
 *
 * ZonedDateTime
 * -> 정확한 시각
 *
 * LocalDateTime + ZoneId
 * -> 정확한 지역시각
 *
 * LocalDate + ZoneId
 * -> 날짜만 입력
 *
 * =================================================
 *
 * 금지:
 *
 * LocalDate에 임의의 12:00 추가
 *
 * LocalDate에 임의의 00:00 추가 후
 * exact로 취급
 *
 * ZonedDateTime.now() 자동 사용
 *
 * LocalDate.now() 자동 사용
 *
 * =================================================
 *
 * 현재 시점을 분석하고 싶어도
 * 상위 Service/Controller가 실제 현재시각을
 * 명시적으로 전달해야 한다.
 */
@Component
public class LuckReferenceContextCalculator {

    /**
     * =================================================
     * ZonedDateTime
     * =================================================
     *
     * 사용자가 offset/zone까지 포함한
     * 정확한 순간을 제공한 경우.
     *
     * 다른 Zone으로 입력됐더라도
     * 동일 Instant를 분석 Zone으로 변환한다.
     */
    public LuckReferenceContext exact(

            ZonedDateTime referenceDateTime,

            ZoneId analysisZoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "정확한 운 분석 기준시각이 필요합니다."
        );

        Objects.requireNonNull(
                analysisZoneId,
                "운 분석 ZoneId가 필요합니다."
        );

        return LuckReferenceContext.exact(
                referenceDateTime,
                analysisZoneId
        );
    }

    /**
     * =================================================
     * LocalDateTime
     * =================================================
     *
     * 사용자가:
     *
     * 2026-08-20 15:30
     *
     * 처럼 지역시각을 입력한 경우.
     *
     * ZoneId를 반드시 함께 받아야 한다.
     *
     * =================================================
     *
     * DST Gap:
     *
     * 실제 존재하지 않는 지역시각이면 거부한다.
     *
     * =================================================
     *
     * DST Overlap:
     *
     * 동일 LocalDateTime에 Offset이 2개 가능한 경우
     * 임의로 하나를 고르지 않는다.
     *
     * 이 경우 Offset까지 포함한 ZonedDateTime을
     * 입력해야 한다.
     */
    public LuckReferenceContext exact(

            LocalDateTime referenceDateTime,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDateTime,
                "정확한 운 분석 기준일시가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "운 분석 ZoneId가 필요합니다."
        );

        ZoneRules rules =
                zoneId
                        .getRules();

        List<ZoneOffset> validOffsets =
                rules
                        .getValidOffsets(
                                referenceDateTime
                        );

        /*
         * DST Gap.
         *
         * 존재하지 않는 지역시각.
         */
        if (
                validOffsets.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "해당 지역에서 실제 존재하지 않는 일시입니다: "
                            + referenceDateTime
                            + " / "
                            + zoneId
            );
        }

        /*
         * DST Overlap.
         *
         * 동일 local time에 두 Instant가 가능하다.
         */
        if (
                validOffsets.size() > 1
        ) {

            throw new IllegalArgumentException(
                    "해당 지역에서 중복되는 지역시각입니다. "
                            + "정확한 Offset이 포함된 ZonedDateTime을 사용해야 합니다: "
                            + referenceDateTime
                            + " / "
                            + zoneId
            );
        }

        ZoneOffset offset =
                validOffsets.get(0);

        ZonedDateTime zonedDateTime =
                ZonedDateTime.ofStrict(
                        referenceDateTime,
                        offset,
                        zoneId
                );

        return LuckReferenceContext.exact(
                zonedDateTime,
                zoneId
        );
    }

    /**
     * =================================================
     * LocalDate
     * =================================================
     *
     * 사용자가 날짜만 입력한 경우.
     *
     * 어떤 시각도 임의로 생성하지 않는다.
     *
     * 이후:
     *
     * 대운
     * 세운
     * 월운
     *
     * Candidate Selector가
     * 해당 civil date 전체를 대상으로
     * possible / definite를 판정한다.
     */
    public LuckReferenceContext dateOnly(

            LocalDate referenceDate,

            ZoneId zoneId

    ) {

        Objects.requireNonNull(
                referenceDate,
                "운 분석 기준 날짜가 필요합니다."
        );

        Objects.requireNonNull(
                zoneId,
                "운 분석 ZoneId가 필요합니다."
        );

        return LuckReferenceContext.dateOnly(
                referenceDate,
                zoneId
        );
    }
}