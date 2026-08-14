package com.dajeom.calculator;

import com.dajeom.entity.Gender;
import com.dajeom.model.DecadeLuckDirectionAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.type.DecadeLuckDirection;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.LuckStartBoundaryType;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 13차-1.
 *
 * 대운 순·역행 및 기산 기준 계산기.
 *
 * =================================================
 *
 * 기본 규칙:
 *
 * 양남 -> 순행
 * 음녀 -> 순행
 *
 * 음남 -> 역행
 * 양녀 -> 역행
 *
 * =================================================
 *
 * 양간:
 *
 * 甲 丙 戊 庚 壬
 *
 * 음간:
 *
 * 乙 丁 己 辛 癸
 *
 * =================================================
 *
 * 순행:
 *
 * 출생시점 -> 다음 월 절입
 *
 * 역행:
 *
 * 이전 월 절입 -> 출생시점
 *
 * =================================================
 *
 * 중요:
 *
 * 실제 기산 나이 계산은 하지 않는다.
 *
 * 이번 클래스는:
 *
 * 1. 방향
 * 2. 사용할 절입 방향
 *
 * 만 결정한다.
 *
 * 출생시각과 절입시각 사이 Duration을
 * 나이로 환산하는 것은 13-2에서 한다.
 */
@Component
public class LuckDirectionCalculator {

    /**
     * =================================================
     * 공식 계산 API
     * =================================================
     */
    public DecadeLuckDirection calculate(

            Gender gender,

            HeavenlyStem yearStem

    ) {

        Objects.requireNonNull(
                gender,
                "대운 방향 계산에는 성별이 필요합니다."
        );

        Objects.requireNonNull(
                yearStem,
                "대운 방향 계산에는 년간이 필요합니다."
        );

        boolean yangYearStem =
                isYangStem(
                        yearStem
                );

        /*
         * =================================================
         * 양남 / 음녀 -> 순행
         *
         * 음남 / 양녀 -> 역행
         * =================================================
         */

        boolean forward =
                (
                        gender == Gender.MALE
                                && yangYearStem
                )
                        ||
                        (
                                gender == Gender.FEMALE
                                        && !yangYearStem
                        );

        return forward
                ? DecadeLuckDirection.FORWARD
                : DecadeLuckDirection.REVERSE;
    }

    /**
     * =================================================
     * FourPillars 기반 공식 분석
     * =================================================
     *
     * 반드시 이미 계산된 사주 년주의 천간을 사용한다.
     */
    public DecadeLuckDirectionAnalysis analyze(

            Gender gender,

            FourPillars fourPillars

    ) {

        Objects.requireNonNull(
                fourPillars,
                "대운 방향 분석에는 FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                fourPillars.yearPillar(),
                "대운 방향 분석에는 년주가 필요합니다."
        );

        HeavenlyStem yearStem =
                Objects.requireNonNull(
                        fourPillars
                                .yearPillar()
                                .stem(),
                        "대운 방향 분석을 위한 년간을 찾을 수 없습니다."
                );

        return analyze(
                gender,
                yearStem
        );
    }

    /**
     * =================================================
     * 년간 직접 입력 분석
     * =================================================
     */
    public DecadeLuckDirectionAnalysis analyze(

            Gender gender,

            HeavenlyStem yearStem

    ) {

        Objects.requireNonNull(
                gender,
                "대운 방향 분석에는 성별이 필요합니다."
        );

        Objects.requireNonNull(
                yearStem,
                "대운 방향 분석에는 년간이 필요합니다."
        );

        boolean yangYearStem =
                isYangStem(
                        yearStem
                );

        DecadeLuckDirection direction =
                calculate(
                        gender,
                        yearStem
                );

        LuckStartBoundaryType startBoundaryType =
                determineStartBoundaryType(
                        direction
                );

        return new DecadeLuckDirectionAnalysis(
                gender,
                yearStem,
                yangYearStem,
                direction,
                startBoundaryType
        );
    }

    /**
     * =================================================
     * 기산 절입 기준
     * =================================================
     */
    public LuckStartBoundaryType determineStartBoundaryType(
            DecadeLuckDirection direction
    ) {

        Objects.requireNonNull(
                direction,
                "대운 방향이 필요합니다."
        );

        return switch (
                direction
                ) {

            case FORWARD ->
                    LuckStartBoundaryType
                            .NEXT_MONTH_BOUNDARY;

            case REVERSE ->
                    LuckStartBoundaryType
                            .PREVIOUS_MONTH_BOUNDARY;
        };
    }

    /**
     * =================================================
     * 천간 음양
     * =================================================
     *
     * 기존 HeavenlyStem의 부가 accessor에
     * 의존하지 않고 천간 자체로 결정한다.
     */
    public boolean isYangStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return switch (
                stem
                ) {

            case JIA,
                 BING,
                 WU,
                 GENG,
                 REN ->
                    true;

            case YI,
                 DING,
                 JI,
                 XIN,
                 GUI ->
                    false;
        };
    }

    public boolean isYinStem(
            HeavenlyStem stem
    ) {

        return !isYangStem(
                stem
        );
    }

    public boolean isForward(

            Gender gender,

            HeavenlyStem yearStem

    ) {

        return calculate(
                gender,
                yearStem
        )
                == DecadeLuckDirection.FORWARD;
    }

    public boolean isReverse(

            Gender gender,

            HeavenlyStem yearStem

    ) {

        return calculate(
                gender,
                yearStem
        )
                == DecadeLuckDirection.REVERSE;
    }
}