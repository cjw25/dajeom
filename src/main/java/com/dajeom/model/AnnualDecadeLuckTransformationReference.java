package com.dajeom.model;

import com.dajeom.type.AnnualDecadeLuckRelationLayer;
import com.dajeom.type.FiveElement;
import com.dajeom.type.TransformationOriginalFunctionState;
import com.dajeom.type.TransformationTargetActivationState;

import java.util.Objects;

/**
 * 정밀화 14차-5.
 *
 * 대운과 세운 사이에서 발생하는
 * 합화 / 기능 변화 Reference.
 *
 * =================================================
 *
 * 기존 압축 구조:
 *
 * targetElementName
 * activated
 * originalFunctionReduced
 *
 * =================================================
 *
 * 보강:
 *
 * layer
 *
 * FiveElement targetElement
 *
 * TransformationTargetActivationState
 *
 * decadeOriginalFunctionState
 *
 * annualOriginalFunctionState
 *
 * =================================================
 *
 * 중요:
 *
 * 현재 14-5 계산 Rule은
 * 대운/세운 Participant 각각에 대해
 * 서로 다른 OriginalFunctionState를
 * 계산하지 않는다.
 *
 * 기존 Rule이 반환하는 것은
 * 대운·세운 Pair Candidate 전체에 대한
 * 하나의 OriginalFunctionState다.
 *
 * 따라서 현재 Analyzer에서는:
 *
 * decadeOriginalFunctionState
 *
 * annualOriginalFunctionState
 *
 * 양쪽에 기존 동일 State를 넣는다.
 *
 * =================================================
 *
 * 이것은 두 Participant의 상태가
 * 원리적으로 항상 같다는 뜻이 아니다.
 *
 * Representation을 먼저 분리해 두어
 * 향후 Rule이 Participant별 상태를 제공할 때
 * API를 다시 깨지 않도록 한다.
 *
 * =================================================
 *
 * 기존:
 *
 * targetElementName()
 * activated()
 * originalFunctionReduced()
 *
 * API는 호환 Helper로 유지한다.
 */
public record AnnualDecadeLuckTransformationReference(

        AnnualDecadeLuckRelationLayer layer,

        String transformationCode,

        String transformationName,

        FiveElement targetElement,

        TransformationTargetActivationState
        targetActivationState,

        TransformationOriginalFunctionState
        decadeOriginalFunctionState,

        TransformationOriginalFunctionState
        annualOriginalFunctionState

) {

    public AnnualDecadeLuckTransformationReference {

        Objects.requireNonNull(
                layer,
                "대운-세운 Transformation에는 Layer가 필요합니다."
        );

        if (
                transformationCode == null
                        || transformationCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 합화 Code가 필요합니다."
            );
        }

        transformationCode =
                transformationCode.trim();

        if (
                transformationName == null
                        || transformationName.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "대운-세운 합화 이름이 필요합니다."
            );
        }

        transformationName =
                transformationName.trim();

        Objects.requireNonNull(
                targetElement,
                "대운-세운 Transformation에는 목표 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetActivationState,
                "대운-세운 Transformation에는 목표 활성 상태가 필요합니다."
        );

        Objects.requireNonNull(
                decadeOriginalFunctionState,
                "대운 Participant의 원기능 상태가 필요합니다."
        );

        Objects.requireNonNull(
                annualOriginalFunctionState,
                "세운 Participant의 원기능 상태가 필요합니다."
        );
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    public boolean heavenlyStemTransformation() {

        return layer
                .heavenlyStem();
    }

    public boolean earthlyBranchTransformation() {

        return layer
                .earthlyBranch();
    }

    /*
     * =================================================
     * Target
     * =================================================
     */

    /**
     * 기존 String API 호환.
     *
     * 후속 판정에는 사용하지 않는다.
     */
    public String targetElementName() {

        return targetElement
                .getKoreanName();
    }

    public boolean hasTargetElement() {

        return true;
    }

    /**
     * 기존 boolean API 호환.
     */
    public boolean activated() {

        return targetActivationState
                .active();
    }

    public boolean strongTargetActivation() {

        return targetActivationState
                .strong();
    }

    /*
     * =================================================
     * Decade Participant
     * =================================================
     */

    public boolean decadeOriginalFunctionReduced() {

        return decadeOriginalFunctionState
                .reduced();
    }

    public boolean decadeOriginalFunctionStronglyReduced() {

        return decadeOriginalFunctionState
                .stronglyReduced();
    }

    /*
     * =================================================
     * Annual Participant
     * =================================================
     */

    public boolean annualOriginalFunctionReduced() {

        return annualOriginalFunctionState
                .reduced();
    }

    public boolean annualOriginalFunctionStronglyReduced() {

        return annualOriginalFunctionState
                .stronglyReduced();
    }

    /*
     * =================================================
     * Participant State
     * =================================================
     */

    /**
     * 향후 Participant별 Rule이 정밀화됐을 때
     * 양쪽 상태가 다른지 확인할 수 있도록 둔다.
     *
     * 현재 Analyzer에서는 동일 상태를 넣으므로
     * 일반적으로 false다.
     */
    public boolean mixedParticipantOriginalFunctionState() {

        return decadeOriginalFunctionState
                != annualOriginalFunctionState;
    }

    /*
     * =================================================
     * Compatibility
     * =================================================
     */

    /**
     * 기존 14-5에서 사용하던 broad helper.
     *
     * 대운 또는 세운 Participant 중
     * 어느 쪽이든 원기능 감소라면 true.
     *
     * 후속 Annual Functional Layer에서는
     * 세운 자체 상태를 볼 때 반드시:
     *
     * annualOriginalFunctionReduced()
     *
     * 를 사용해야 한다.
     *
     * 대운 기준선 제한을 볼 때는:
     *
     * decadeOriginalFunctionReduced()
     *
     * 를 사용한다.
     */
    public boolean originalFunctionReduced() {

        return decadeOriginalFunctionReduced()
                || annualOriginalFunctionReduced();
    }

    public boolean originalFunctionStronglyReduced() {

        return decadeOriginalFunctionStronglyReduced()
                || annualOriginalFunctionStronglyReduced();
    }

    public boolean functionalChange() {

        return activated()
                || originalFunctionReduced();
    }
}