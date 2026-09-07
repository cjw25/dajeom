package com.dajeom.rule;

import com.dajeom.domain.DayMasterForceStatusAnalysis;
import com.dajeom.domain.DayMasterGroundStatus;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterSpecialStructureCandidateAnalysis;
import com.dajeom.domain.DayMasterStrengthAnalysis;
import com.dajeom.domain.DayMasterStrengthDecisionBasis;
import com.dajeom.domain.DayMasterStrengthStatus;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 일반적인 일간 신강/신약 판정 Rule.
 *
 * =================================================
 *
 * 중요:
 *
 * 이 Rule은 오직 "일반 왕쇠"만 판단한다.
 *
 * 真化 / 從格 등의 우선 분기는
 * DayMasterStrengthDecisionCalculator가
 * 이 Rule을 호출하기 전에 처리한다.
 *
 * 따라서 특수구조 "후보"가 있다는 이유만으로
 * 이 Rule 자체가 중단되지는 않는다.
 *
 * =================================================
 *
 * 판단 순서:
 *
 * 1. 월령 旺相休囚死
 * 2. 실제 통근/得地 구조
 * 3. 인성·비겁 생부
 * 4. 식상·재성·관살 방향
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 오행 개수 비교
 * - 점수
 * - 퍼센트
 * - 가중치
 * - 다수결
 *
 * =================================================
 *
 * 명백하지 않은 경우
 * MIXED_NEEDS_REVIEW로 남긴다.
 */
@Component
public class DayMasterStrengthRule {

    public DayMasterStrengthAnalysis assess(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis
                    specialStructure

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "월령 분석은 필수입니다."
        );

        Objects.requireNonNull(
                ground,
                "득지 구조 분석은 필수입니다."
        );

        Objects.requireNonNull(
                force,
                "득세 구조 분석은 필수입니다."
        );

        Objects.requireNonNull(
                specialStructure,
                "특수구조 후보 분석은 필수입니다."
        );

        validateDayMaster(
                dayMaster,
                season,
                ground,
                force,
                specialStructure
        );

        FiveElementSeasonState seasonState =
                season.seasonState();

        DayMasterGroundStatus groundStatus =
                ground.status();

        boolean supportiveSeason =
                seasonState
                        == FiveElementSeasonState.WANG

                        ||

                        seasonState
                                == FiveElementSeasonState.XIANG;

        boolean rootPresent =
                groundStatus
                        != DayMasterGroundStatus.NO_ROOT;

        boolean generatingSupport =
                force
                        .hasAnyGeneratingSupport();

        boolean sameElementSupport =
                force
                        .hasAnySameElementSupport();

        boolean support =
                generatingSupport
                        ||
                        sameElementSupport;

        boolean output =
                force
                        .hasGeneratingOutflowStructure();

        boolean wealth =
                force
                        .hasWealthDirectionStructure();

        boolean control =
                force
                        .hasControlPressureStructure();

        boolean opposingDirection =
                output
                        ||
                        wealth
                        ||
                        control;

        /*
         * =================================================
         * 1. 명백한 강한 구조
         * =================================================
         *
         * - 旺/相
         * - 비교적 온전한 근
         * - 생부 존재
         * - 설기/재/관살 방향 없음
         */

        boolean cleanSupportingGround =
                groundStatus
                        == DayMasterGroundStatus
                        .ROOT_PRESENT

                        ||

                        groundStatus
                                == DayMasterGroundStatus
                                .ROOT_PRESENT_WITH_SUPPORTIVE_TRANSFORMATION;

        if (
                supportiveSeason
                        &&
                        cleanSupportingGround
                        &&
                        support
                        &&
                        !opposingDirection
        ) {

            return build(
                    dayMaster,
                    DayMasterStrengthStatus
                            .CLEARLY_STRONG,
                    DayMasterStrengthDecisionBasis
                            .SUPPORTIVE_SEASON_WITH_ROOT_AND_SUPPORT,
                    season,
                    ground,
                    force,
                    specialStructure
            );
        }

        /*
         * =================================================
         * 2. 강한 쪽으로 기우는 구조
         * =================================================
         *
         * 旺/相이고
         * 근 또는 생부가 존재하며
         * 뚜렷한 설기/재/관살 방향이 없음.
         */

        if (
                supportiveSeason
                        &&
                        !opposingDirection
                        &&
                        (
                                rootPresent
                                        ||
                                        support
                        )
        ) {

            return build(
                    dayMaster,
                    DayMasterStrengthStatus
                            .STRONG_LEANING,
                    DayMasterStrengthDecisionBasis
                            .SUPPORTIVE_SEASON_WITH_PARTIAL_FOUNDATION,
                    season,
                    ground,
                    force,
                    specialStructure
            );
        }

        /*
         * =================================================
         * 3. 명백한 약한 구조
         * =================================================
         *
         * 休/囚/死
         * +
         * 무근
         * +
         * 인성/비겁 생부 없음
         *
         * 실제 종격 여부는 이 Rule 이전에
         * 별도 Rule로 검토되어 있어야 한다.
         */

        if (
                !supportiveSeason
                        &&
                        !rootPresent
                        &&
                        !support
        ) {

            return build(
                    dayMaster,
                    DayMasterStrengthStatus
                            .CLEARLY_WEAK,
                    DayMasterStrengthDecisionBasis
                            .NON_SUPPORTIVE_SEASON_WITHOUT_ROOT_OR_SUPPORT,
                    season,
                    ground,
                    force,
                    specialStructure
            );
        }

        /*
         * =================================================
         * 4. 실령 + 무근 + 생부 존재
         * =================================================
         *
         * 약한 방향은 분명하지만
         * 생부가 남아 있으므로
         * 극약/진종으로 취급하지 않는다.
         */

        if (
                !supportiveSeason
                        &&
                        !rootPresent
                        &&
                        support
        ) {

            return build(
                    dayMaster,
                    DayMasterStrengthStatus
                            .WEAK_LEANING,
                    DayMasterStrengthDecisionBasis
                            .NON_SUPPORTIVE_SEASON_WITHOUT_ROOT_BUT_WITH_SUPPORT,
                    season,
                    ground,
                    force,
                    specialStructure
            );
        }

        /*
         * =================================================
         * 5. 실령 + 불안정 근 + 무생부 + 극설
         * =================================================
         */

        boolean vulnerableGround =
                groundStatus
                        == DayMasterGroundStatus
                        .ROOT_PRESENT_WITH_DISTURBANCE

                        ||

                        groundStatus
                                == DayMasterGroundStatus
                                .ROOT_PRESENT_WITH_NON_SUPPORTIVE_TRANSFORMATION

                        ||

                        groundStatus
                                == DayMasterGroundStatus
                                .MIXED_ROOT_CONTEXT;

        if (
                !supportiveSeason
                        &&
                        rootPresent
                        &&
                        vulnerableGround
                        &&
                        !support
                        &&
                        opposingDirection
        ) {

            return build(
                    dayMaster,
                    DayMasterStrengthStatus
                            .WEAK_LEANING,
                    DayMasterStrengthDecisionBasis
                            .NON_SUPPORTIVE_SEASON_WITH_VULNERABLE_ROOT,
                    season,
                    ground,
                    force,
                    specialStructure
            );
        }

        /*
         * =================================================
         * 6. 나머지 혼재 구조
         * =================================================
         *
         * 예:
         *
         * - 실령했지만 근과 생부가 모두 존재
         * - 旺/相이나 설기·재·관살도 존재
         * - 근이 충을 받으면서 생부도 존재
         * - 당령이나 무근
         *
         * 이런 경우 억지로 강/약을 선택하지 않는다.
         */

        return build(
                dayMaster,
                DayMasterStrengthStatus
                        .MIXED_NEEDS_REVIEW,
                DayMasterStrengthDecisionBasis
                        .MIXED_STRUCTURE,
                season,
                ground,
                force,
                specialStructure
        );
    }

    private DayMasterStrengthAnalysis build(

            HeavenlyStem dayMaster,

            DayMasterStrengthStatus status,

            DayMasterStrengthDecisionBasis basis,

            DayMasterSeasonAnalysis season,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis
                    specialStructure

    ) {

        return new DayMasterStrengthAnalysis(
                dayMaster,
                status,
                basis,
                season.seasonState(),
                ground.status(),
                force.visibleSupportPattern(),
                force.hasAnyGeneratingSupport(),
                force.hasAnySameElementSupport(),
                force.hasGeneratingOutflowStructure(),
                force.hasWealthDirectionStructure(),
                force.hasControlPressureStructure(),
                specialStructure.candidates()
        );
    }

    private void validateDayMaster(

            HeavenlyStem dayMaster,

            DayMasterSeasonAnalysis season,

            DayMasterGroundStatusAnalysis ground,

            DayMasterForceStatusAnalysis force,

            DayMasterSpecialStructureCandidateAnalysis
                    specialStructure

    ) {

        if (
                season.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "월령 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                ground.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득지 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                force.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "득세 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                specialStructure.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "특수구조 후보 분석의 일간이 일치하지 않습니다."
            );
        }
    }
}