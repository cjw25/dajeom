package com.dajeom.model;

import com.dajeom.type.AnnualCoreCapacitySide;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.NatalTenGodFunctionType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 하나의 세운 Function이
 * 원국의 고정된 DayMaster Capacity와
 * 어떻게 접촉하는지를 보존한다.
 *
 * =================================================
 *
 * Source Function:
 *
 * 해당 Function의
 * AnnualFunctionMigration
 *
 * =================================================
 *
 * Incoming:
 *
 * 다른 Function의 Transformation으로부터
 * 현재 Function을 Target으로 활성화한
 * AnnualFunctionMigration 목록.
 *
 * =================================================
 *
 * 따라서:
 *
 * direct annual input 없음
 *
 * +
 *
 * transformation target 있음
 *
 * 이 경우도 실제 활성 Function으로
 * 취급할 수 있다.
 *
 * =================================================
 *
 * 중요:
 *
 * 일간 신강신약을 세운마다
 * 다시 계산하지 않는다.
 *
 * NatalCoreSelfEffect는 고정이다.
 */
public record AnnualFunctionCapacityContext(

        NatalCoreSelfEffect coreSelf,

        NatalTenGodFamilyEffect natalFunctionEffect,

        AnnualFunctionMigration sourceMigration,

        List<AnnualFunctionMigration> incomingMigrations,

        AnnualCoreCapacitySide capacitySide,

        boolean completeChart

) {

    public AnnualFunctionCapacityContext {

        /*
         * =================================================
         * Compact Constructor 주의
         * =================================================
         *
         * record compact constructor 본문이 실행되는 동안에는
         * record field의 최종 대입이 아직 완료되지 않았다.
         *
         * 따라서 이 생성자 안에서는:
         *
         * functionType()
         * period()
         * targetYear()
         *
         * 처럼 this.sourceMigration을 읽는
         * instance helper를 호출하면 안 된다.
         *
         * 반드시 생성자 매개변수 sourceMigration에서
         * 필요한 값을 직접 꺼내 사용한다.
         */

        Objects.requireNonNull(
                coreSelf,
                "Annual Function Capacity에는 Natal CoreSelf가 필요합니다."
        );

        Objects.requireNonNull(
                natalFunctionEffect,
                "Annual Function Capacity에는 Natal Function Effect가 필요합니다."
        );

        Objects.requireNonNull(
                sourceMigration,
                "Annual Function Capacity에는 Source Migration이 필요합니다."
        );

        incomingMigrations =
                incomingMigrations == null
                        ? List.of()
                        : List.copyOf(
                        incomingMigrations
                );

        Objects.requireNonNull(
                capacitySide,
                "Annual Function Capacity에는 Capacity Side가 필요합니다."
        );

        /*
         * =================================================
         * Constructor-local Source 값
         * =================================================
         *
         * compact constructor 안에서는
         * this.sourceMigration이 아니라
         * 매개변수 sourceMigration에서 직접 가져온다.
         */

        NatalTenGodFunctionType sourceFunctionType =
                sourceMigration
                        .sourceFunctionType();

        AnnualLuckPeriod sourcePeriod =
                sourceMigration
                        .period();

        if (
                coreSelf.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal CoreSelf와 Annual Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                natalFunctionEffect.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Natal Function Effect와 Annual Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                sourceMigration.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Annual Source Migration과 Capacity의 completeChart가 일치하지 않습니다."
            );
        }

        if (
                natalFunctionEffect.functionType()
                        != sourceFunctionType
        ) {

            throw new IllegalArgumentException(
                    "Natal Function Effect와 Annual Source Migration의 Function이 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Capacity Side
         * =================================================
         *
         * 기존:
         *
         * AnnualCoreCapacitySide.from(
         *     functionType()
         * )
         *
         * functionType()가 this.sourceMigration을 읽어서
         * compact constructor 실행 중 NPE가 발생했다.
         *
         * 수정:
         *
         * 생성자 지역변수 sourceFunctionType 사용.
         */

        AnnualCoreCapacitySide expectedSide =
                AnnualCoreCapacitySide.from(
                        sourceFunctionType
                );

        if (
                capacitySide
                        != expectedSide
        ) {

            throw new IllegalArgumentException(
                    "Annual Capacity Side가 Function 정의와 일치하지 않습니다. "
                            + "function="
                            + sourceFunctionType
                            + ", expected="
                            + expectedSide
                            + ", actual="
                            + capacitySide
            );
        }

        Set<AnnualFunctionMigration> seen =
                new LinkedHashSet<>();

        for (
                AnnualFunctionMigration incoming
                : incomingMigrations
        ) {

            Objects.requireNonNull(
                    incoming,
                    "Incoming Annual Migration은 null일 수 없습니다."
            );

            if (
                    incoming.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Incoming Annual Migration의 completeChart가 일치하지 않습니다."
                );
            }

            /*
             * =================================================
             * Period
             * =================================================
             *
             * 기존 period()는 this.sourceMigration을 읽었다.
             *
             * compact constructor 안에서는
             * sourcePeriod 지역값을 사용한다.
             */

            if (
                    incoming.period()
                            != sourcePeriod
            ) {

                throw new IllegalArgumentException(
                        "다른 AnnualLuckPeriod의 Migration이 Capacity Context에 연결되었습니다."
                );
            }

            /*
             * =================================================
             * Target Function
             * =================================================
             *
             * 기존 functionType() 대신
             * sourceFunctionType 지역값 사용.
             */

            if (
                    !incoming.targets(
                            sourceFunctionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 Function을 Target으로 하지 않는 Migration이 Incoming으로 연결되었습니다. "
                                + "targetFunction="
                                + sourceFunctionType
                                + ", sourceFunction="
                                + incoming.sourceFunctionType()
                );
            }

            if (
                    !seen.add(
                            incoming
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Incoming Annual Migration이 중복되었습니다."
                );
            }
        }
    }

    public int targetYear() {

        return sourceMigration
                .targetYear();
    }

    public AnnualLuckPeriod period() {

        return sourceMigration
                .period();
    }

    public NatalTenGodFunctionType functionType() {

        return sourceMigration
                .sourceFunctionType();
    }

    /*
     * =================================================
     * Activation Source
     * =================================================
     */

    public boolean directInputActive() {

        return sourceMigration
                .directlyEntered();
    }

    public boolean transformedTargetActive() {

        return !incomingMigrations
                .isEmpty();
    }

    public boolean active() {

        return directInputActive()
                || transformedTargetActive();
    }

    public boolean transformedTargetOnly() {

        return !directInputActive()
                && transformedTargetActive();
    }

    public boolean directAndTransformedTargetActive() {

        return directInputActive()
                && transformedTargetActive();
    }

    /*
     * =================================================
     * Transformation Target
     * =================================================
     */

    public List<AnnualFunctionMigrationTarget>
    incomingTargets() {

        return incomingMigrations
                .stream()
                .flatMap(
                        migration ->
                                migration
                                        .targetsTo(
                                                functionType()
                                        )
                                        .stream()
                )
                .toList();
    }

    /**
     * 동일 지지 Transformation이
     * 여러 Source Function에서 현재 Function으로
     * 연결되는 경우를 대비해
     * 실제 Transformation은 중복 제거한다.
     */
    public List<AnnualLuckNatalTransformationReference>
    distinctIncomingTransformations() {

        Set<AnnualLuckNatalTransformationReference> result =
                new LinkedHashSet<>();

        for (
                AnnualFunctionMigrationTarget target
                : incomingTargets()
        ) {

            result.add(
                    target.sourceTransformation()
            );
        }

        return List.copyOf(
                result
        );
    }

    public boolean strongTransformedTargetActive() {

        return distinctIncomingTransformations()
                .stream()
                .anyMatch(
                        AnnualLuckNatalTransformationReference
                                ::strongTargetActivation
                );
    }

    /*
     * =================================================
     * Original Annual Input
     * =================================================
     */

    public boolean annualOriginalFunctionReductionExists() {

        return sourceMigration
                .annualOriginalFunctionReductionExists();
    }

    public boolean annualOriginalFunctionStrongReductionExists() {

        return sourceMigration
                .annualOriginalFunctionStrongReductionExists();
    }

    public boolean mixedAnnualOriginalFunctionCondition() {

        return sourceMigration
                .mixedAnnualOriginalFunctionCondition();
    }

    /*
     * =================================================
     * Fixed Natal CoreSelf
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

    public boolean followStructureReviewRequired() {

        return coreSelf
                .followStructureReviewRequired();
    }

    /*
     * =================================================
     * Capacity Side
     * =================================================
     */

    public boolean supportSide() {

        return capacitySide
                .supportSide();
    }

    public boolean pressureSide() {

        return capacitySide
                .pressureSide();
    }

    /**
     * 신강측 원국에
     * support-side Function이
     * 이번 세운에서 실제 활성.
     *
     * 길흉 결론이 아니다.
     */
    public boolean strongSideSupportContact() {

        return active()
                && natalStrongSide()
                && supportSide();
    }

    /**
     * 신약측 원국에
     * pressure-side Function이
     * 이번 세운에서 실제 활성.
     */
    public boolean weakSidePressureContact() {

        return active()
                && natalWeakSide()
                && pressureSide();
    }

    public boolean strongSidePressureContact() {

        return active()
                && natalStrongSide()
                && pressureSide();
    }

    public boolean weakSideSupportContact() {

        return active()
                && natalWeakSide()
                && supportSide();
    }

    public boolean sameDirectionAccumulationContact() {

        return strongSideSupportContact()
                || weakSidePressureContact();
    }

    public boolean counterDirectionContact() {

        return strongSidePressureContact()
                || weakSideSupportContact();
    }

    /*
     * =================================================
     * Natal Structure
     * =================================================
     */

    public boolean natalStructuralCenter() {

        return natalFunctionEffect
                .primaryStructure();
    }

    /*
     * =================================================
     * Natal Relation
     * =================================================
     */

    public boolean restrictiveNatalRelationContextExists() {

        return sourceMigration
                .sourceEffect()
                .restrictiveRelationContextExists();
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

        return natalExtreme()

                || followStructureReviewRequired()

                || natalBalancedZone()

                || annualOriginalFunctionReductionExists()

                || mixedAnnualOriginalFunctionCondition()

                || transformedTargetActive()

                || restrictiveNatalRelationContextExists();
    }

    public boolean capacityReviewRequired() {

        if (
                !active()
        ) {

            return false;
        }

        return sameDirectionAccumulationContact()

                || natalExtreme()

                || followStructureReviewRequired()

                || annualOriginalFunctionReductionExists()

                || transformedTargetActive();
    }
}