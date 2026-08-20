package com.dajeom.model;

import com.dajeom.type.DecadeCoreCapacitySide;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.Objects;

/**
 * 하나의 대운 Period에서
 * 하나의 Function Operation이
 * 원국의 고정된 자기기반과
 * 어떤 구조적 접점을 만드는지 보존한다.
 *
 * =================================================
 *
 * Source:
 *
 * NatalCoreSelfEffect
 *
 * +
 *
 * NatalTenGodFamilyEffect
 *
 * +
 *
 * DecadeFunctionOperation
 *
 * =================================================
 *
 * 여기서는 신강·신약을
 * 다시 계산하지 않는다.
 *
 * NatalCoreSelfEffect에 이미 보존된
 * 4차 최종 결과를 그대로 사용한다.
 *
 * =================================================
 *
 * 이 객체가 판단하는 것:
 *
 * 1.
 * 대운 입력이 SUPPORT_SIDE인지
 * PRESSURE_SIDE인지
 *
 * 2.
 * 원국이 strongSide / weakSide /
 * balancedZone인지
 *
 * 3.
 * 실제 직접 입력이 있는지
 *
 * 4.
 * Transformation에 의한
 * 대운 원기능 감소가 있는지
 *
 * 5.
 * 관계 간섭 Context가 있는지
 *
 * 6.
 * 원국 Cross Pattern 접촉 후보가 있는지
 *
 * =================================================
 *
 * 이 객체가 판단하지 않는 것:
 *
 * - 좋다 / 나쁘다
 * - 용신이다 / 기신이다
 * - 성공한다 / 실패한다
 * - 돈이 들어온다
 * - 직업운이 좋아진다
 *
 * =================================================
 *
 * 특히:
 *
 * weakSide + PRESSURE_SIDE
 *
 * 는 바로 흉이 아니라
 *
 * "기존 자기기반 대비 추가 압력 입력이므로
 * 수용여력 검토가 필요한 접점"
 *
 * 으로만 보존한다.
 *
 * strongSide + SUPPORT_SIDE 역시
 *
 * "기존 지원측에 같은 방향 입력이 추가되므로
 * 누적/편중 검토가 필요한 접점"
 *
 * 으로만 보존한다.
 */
public record DecadeFunctionCapacityContext(

        NatalCoreSelfEffect coreSelf,

        NatalTenGodFamilyEffect natalFunctionEffect,

        DecadeFunctionOperation operation,

        DecadeCoreCapacitySide capacitySide,

        boolean completeChart

) {

    public DecadeFunctionCapacityContext {

        Objects.requireNonNull(
                coreSelf,
                "DecadeFunctionCapacityContext에는 NatalCoreSelfEffect가 필요합니다."
        );

        Objects.requireNonNull(
                natalFunctionEffect,
                "DecadeFunctionCapacityContext에는 원국 기능축이 필요합니다."
        );

        Objects.requireNonNull(
                operation,
                "DecadeFunctionCapacityContext에는 DecadeFunctionOperation이 필요합니다."
        );

        Objects.requireNonNull(
                capacitySide,
                "DecadeFunctionCapacityContext에는 Capacity Side가 필요합니다."
        );

        /*
         * =================================================
         * Function 정합성
         * =================================================
         */

        if (
                natalFunctionEffect.functionType()
                        != operation.functionType()
        ) {

            throw new IllegalArgumentException(
                    "원국 기능축과 대운 Function Operation의 기능 종류가 일치하지 않습니다. "
                            + "natal="
                            + natalFunctionEffect.functionType()
                            + ", decade="
                            + operation.functionType()
            );
        }

        DecadeCoreCapacitySide expectedSide =
                DecadeCoreCapacitySide.from(
                        operation.functionType()
                );

        if (
                capacitySide
                        != expectedSide
        ) {

            throw new IllegalArgumentException(
                    "Function과 Capacity Side가 일치하지 않습니다. "
                            + "function="
                            + operation.functionType()
                            + ", expected="
                            + expectedSide
                            + ", actual="
                            + capacitySide
            );
        }

        /*
         * =================================================
         * completeChart 정합성
         * =================================================
         */

        if (
                coreSelf.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "CoreSelf와 Capacity Context의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                natalFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "원국 Function Effect와 Capacity Context의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                operation.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "대운 Function Operation과 Capacity Context의 completeChart가 일치하지 않습니다."
            );
        }
    }

    public int sequenceNumber() {

        return operation
                .sequenceNumber();
    }

    public NatalTenGodFunctionType functionType() {

        return operation
                .functionType();
    }

    /*
     * =================================================
     * 고정 Natal Core
     * =================================================
     */

    public DayMasterStrengthLevel natalStrengthLevel() {

        return coreSelf
                .strengthLevel();
    }

    public boolean natalStrongSide() {

        return coreSelf
                .strongSide();
    }

    public boolean natalWeakSide() {

        return coreSelf
                .weakSide();
    }

    public boolean natalBalancedZone() {

        return coreSelf
                .balancedZone();
    }

    public boolean natalExtreme() {

        return coreSelf
                .extreme();
    }

    /**
     * 종격 가능성 등을 다음 구조단계에서
     * 추가 검토해야 하는 원국인가.
     *
     * 이 상태에서는 단순 신강/신약 논리의
     * 해석 강도를 높이면 안 된다.
     */
    public boolean followStructureReviewRequired() {

        return coreSelf
                .followStructureReviewRequired();
    }

    /*
     * =================================================
     * Capacity Side
     * =================================================
     */

    public boolean supportSideInput() {

        return capacitySide
                .supportSide();
    }

    public boolean pressureSideInput() {

        return capacitySide
                .pressureSide();
    }

    /*
     * =================================================
     * 대운 직접 Input
     * =================================================
     */

    public boolean directlyEntered() {

        return operation
                .directlyEntered();
    }

    /**
     * 원국이 weak-side인 상태에서
     * 압력측 기능이 대운에 직접 들어오는 접점.
     *
     * 이것은 불리 판정이 아니다.
     *
     * 다음 단계에서:
     *
     * - 원기능 감소
     * - Transformation
     * - Cross Pattern
     * - 구조 Context
     *
     * 를 함께 봐야 한다.
     */
    public boolean weakSidePressureContact() {

        return directlyEntered()
                && natalWeakSide()
                && pressureSideInput();
    }

    /**
     * 원국이 strong-side인 상태에서
     * 지원측 기능이 추가로 들어오는 접점.
     *
     * 이것 역시 불리 판정이 아니다.
     *
     * 동일 방향 누적 여부를
     * 검토해야 한다는 뜻이다.
     */
    public boolean strongSideSupportContact() {

        return directlyEntered()
                && natalStrongSide()
                && supportSideInput();
    }

    /**
     * strong-side + pressure-side.
     *
     * 단순히 좋다고 판정하지 않는다.
     *
     * 압력 기능을 감당할 가능성을
     * 검토할 수 있는 구조적 접점일 뿐이다.
     */
    public boolean strongSidePressureContact() {

        return directlyEntered()
                && natalStrongSide()
                && pressureSideInput();
    }

    /**
     * weak-side + support-side.
     *
     * 단순히 희신/길로 취급하지 않는다.
     */
    public boolean weakSideSupportContact() {

        return directlyEntered()
                && natalWeakSide()
                && supportSideInput();
    }

    /*
     * =================================================
     * Balanced Zone
     * =================================================
     *
     * BALANCED_STRONG / BALANCED_WEAK은
     * strongSide/weakSide이면서
     * balancedZone에도 포함된다.
     *
     * 따라서 balancedZone을
     * 별도의 qualification으로 보존한다.
     */

    public boolean balancedZoneDirectContact() {

        return directlyEntered()
                && natalBalancedZone();
    }

    /*
     * =================================================
     * Transformation
     * =================================================
     */

    public boolean originalFunctionReductionExists() {

        return operation
                .originalFunctionReductionExists();
    }

    public boolean strongOriginalFunctionReductionExists() {

        return operation
                .strongOriginalFunctionReductionExists();
    }

    public boolean mixedOriginalFunctionCondition() {

        return operation
                .mixedOriginalFunctionCondition();
    }

    public boolean activeTransformationExists() {

        return operation
                .activeTransformationExists();
    }

    /*
     * =================================================
     * Relation
     * =================================================
     */

    public boolean restrictiveRelationContextExists() {

        return operation
                .restrictiveRelationContextExists();
    }

    /*
     * =================================================
     * Natal Cross Pattern
     * =================================================
     */

    public boolean natalCrossPatternContactCandidate() {

        return operation
                .natalCrossPatternContactCandidate();
    }

    /*
     * =================================================
     * Capacity Review
     * =================================================
     */

    /**
     * 원국 자기기반과
     * 대운 Input 방향이 같은 쪽으로
     * 추가 누적되는 구조적 접점.
     *
     * strong + support
     * 또는
     * weak + pressure
     *
     * 를 보존한다.
     *
     * 좋고 나쁨 판정이 아니다.
     */
    public boolean sameDirectionAccumulationReviewRequired() {

        return strongSideSupportContact()
                || weakSidePressureContact();
    }

    /**
     * 원국 자기기반과 반대쪽 기능이
     * 들어오는 접점.
     *
     * strong + pressure
     * weak + support
     *
     * 역시 자동으로 유리하다고 판정하지 않는다.
     */
    public boolean counterDirectionContact() {

        return strongSidePressureContact()
                || weakSideSupportContact();
    }

    /**
     * Capacity 해석에서
     * 단순한 strong/weak 방향만으로
     * 결론 내리면 안 되는 상태.
     */
    public boolean qualificationRequired() {

        return natalExtreme()
                || followStructureReviewRequired()
                || natalBalancedZone()
                || mixedOriginalFunctionCondition()
                || activeTransformationExists()
                || restrictiveRelationContextExists()
                || operation.mixedContext();
    }

    /**
     * 후속 실제 Functional Effect 판정에서
     * 반드시 검토해야 할 Context가 있는가.
     */
    public boolean capacityReviewRequired() {

        if (
                !directlyEntered()
        ) {

            return false;
        }

        return sameDirectionAccumulationReviewRequired()
                || qualificationRequired()
                || originalFunctionReductionExists()
                || natalCrossPatternContactCandidate();
    }
}