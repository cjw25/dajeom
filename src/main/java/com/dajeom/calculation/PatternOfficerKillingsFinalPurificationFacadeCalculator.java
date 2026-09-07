package com.dajeom.calculation;

import com.dajeom.domain.PatternOfficerKillingsFinalPurificationAnalysis;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationInput;
import com.dajeom.domain.PatternOfficerKillingsFinalPurificationPreparation;
import com.dajeom.domain.PatternOfficerKillingsStandardPurificationSnapshot;
import com.dajeom.domain.PatternOfficerKillingsYangBladeNodeSnapshot;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 官殺混雜 최종 판정 호출부를 단순화하는 Facade.
 *
 * =================================================
 * 호출부
 * =================================================
 *
 * 기존 官殺 Rule Engine
 *
 *      ↓
 *
 * PatternOfficerKillingsStandardPurificationSnapshot
 *
 * 기존 羊刃合殺 Rule Engine
 *
 *      ↓
 *
 * PatternOfficerKillingsYangBladeNodeSnapshot
 *
 *      ↓
 *
 * 이 Facade
 *
 *      ↓
 *
 * PatternOfficerKillingsFinalPurificationAnalysis
 *
 * =================================================
 *
 * 실제 서비스나 MyeongriProfile 조립부에서는
 *
 * FinalPurificationInput
 * YangBladeCompletionAnalysis
 *
 * 등을 직접 조립할 필요가 없다.
 */
@Component
public class PatternOfficerKillingsFinalPurificationFacadeCalculator {

    private final PatternOfficerKillingsFinalPurificationPipelineCalculator
            pipelineCalculator;

    public PatternOfficerKillingsFinalPurificationFacadeCalculator(

            PatternOfficerKillingsFinalPurificationPipelineCalculator
                    pipelineCalculator

    ) {

        this.pipelineCalculator =
                Objects.requireNonNull(
                        pipelineCalculator,
                        "PatternOfficerKillingsFinalPurificationPipelineCalculator는 필수입니다."
                );
    }

    /**
     * 표준 官殺 정리 Snapshot과
     * 羊刃合殺 node Snapshot을 받아
     *
     * 중간 입력 + 최종 분석을 모두 반환한다.
     */
    public PatternOfficerKillingsFinalPurificationPreparation calculate(

            PatternOfficerKillingsStandardPurificationSnapshot standard,

            PatternOfficerKillingsYangBladeNodeSnapshot yangBlade

    ) {

        Objects.requireNonNull(
                standard,
                "PatternOfficerKillingsStandardPurificationSnapshot은 필수입니다."
        );

        Objects.requireNonNull(
                yangBlade,
                "PatternOfficerKillingsYangBladeNodeSnapshot은 필수입니다."
        );

        /*
         * =================================================
         * 1. 기본 상태 검증
         * =================================================
         */

        validateCombination(
                standard,
                yangBlade
        );

        /*
         * =================================================
         * 2. Final Input 조립
         * =================================================
         */

        PatternOfficerKillingsFinalPurificationInput input =
                new PatternOfficerKillingsFinalPurificationInput(

                        standard.mixed(),

                        standard.cleared(),

                        standard.reviewRequired(),

                        standard.retainedKillingNodes(),

                        yangBlade.resolvedKillingNodes(),

                        yangBlade.reviewKillingNodes()
                );

        /*
         * =================================================
         * 3. node-level completion + 최종 purification
         * =================================================
         */

        PatternOfficerKillingsFinalPurificationAnalysis analysis =
                pipelineCalculator
                        .calculate(
                                input
                        );

        /*
         * =================================================
         * 4. 추적 가능한 Preparation 반환
         * =================================================
         */

        return new PatternOfficerKillingsFinalPurificationPreparation(
                standard,
                yangBlade,
                input,
                analysis
        );
    }

    /**
     * 중간 결과가 필요 없고
     * 최종 官殺 판정만 필요한 호출부용.
     */
    public PatternOfficerKillingsFinalPurificationAnalysis calculateFinal(

            PatternOfficerKillingsStandardPurificationSnapshot standard,

            PatternOfficerKillingsYangBladeNodeSnapshot yangBlade

    ) {

        return calculate(
                standard,
                yangBlade
        ).analysis();
    }

    /*
     * =================================================
     * 羊刃合殺 검토가 없는 경우
     * =================================================
     *
     * resolved/review가 모두 없는 empty snapshot을 사용한다.
     */
    public PatternOfficerKillingsFinalPurificationAnalysis
    calculateWithoutYangBlade(

            PatternOfficerKillingsStandardPurificationSnapshot standard

    ) {

        Objects.requireNonNull(
                standard,
                "PatternOfficerKillingsStandardPurificationSnapshot은 필수입니다."
        );

        PatternOfficerKillingsYangBladeNodeSnapshot emptyYangBlade =
                new PatternOfficerKillingsYangBladeNodeSnapshot(
                        java.util.Set.of(),
                        java.util.Set.of()
                );

        return calculateFinal(
                standard,
                emptyYangBlade
        );
    }

    /*
     * =================================================
     * Snapshot 조합 검증
     * =================================================
     */

    private void validateCombination(

            PatternOfficerKillingsStandardPurificationSnapshot standard,

            PatternOfficerKillingsYangBladeNodeSnapshot yangBlade

    ) {

        /*
         * 官殺混雜 자체가 아닌데
         * 羊刃合殺 purification 결과가 존재할 수 없다.
         */
        if (
                !standard.mixed()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "官殺混雜이 아닌 경우 羊刃合殺 purification 결과를 사용할 수 없습니다."
            );
        }

        /*
         * 표준 단계에서 이미 전체 정리가 끝났는데
         * 후속 羊刃合殺 결과가 있으면 안 된다.
         */
        if (
                standard.cleared()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 정리가 이미 완료된 경우 후속 羊刃合殺 결과가 존재할 수 없습니다."
            );
        }

        /*
         * 표준 단계 자체가 REVIEW_REQUIRED라면
         * retained 대상이 확정되지 않았다고 보기 때문에
         * 후속 羊刃合殺 결과를 확정값으로 사용하지 않는다.
         */
        if (
                standard.reviewRequired()
                        &&
                        !yangBlade.empty()
        ) {

            throw new IllegalArgumentException(
                    "표준 官殺 purification이 REVIEW_REQUIRED인 상태에서는 羊刃合殺 결과를 확정값으로 결합할 수 없습니다."
            );
        }

        /*
         * resolved 七殺은 반드시
         * 표준 단계 이후 retained 七殺이어야 한다.
         */
        if (
                !standard
                        .retainedKillingNodes()
                        .containsAll(
                                yangBlade.resolvedKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 resolved 노드 중 retained 七殺이 아닌 노드가 존재합니다."
            );
        }

        /*
         * review 七殺 역시 retained 七殺이어야 한다.
         */
        if (
                !standard
                        .retainedKillingNodes()
                        .containsAll(
                                yangBlade.reviewKillingNodes()
                        )
        ) {

            throw new IllegalArgumentException(
                    "羊刃合殺 review 노드 중 retained 七殺이 아닌 노드가 존재합니다."
            );
        }
    }
}