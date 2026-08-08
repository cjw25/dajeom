package com.dajeom.model;

import java.util.Objects;
import java.util.Optional;

/**
 * 사주 원국의 네 기둥.
 *
 * 출생시간을 아는 경우:
 * 년주 + 월주 + 일주 + 시주
 *
 * 출생시간을 모르는 경우:
 * 년주 + 월주 + 일주
 * 시주는 null 상태로 유지한다.
 *
 * 출생시간을 모른다고 임의의 시간을
 * 만들어 시주를 계산하지 않는다.
 */
public record FourPillars(

        Pillar yearPillar,

        Pillar monthPillar,

        Pillar dayPillar,

        Pillar hourPillar

) {

    public FourPillars {

        Objects.requireNonNull(
                yearPillar,
                "년주는 필수입니다."
        );

        Objects.requireNonNull(
                monthPillar,
                "월주는 필수입니다."
        );

        Objects.requireNonNull(
                dayPillar,
                "일주는 필수입니다."
        );

        /*
         * hourPillar는 출생시간을 모르는 경우
         * null이 될 수 있으므로
         * Objects.requireNonNull을 사용하지 않는다.
         */
    }

    /**
     * 시주 계산 여부.
     */
    public boolean hasHourPillar() {

        return hourPillar != null;
    }

    /**
     * 시주를 Optional 형태로 조회한다.
     */
    public Optional<Pillar> optionalHourPillar() {

        return Optional.ofNullable(
                hourPillar
        );
    }

    /**
     * 전체 사주 한자 표현.
     *
     * 시간 있음:
     * 癸酉 庚申 丁丑 甲辰
     *
     * 시간 모름:
     * 癸酉 庚申 丁丑
     */
    public String fullChineseName() {

        StringBuilder builder =
                new StringBuilder();

        builder
                .append(
                        yearPillar.chineseName()
                )
                .append(" ")
                .append(
                        monthPillar.chineseName()
                )
                .append(" ")
                .append(
                        dayPillar.chineseName()
                );

        if (hourPillar != null) {

            builder
                    .append(" ")
                    .append(
                            hourPillar.chineseName()
                    );
        }

        return builder.toString();
    }

    /**
     * 전체 사주 한글 표현.
     *
     * 시간 있음:
     * 계유 경신 정축 갑진
     *
     * 시간 모름:
     * 계유 경신 정축
     */
    public String fullKoreanName() {

        StringBuilder builder =
                new StringBuilder();

        builder
                .append(
                        yearPillar.koreanName()
                )
                .append(" ")
                .append(
                        monthPillar.koreanName()
                )
                .append(" ")
                .append(
                        dayPillar.koreanName()
                );

        if (hourPillar != null) {

            builder
                    .append(" ")
                    .append(
                            hourPillar.koreanName()
                    );
        }

        return builder.toString();
    }

    /**
     * 일간.
     *
     * 명리 분석의 중심이 되는 천간이다.
     */
    public com.dajeom.type.HeavenlyStem dayMaster() {

        return dayPillar.stem();
    }
}