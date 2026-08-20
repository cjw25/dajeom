package com.dajeom.model;

import com.dajeom.type.AnnualDecadeFunctionContactMode;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.Objects;

/**
 * 하나의 세운 × 특정 대운 Segment에서
 * 하나의 Functional Family가
 * 두 시간 Layer에 어떻게 나타나는지를 보존한다.
 *
 * =================================================
 *
 * Decade:
 *
 * DecadeResolvedFunctionEffect
 *
 * =================================================
 *
 * Annual:
 *
 * AnnualFunctionCapacityContext
 *
 * =================================================
 *
 * 이 단계의 핵심:
 *
 * 1.
 * 대운에 이미 활성된 Function인가.
 *
 * 2.
 * 세운에서도 활성되는가.
 *
 * 3.
 * 세운이 대운에 없던 Function을 새로 넣는가.
 *
 * 4.
 * 두 Layer에서 같은 Function이 겹치는가.
 *
 * 5.
 * Transformation Target이 포함되어 있는가.
 *
 * 6.
 * 고정 Natal Capacity상 추가 검토가 필요한가.
 *
 * =================================================
 *
 * 아직:
 *
 * - 14-5 Transformation Source Function 재배치
 * - CrossFunctional Pattern 최종 완성
 * - 길흉
 *
 * 은 하지 않는다.
 */
public record AnnualDecadeFunctionSegmentEffect(

        AnnualDecadeLuckInteraction interaction,

        DecadeResolvedFunctionEffect decadeFunctionEffect,

        AnnualFunctionCapacityContext annualFunctionContext,

        AnnualDecadeFunctionContactMode contactMode,

        boolean completeChart

) {

    public AnnualDecadeFunctionSegmentEffect {

        Objects.requireNonNull(
                interaction,
                "Annual-Decade Function Segment에는 Interaction이 필요합니다."
        );

        Objects.requireNonNull(
                decadeFunctionEffect,
                "Annual-Decade Function Segment에는 Decade Function Effect가 필요합니다."
        );

        Objects.requireNonNull(
                annualFunctionContext,
                "Annual-Decade Function Segment에는 Annual Function Capacity Context가 필요합니다."
        );

        Objects.requireNonNull(
                contactMode,
                "Annual-Decade Function Segment에는 Contact Mode가 필요합니다."
        );

        if (
                decadeFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Decade Function Effect와 Segment Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                annualFunctionContext.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Capacity와 Segment Effect의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                decadeFunctionEffect
                        .sequenceNumber()
                        != interaction
                        .decadeSequenceNumber()
        ) {

            throw new IllegalArgumentException(
                    "Decade Function Effect와 14-5 Interaction의 대운 Sequence가 일치하지 않습니다."
            );
        }

        if (
                annualFunctionContext.period()
                        != interaction.annualPeriod()
        ) {

            throw new IllegalArgumentException(
                    "Annual Function Capacity와 14-5 Interaction이 서로 다른 AnnualLuckPeriod를 참조합니다."
            );
        }

        if (
                decadeFunctionEffect.functionType()
                        != annualFunctionContext.functionType()
        ) {

            throw new IllegalArgumentException(
                    "대운 Function과 세운 Function이 동일 축이 아닙니다. "
                            + "decade="
                            + decadeFunctionEffect.functionType()
                            + ", annual="
                            + annualFunctionContext.functionType()
            );
        }

        AnnualDecadeFunctionContactMode expected =
                AnnualDecadeFunctionContactMode.of(
                        decadeFunctionEffect.active(),
                        annualFunctionContext.active()
                );

        if (
                contactMode != expected
        ) {

            throw new IllegalArgumentException(
                    "Annual-Decade Function Contact Mode가 실제 활성 상태와 일치하지 않습니다. "
                            + "function="
                            + decadeFunctionEffect.functionType()
                            + ", expected="
                            + expected
                            + ", actual="
                            + contactMode
            );
        }
    }

    public int targetYear() {

        return annualFunctionContext
                .targetYear();
    }

    public int decadeSequenceNumber() {

        return interaction
                .decadeSequenceNumber();
    }

    public NatalTenGodFunctionType functionType() {

        return annualFunctionContext
                .functionType();
    }

    /*
     * =================================================
     * Activation
     * =================================================
     */

    public boolean active() {

        return contactMode
                .active();
    }

    public boolean decadeActive() {

        return contactMode
                .decadeActive();
    }

    public boolean annualActive() {

        return contactMode
                .annualActive();
    }

    public boolean inactive() {

        return contactMode
                .inactive();
    }

    /**
     * 대운에는 존재하지만
     * 이번 세운 자체에서는
     * 같은 Function이 활성되지 않은 상태.
     */
    public boolean decadeBaselineOnly() {

        return contactMode
                .decadeOnly();
    }

    /**
     * 해당 대운 Functional 기준선에는 없었지만
     * 이번 세운에서 새로 활성되는 Function.
     */
    public boolean annualNewActivation() {

        return contactMode
                .annualOnly();
    }

    /**
     * 같은 Function이
     * 대운과 세운 양쪽에서 활성.
     */
    public boolean continuationContact() {

        return contactMode
                .bothActive();
    }

    /**
     * 동일 Function이
     * 대운 + 세운 두 시간 Layer에서
     * 함께 들어오는 구조.
     *
     * =================================================
     *
     * 이것은 "강화 확정"이 아니다.
     *
     * 후속 해석에서 누적/반복 접촉을
     * 검토해야 한다는 Candidate다.
     */
    public boolean sameFunctionAccumulationCandidate() {

        return continuationContact();
    }

    /*
     * =================================================
     * Decade Activation Source
     * =================================================
     */

    public boolean decadeOriginalInputActive() {

        return decadeFunctionEffect
                .originalInputActive();
    }

    public boolean decadeTransformedTargetActive() {

        return decadeFunctionEffect
                .transformedTargetActive();
    }

    /*
     * =================================================
     * Annual Activation Source
     * =================================================
     */

    public boolean annualDirectInputActive() {

        return annualFunctionContext
                .directInputActive();
    }

    public boolean annualTransformedTargetActive() {

        return annualFunctionContext
                .transformedTargetActive();
    }

    /*
     * =================================================
     * Transformation-mediated
     * =================================================
     */

    public boolean transformationMediatedContact() {

        return active()

                && (
                decadeTransformedTargetActive()

                        || annualTransformedTargetActive()
        );
    }

    /*
     * =================================================
     * Original Function Reduction
     * =================================================
     */

    public boolean decadeOriginalFunctionReductionExists() {

        return decadeFunctionEffect
                .originalInputReduced();
    }

    public boolean decadeOriginalFunctionStrongReductionExists() {

        return decadeFunctionEffect
                .originalInputStronglyReduced();
    }

    public boolean annualOriginalFunctionReductionExists() {

        return annualFunctionContext
                .annualOriginalFunctionReductionExists();
    }

    public boolean annualOriginalFunctionStrongReductionExists() {

        return annualFunctionContext
                .annualOriginalFunctionStrongReductionExists();
    }

    /*
     * =================================================
     * Natal Fixed Capacity
     * =================================================
     */

    /**
     * 같은 Function이 대운과 세운에서 함께 활성되고,
     * 양쪽 모두 원국 강약 방향상
     * 같은 방향 누적 검토 대상인 경우.
     *
     * 길흉이 아니다.
     */
    public boolean natalCapacityAccumulationReviewRequired() {

        return continuationContact()

                && decadeFunctionEffect
                .sameDirectionAccumulationContact()

                && annualFunctionContext
                .sameDirectionAccumulationContact();
    }

    public boolean decadeCapacityReviewRequired() {

        return decadeFunctionEffect
                .capacityReviewRequired();
    }

    public boolean annualCapacityReviewRequired() {

        return annualFunctionContext
                .capacityReviewRequired();
    }

    /*
     * =================================================
     * Relation Qualification
     * =================================================
     */

    public boolean decadeNatalRestrictiveRelationExists() {

        return decadeFunctionEffect
                .restrictiveRelationContextExists();
    }

    public boolean annualNatalRestrictiveRelationExists() {

        return annualFunctionContext
                .restrictiveNatalRelationContextExists();
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    public boolean qualificationRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return sameFunctionAccumulationCandidate()

                || transformationMediatedContact()

                || decadeOriginalFunctionReductionExists()

                || annualOriginalFunctionReductionExists()

                || decadeCapacityReviewRequired()

                || annualCapacityReviewRequired()

                || decadeNatalRestrictiveRelationExists()

                || annualNatalRestrictiveRelationExists();
    }
}