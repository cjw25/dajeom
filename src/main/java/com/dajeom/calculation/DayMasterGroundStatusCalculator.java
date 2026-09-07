package com.dajeom.calculation;

import com.dajeom.domain.DayMasterEffectiveRootAnalysis;
import com.dajeom.domain.DayMasterEffectiveRootContext;
import com.dajeom.domain.DayMasterGroundStatus;
import com.dajeom.domain.DayMasterGroundStatusAnalysis;
import com.dajeom.domain.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 지지 뿌리 구조를
 * 得地 영역의 명리 Context로 분류한다.
 *
 * =================================================
 *
 * 하지 않는 것:
 *
 * - 득지 점수
 * - 근 점수
 * - 월지 몇 점
 * - 일지 몇 점
 * - 뿌리 개수 비교
 * - 신강/신약 판정
 *
 * =================================================
 *
 * 근의 존재와 현재 원국 Context를
 * 종류별로만 정리한다.
 */
@Component
public class DayMasterGroundStatusCalculator {

    public DayMasterGroundStatusAnalysis analyze(

            HeavenlyStem dayMaster,

            DayMasterEffectiveRootAnalysis effectiveRoots

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                effectiveRoots,
                "일간 유효 통근 분석은 필수입니다."
        );

        if (
                effectiveRoots.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "통근 분석의 일간이 대상 일간과 일치하지 않습니다."
            );
        }

        List<DayMasterEffectiveRootContext> allRoots =
                effectiveRoots
                        .allRoots();

        /*
         * 아예 근 Context가 없음.
         */
        if (
                allRoots.isEmpty()
        ) {

            return new DayMasterGroundStatusAnalysis(
                    dayMaster,
                    DayMasterGroundStatus.NO_ROOT,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of()
            );
        }

        /*
         * 관계 간섭도 없고
         * 합화 작용도 없는 원래 근.
         */
        List<DayMasterEffectiveRootContext> plainRoots =
                allRoots
                        .stream()
                        .filter(
                                root ->
                                        !root.hasDisturbanceContext()
                        )
                        .filter(
                                root ->
                                        !root.hasTransformationEffect()
                        )
                        .toList();

        /*
         * 충 / 형 / 파 / 해 등
         * 관계 Context가 걸린 근.
         *
         * 합화 여부와 독립적으로 보존한다.
         */
        List<DayMasterEffectiveRootContext> disturbedRoots =
                allRoots
                        .stream()
                        .filter(
                                DayMasterEffectiveRootContext
                                        ::hasDisturbanceContext
                        )
                        .toList();

        /*
         * ACCEPTED 합화 작용이
         * 일간을 생하거나 동류로 작용.
         */
        List<DayMasterEffectiveRootContext>
                supportiveTransformationRoots =
                allRoots
                        .stream()
                        .filter(
                                DayMasterEffectiveRootContext
                                        ::hasTransformationEffect
                        )
                        .filter(
                                DayMasterEffectiveRootContext
                                        ::transformationSupportsDayMaster
                        )
                        .toList();

        /*
         * ACCEPTED 합화 작용은 존재하지만
         * 일간을 생하거나 동류로 작용하지 않음.
         *
         * 정확히 어떤 관계인지는
         * 각 Context의 condition /
         * transformationRelation에 그대로 남아 있다.
         */
        List<DayMasterEffectiveRootContext>
                nonSupportiveTransformationRoots =
                allRoots
                        .stream()
                        .filter(
                                DayMasterEffectiveRootContext
                                        ::hasTransformationEffect
                        )
                        .filter(
                                root ->
                                        !root
                                                .transformationSupportsDayMaster()
                        )
                        .toList();

        DayMasterGroundStatus status =
                determineStatus(
                        plainRoots,
                        disturbedRoots,
                        supportiveTransformationRoots,
                        nonSupportiveTransformationRoots
                );

        return new DayMasterGroundStatusAnalysis(
                dayMaster,
                status,
                allRoots,
                plainRoots,
                disturbedRoots,
                supportiveTransformationRoots,
                nonSupportiveTransformationRoots
        );
    }

    private DayMasterGroundStatus determineStatus(

            List<DayMasterEffectiveRootContext> plainRoots,

            List<DayMasterEffectiveRootContext> disturbedRoots,

            List<DayMasterEffectiveRootContext>
                    supportiveTransformationRoots,

            List<DayMasterEffectiveRootContext>
                    nonSupportiveTransformationRoots

    ) {

        boolean plain =
                !plainRoots.isEmpty();

        boolean disturbed =
                !disturbedRoots.isEmpty();

        boolean supportiveTransformation =
                !supportiveTransformationRoots
                        .isEmpty();

        boolean nonSupportiveTransformation =
                !nonSupportiveTransformationRoots
                        .isEmpty();

        /*
         * 여기서 개수를 비교하지 않는다.
         *
         * 몇 개가 더 많은가가 아니라
         * 서로 다른 종류의 Context가
         * 동시에 존재하는가만 확인한다.
         */

        boolean mixed =
                (
                        plain
                                &&
                                (
                                        disturbed
                                                ||
                                                supportiveTransformation
                                                ||
                                                nonSupportiveTransformation
                                )
                )

                        ||

                        (
                                disturbed
                                        &&
                                        (
                                                supportiveTransformation
                                                        ||
                                                        nonSupportiveTransformation
                                        )
                        )

                        ||

                        (
                                supportiveTransformation
                                        &&
                                        nonSupportiveTransformation
                        );

        if (mixed) {

            return DayMasterGroundStatus
                    .MIXED_ROOT_CONTEXT;
        }

        if (
                supportiveTransformation
        ) {

            return DayMasterGroundStatus
                    .ROOT_PRESENT_WITH_SUPPORTIVE_TRANSFORMATION;
        }

        if (
                nonSupportiveTransformation
        ) {

            return DayMasterGroundStatus
                    .ROOT_PRESENT_WITH_NON_SUPPORTIVE_TRANSFORMATION;
        }

        if (disturbed) {

            return DayMasterGroundStatus
                    .ROOT_PRESENT_WITH_DISTURBANCE;
        }

        return DayMasterGroundStatus
                .ROOT_PRESENT;
    }
}