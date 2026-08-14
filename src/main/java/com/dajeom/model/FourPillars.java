package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주의 네 기둥을 표현한다.
 *
 * 년주 / 월주 / 일주는 반드시 존재한다.
 *
 * 시주는 출생시간을 모르는 경우
 * 존재하지 않을 수 있다.
 *
 * 중요:
 *
 * 시주가 없다고 해서
 * 임의의 시주를 생성하지 않는다.
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
                "년주가 필요합니다."
        );

        Objects.requireNonNull(
                monthPillar,
                "월주가 필요합니다."
        );

        Objects.requireNonNull(
                dayPillar,
                "일주가 필요합니다."
        );

        /*
         * hourPillar는 의도적으로 null을 허용한다.
         *
         * 출생시간 미상인 경우
         * 시주를 가짜로 생성하지 않기 때문이다.
         */
    }

    /**
     * 일간.
     *
     * 사주에서 일간은 일주의 천간이다.
     *
     * 여러 분석 계층에서 반복적으로 사용하므로
     * FourPillars의 공식 편의 API로 제공한다.
     */
    public HeavenlyStem dayMaster() {

        return dayPillar.stem();
    }

    /**
     * 시주 존재 여부.
     */
    public boolean hasHourPillar() {

        return hourPillar != null;
    }

    /**
     * 시주를 Optional 형태로 반환한다.
     */
    public Optional<Pillar> hourPillarOptional() {

        return Optional.ofNullable(
                hourPillar
        );
    }

    /**
     * 현재 확정된 기둥 개수.
     *
     * 출생시간 있음  -> 4
     * 출생시간 미상 -> 3
     */
    public int knownPillarCount() {

        return hasHourPillar()
                ? 4
                : 3;
    }

    /**
     * 확정 가능한 기둥만 순서대로 반환한다.
     *
     * 년주
     * 월주
     * 일주
     * 시주(존재할 경우)
     */
    public List<Pillar> knownPillars() {

        List<Pillar> result =
                new ArrayList<>();

        result.add(
                yearPillar
        );

        result.add(
                monthPillar
        );

        result.add(
                dayPillar
        );

        if (
                hourPillar != null
        ) {

            result.add(
                    hourPillar
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 완전한 사주팔자인지 여부.
     *
     * true:
     * 4주 8자
     *
     * false:
     * 출생시간 미상으로 3주 6자
     */
    public boolean complete() {

        return hasHourPillar();
    }
}