package com.dajeom.model;

import com.dajeom.entity.Gender;
import com.dajeom.type.DecadeLuckDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckStartBoundaryType;

import java.util.Objects;

/**
 * 정밀화 13차-1.
 *
 * 대운 방향 및 기산 기준 분석 결과.
 *
 * =================================================
 *
 * 보존 근거:
 *
 * - 성별
 * - 사주 년간
 * - 년간 음양
 * - 순행/역행
 * - 다음/이전 절입 기준
 *
 * =================================================
 *
 * 중요:
 *
 * yearStem은 단순한 출생연도의 달력 년간이 아니라
 * 이미 만세력에서 확정된 사주 년주의 천간이어야 한다.
 *
 * 따라서 입춘 이전 출생자도
 * FourPillars.yearPillar()의 결과를 기준으로 한다.
 */
public record DecadeLuckDirectionAnalysis(

        Gender gender,

        HeavenlyStem yearStem,

        boolean yangYearStem,

        DecadeLuckDirection direction,

        LuckStartBoundaryType startBoundaryType

) {

    public DecadeLuckDirectionAnalysis {

        Objects.requireNonNull(
                gender,
                "대운 방향 분석에는 성별이 필요합니다."
        );

        Objects.requireNonNull(
                yearStem,
                "대운 방향 분석에는 년간이 필요합니다."
        );

        Objects.requireNonNull(
                direction,
                "대운 방향이 필요합니다."
        );

        Objects.requireNonNull(
                startBoundaryType,
                "대운 기산 절입 기준이 필요합니다."
        );

        /*
         * =================================================
         * 방향 ↔ 절입 기준 정합성
         * =================================================
         */

        if (
                direction == DecadeLuckDirection.FORWARD
                        && startBoundaryType
                        != LuckStartBoundaryType.NEXT_MONTH_BOUNDARY
        ) {

            throw new IllegalArgumentException(
                    "순행 대운은 다음 절입을 기산 기준으로 사용해야 합니다."
            );
        }

        if (
                direction == DecadeLuckDirection.REVERSE
                        && startBoundaryType
                        != LuckStartBoundaryType.PREVIOUS_MONTH_BOUNDARY
        ) {

            throw new IllegalArgumentException(
                    "역행 대운은 이전 절입을 기산 기준으로 사용해야 합니다."
            );
        }
    }

    public boolean forward() {

        return direction.forward();
    }

    public boolean reverse() {

        return direction.reverse();
    }

    public boolean male() {

        return gender == Gender.MALE;
    }

    public boolean female() {

        return gender == Gender.FEMALE;
    }

    public boolean yinYearStem() {

        return !yangYearStem;
    }

    public boolean usesNextBoundary() {

        return startBoundaryType
                .nextBoundary();
    }

    public boolean usesPreviousBoundary() {

        return startBoundaryType
                .previousBoundary();
    }
}