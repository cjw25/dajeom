package com.dajeom.domain;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 대운 / 세운 / 월운의 기간 식별자.
 *
 * =================================================
 *
 * key
 *
 * 내부 식별값.
 *
 * 예:
 *
 * DAEWOON-3
 * SEWOON-2026
 * WOLWOON-2026-09
 *
 * =================================================
 *
 * displayLabel
 *
 * 내부 계산표를 사용자에게 그대로 노출하기 위한 값이 아니다.
 *
 * AI 내부 입력에서 기간을 구분할 때 사용할 수 있다.
 *
 * =================================================
 *
 * startInclusive
 * endExclusive
 *
 * 절입 기준을 반영한 실제 기간 범위.
 */
public record LifePeriodKey(

        LifePeriodType type,

        String key,

        String displayLabel,

        LocalDateTime startInclusive,

        LocalDateTime endExclusive

) {

    public LifePeriodKey {

        Objects.requireNonNull(
                type,
                "LifePeriodType은 필수입니다."
        );

        key =
                requireText(
                        key,
                        "period key"
                );

        displayLabel =
                requireText(
                        displayLabel,
                        "displayLabel"
                );

        Objects.requireNonNull(
                startInclusive,
                "기간 시작시각은 필수입니다."
        );

        Objects.requireNonNull(
                endExclusive,
                "기간 종료시각은 필수입니다."
        );

        if (
                !startInclusive.isBefore(
                        endExclusive
                )
        ) {

            throw new IllegalArgumentException(
                    "기간 시작시각은 종료시각보다 빨라야 합니다."
            );
        }
    }

    public boolean contains(
            LocalDateTime time
    ) {

        Objects.requireNonNull(
                time,
                "확인할 시각은 필수입니다."
        );

        return !time.isBefore(startInclusive)
                &&
                time.isBefore(endExclusive);
    }

    private static String requireText(

            String value,

            String name

    ) {

        if (
                value == null
                        ||
                        value.isBlank()
        ) {

            throw new IllegalArgumentException(
                    name + "은 비어 있을 수 없습니다."
            );
        }

        return value.trim();
    }
}