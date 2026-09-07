package com.dajeom.rule;

import com.dajeom.domain.DayMasterRootInfluenceAssessment;
import com.dajeom.domain.DayMasterRootInfluenceState;
import com.dajeom.domain.DayMasterRootRelationSignals;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.FiveElementSeasonState;
import com.dajeom.domain.StemGroundHit;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 통근 지점에 걸린 합충형파해 Context를
 * 월령 계절 상태와 함께 분류한다.
 *
 * =================================================
 * 핵심 원칙
 * =================================================
 *
 * 1. 충 = 뿌리 삭제가 아니다.
 *
 * 2. 합 = 뿌리 강화가 아니다.
 *
 * 3. 旺/相 상태의 충과
 *    休/囚/死 상태의 충을
 *    같은 의미로 확정하지 않는다.
 *
 * 4. 합과 충/형/파/해가 겹치면
 *    하나의 관계로 축약하지 않는다.
 *
 * 5. 실제 신강/신약 판단은
 *    이 결과와 다른 원국 구조를
 *    함께 본 뒤 별도 단계에서 한다.
 *
 * 숫자 점수와 가중치는 없다.
 */
@Component
public class DayMasterRootInfluenceRule {

    public DayMasterRootInfluenceAssessment assess(

            StemGroundHit root,

            DayMasterRootRelationSignals signals,

            DayMasterSeasonAnalysis season

    ) {

        Objects.requireNonNull(
                root,
                "통근 지점은 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "지지 관계 신호는 필수입니다."
        );

        Objects.requireNonNull(
                season,
                "일간 계절 분석은 필수입니다."
        );

        if (
                root.targetStem()
                        != season.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "통근 대상 천간과 계절 분석의 일간이 일치하지 않습니다."
            );
        }

        FiveElementSeasonState seasonState =
                season.seasonState();

        DayMasterRootInfluenceState state =
                determineState(
                        signals,
                        seasonState
                );

        return new DayMasterRootInfluenceAssessment(
                root,
                signals,
                seasonState,
                state
        );
    }

    private DayMasterRootInfluenceState determineState(

            DayMasterRootRelationSignals signals,

            FiveElementSeasonState seasonState

    ) {

        /*
         * 관계가 전혀 없음.
         */
        if (!signals.hasAnyRelation()) {

            return DayMasterRootInfluenceState
                    .NO_DIRECT_RELATION;
        }

        /*
         * 합과 동시에 충/형/파/해가 존재.
         *
         * 합 하나로도,
         * 충 하나로도 축약하지 않는다.
         */
        if (
                signals
                        .mixedCombinationAndDisturbance()
        ) {

            return DayMasterRootInfluenceState
                    .MIXED_RELATION_CONTEXT;
        }

        /*
         * 충 + 다른 방해관계.
         */
        if (
                signals
                        .clashWithAdditionalDisturbance()
        ) {

            return DayMasterRootInfluenceState
                    .CLASH_WITH_ADDITIONAL_DISTURBANCE;
        }

        /*
         * 단독 충 Context.
         *
         * 월령 상태를 구분해 보존한다.
         *
         * 여기서 어느 쪽도
         * ROOT_DESTROYED로 바꾸지 않는다.
         */
        if (signals.hasClash()) {

            return switch (seasonState) {

                case WANG, XIANG ->
                        DayMasterRootInfluenceState
                                .CLASH_IN_WANG_OR_XIANG;

                case XIU, QIU, SI ->
                        DayMasterRootInfluenceState
                                .CLASH_IN_XIU_QIU_OR_SI;
            };
        }

        /*
         * 충 없이 합만 존재.
         */
        if (
                signals.hasCombination()
                        &&
                        !signals.hasDisturbingRelation()
        ) {

            return DayMasterRootInfluenceState
                    .COMBINATION_CONTEXT;
        }

        /*
         * 충은 없지만
         * 형 / 자형 / 파 / 해 존재.
         */
        if (
                signals.hasDisturbingRelation()
        ) {

            return DayMasterRootInfluenceState
                    .NON_CLASH_DISTURBANCE_CONTEXT;
        }

        return DayMasterRootInfluenceState
                .NO_DIRECT_RELATION;
    }
}