package com.dajeom.calculation;

import com.dajeom.domain.DayMasterForceFormationAnalysis;
import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterForceSupportPattern;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 得勢 영역의 생조/설기/재/관살 구조를
 * 명리 Context로 정리한다.
 *
 * =================================================
 *
 * 핵심:
 *
 * 천간에 실제 드러난
 *
 * - 일간을 생함
 * - 같은 오행
 *
 * 을 visibleSupportPattern으로 분류한다.
 *
 * 그리고 별도로:
 *
 * - 지장간 생조 기반
 * - 설기
 * - 재성 방향
 * - 관살 방향
 *
 * 을 그대로 유지한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 인성 2개 = 몇 점
 * - 비겁 1개 = 몇 점
 * - 생조가 더 많다
 * - 극설이 더 많다
 * - 得勢 true / false
 * - 신강/신약
 */
@Component
public class DayMasterForceStatusCalculator {

    public DayMasterForceStatusAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterForceFormationAnalysis forceFormation

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                forceFormation,
                "일간 세력 형성 구조는 필수입니다."
        );

        if (
                forceFormation.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "세력 형성 구조의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        DayMasterForceSupportPattern
                visibleSupportPattern =
                resolveVisibleSupportPattern(
                        forceFormation
                );

        return new DayMasterForceStatusAnalysis(

                dayMaster,

                visibleSupportPattern,

                /*
                 * 천간 인성 방향
                 */
                forceFormation
                        .visibleGeneratingDayMaster(),

                /*
                 * 천간 비겁 방향
                 */
                forceFormation
                        .visibleSameElement(),

                /*
                 * 지장간 인성 방향
                 */
                forceFormation
                        .hiddenGeneratingDayMaster(),

                /*
                 * 지장간 비겁 방향
                 */
                forceFormation
                        .hiddenSameElement(),

                /*
                 * 천간 설기 방향
                 */
                forceFormation
                        .visibleGeneratedByDayMaster(),

                /*
                 * 지장간 설기 방향
                 */
                forceFormation
                        .hiddenGeneratedByDayMaster(),

                /*
                 * 천간 재성 방향
                 */
                forceFormation
                        .visibleControlledByDayMaster(),

                /*
                 * 지장간 재성 방향
                 */
                forceFormation
                        .hiddenControlledByDayMaster(),

                /*
                 * 천간 관살 방향
                 */
                forceFormation
                        .visibleControllingDayMaster(),

                /*
                 * 지장간 관살 방향
                 */
                forceFormation
                        .hiddenControllingDayMaster()
        );
    }

    private DayMasterForceSupportPattern
    resolveVisibleSupportPattern(

            DayMasterForceFormationAnalysis forceFormation

    ) {

        boolean generating =
                !forceFormation
                        .visibleGeneratingDayMaster()
                        .isEmpty();

        boolean sameElement =
                !forceFormation
                        .visibleSameElement()
                        .isEmpty();

        /*
         * 개수를 비교하지 않는다.
         *
         * 두 종류의 명리 구조가
         * 존재하는지만 본다.
         */

        if (
                generating
                        &&
                        sameElement
        ) {

            return DayMasterForceSupportPattern
                    .GENERATING_AND_SAME_ELEMENT;
        }

        if (generating) {

            return DayMasterForceSupportPattern
                    .GENERATING_ONLY;
        }

        if (sameElement) {

            return DayMasterForceSupportPattern
                    .SAME_ELEMENT_ONLY;
        }

        return DayMasterForceSupportPattern
                .NO_VISIBLE_SUPPORT;
    }
}