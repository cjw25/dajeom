package com.dajeom.engine;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.CompanionCompositeAnalysis;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.NatalCoreSelfEffect;
import com.dajeom.model.NatalFunctionalAnalysis;
import com.dajeom.model.NatalInterpersonalEffect;
import com.dajeom.model.NatalStructuralCenterEffect;
import com.dajeom.model.NatalTenGodFamilyEffect;
import com.dajeom.model.OfficerCompositeAnalysis;
import com.dajeom.model.OutputCompositeAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.ResourceCompositeAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TenGodCompositeAnalysis;
import com.dajeom.model.WealthCompositeAnalysis;
import com.dajeom.type.NatalFunctionalActivationLevel;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 명리 분석을
 * Functional Effect로 정규화하는 Engine.
 *
 * =================================================
 *
 * 입력:
 *
 * NatalChartAdvancedAnalysis
 *
 * +
 *
 * TenGodCompositeAnalysis
 *
 * =================================================
 *
 * 출력:
 *
 * NatalFunctionalAnalysis
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 기존 3~10차 계산을 다시 하지 않는다.
 *
 * 2.
 * 새로운 세력 점수나 가중치를 만들지 않는다.
 *
 * 3.
 * 십성 활성 수준은
 * 10차 Composite가 이미 판정한:
 *
 * - present
 * - meaningful Pattern
 * - strong Pattern
 *
 * 을 그대로 사용한다.
 *
 * 4.
 * 격국은 추가 점수가 아니라
 * 중심성 Evidence로만 사용한다.
 *
 * 5.
 * 관계/합화는 존재와 기능 변화 사실만 보존하고:
 *
 * 합 = 길
 * 충 = 흉
 *
 * 식의 단정을 하지 않는다.
 *
 * =================================================
 *
 * 이 Engine이 만드는 결과는
 * 아직 최종 Personality가 아니다.
 *
 * 다음 단계에서:
 *
 * NatalFunctionalAnalysis
 *
 * ↓
 *
 * CrossFunctionalPattern
 *
 * ↓
 *
 * ConditionalResponsePattern
 *
 * ↓
 *
 * PERSONALITY
 *
 * 로 연결한다.
 */
@Component
public class NatalFunctionalAnalysisEngine {

    public NatalFunctionalAnalysis analyze(

            NatalChartAdvancedAnalysis
                    advancedAnalysis,

            TenGodCompositeAnalysis
                    tenGodCompositeAnalysis

    ) {

        Objects.requireNonNull(
                advancedAnalysis,
                "원국 고차 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodCompositeAnalysis,
                "십성 복합 분석 결과는 필수입니다."
        );

        validateConsistency(
                advancedAnalysis,
                tenGodCompositeAnalysis
        );

        StructureAnalysis structure =
                advancedAnalysis
                        .structureAnalysis();

        Optional<StructureTenGodType>
                primaryStandardTenGod =
                structure
                        .primaryStandardTenGod();

        /*
         * =================================================
         * CORE SELF
         * =================================================
         */
        NatalCoreSelfEffect coreSelf =
                createCoreSelf(
                        advancedAnalysis
                );

        /*
         * =================================================
         * 비겁
         *
         * SELF_ASSERTION
         * =================================================
         */
        NatalTenGodFamilyEffect selfAssertion =
                createCompanionEffect(
                        tenGodCompositeAnalysis
                                .companionAnalysis(),

                        primaryStandardTenGod
                );

        /*
         * =================================================
         * 식상
         *
         * EXPRESSION
         * =================================================
         */
        NatalTenGodFamilyEffect expression =
                createOutputEffect(
                        tenGodCompositeAnalysis
                                .outputAnalysis(),

                        primaryStandardTenGod
                );

        /*
         * =================================================
         * 재성
         *
         * RESOURCE_REALITY
         * =================================================
         */
        NatalTenGodFamilyEffect resourceReality =
                createWealthEffect(
                        tenGodCompositeAnalysis
                                .wealthAnalysis(),

                        primaryStandardTenGod
                );

        /*
         * =================================================
         * 관살
         *
         * RESPONSIBILITY
         * =================================================
         */
        NatalTenGodFamilyEffect responsibility =
                createOfficerEffect(
                        tenGodCompositeAnalysis
                                .officerAnalysis(),

                        primaryStandardTenGod
                );

        /*
         * =================================================
         * 인성
         *
         * PROCESSING
         * =================================================
         */
        NatalTenGodFamilyEffect processing =
                createResourceEffect(
                        tenGodCompositeAnalysis
                                .resourceAnalysis(),

                        primaryStandardTenGod
                );

        /*
         * =================================================
         * STRUCTURAL CENTER
         * =================================================
         */
        NatalStructuralCenterEffect structuralCenter =
                createStructuralCenter(
                        structure
                );

        /*
         * =================================================
         * INTERPERSONAL
         * =================================================
         */
        NatalInterpersonalEffect interpersonal =
                createInterpersonal(

                        advancedAnalysis
                                .relationAnalysis(),

                        advancedAnalysis
                                .combinationTransformationAnalysis()
                );

        boolean completeChart =
                advancedAnalysis
                        .hasHourPillar();

        /*
         * =================================================
         * 최종 Aggregate
         *
         * 10-9의 교차검증 Pattern도
         * 삭제하지 않고 그대로 보존한다.
         * =================================================
         */
        return new NatalFunctionalAnalysis(

                advancedAnalysis
                        .coreAnalysis()
                        .dayMaster(),

                coreSelf,

                List.of(
                        selfAssertion,
                        expression,
                        resourceReality,
                        responsibility,
                        processing
                ),

                structuralCenter,

                interpersonal,

                tenGodCompositeAnalysis
                        .orderedPatterns(),

                completeChart
        );
    }

    /**
     * 4차 신강·신약 결과를
     * Functional CoreSelf로 정규화한다.
     *
     * 원시 오행 세력을 다시 계산하지 않는다.
     */
    private NatalCoreSelfEffect createCoreSelf(
            NatalChartAdvancedAnalysis
                    advancedAnalysis
    ) {

        var strength =
                advancedAnalysis
                        .dayMasterStrengthAnalysis();

        return new NatalCoreSelfEffect(

                strength
                        .dayMaster(),

                strength
                        .dayMasterElement(),

                strength
                        .level(),

                strength
                        .strengthBasisPoints(),

                strength
                        .supportPower(),

                strength
                        .pressurePower(),

                strength
                        .directlyGainsCommand(),

                strength
                        .seasonallySupported(),

                strength
                        .grounded(),

                strength
                        .gainsForce(),

                strength
                        .gainsEstablishedForce(),

                strength
                        .extreme(),

                strength
                        .requiresFollowStructureReview(),

                strength
                        .completeChart()
        );
    }

    /**
     * 비견·겁재 Family.
     */
    private NatalTenGodFamilyEffect
    createCompanionEffect(

            CompanionCompositeAnalysis analysis,

            Optional<StructureTenGodType>
                    primaryStandardTenGod

    ) {

        List<String> meaningful =
                analysis
                        .meaningfulSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        List<String> strong =
                analysis
                        .strongSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        return createFamilyEffect(

                NatalTenGodFunctionType
                        .SELF_ASSERTION,

                analysis
                        .companionFamilyPower(),

                analysis
                        .familyShareBasisPoints(),

                analysis
                        .hasCompanionFamily(),

                meaningful,

                strong,

                primaryStandardTenGod,

                analysis
                        .completeChart()
        );
    }

    /**
     * 식신·상관 Family.
     */
    private NatalTenGodFamilyEffect
    createOutputEffect(

            OutputCompositeAnalysis analysis,

            Optional<StructureTenGodType>
                    primaryStandardTenGod

    ) {

        List<String> meaningful =
                analysis
                        .meaningfulSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        List<String> strong =
                analysis
                        .strongSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        return createFamilyEffect(

                NatalTenGodFunctionType
                        .EXPRESSION,

                analysis
                        .outputFamilyPower(),

                analysis
                        .outputFamilyShareBasisPoints(),

                analysis
                        .outputPresent(),

                meaningful,

                strong,

                primaryStandardTenGod,

                analysis
                        .completeChart()
        );
    }

    /**
     * 정재·편재 Family.
     */
    private NatalTenGodFamilyEffect
    createWealthEffect(

            WealthCompositeAnalysis analysis,

            Optional<StructureTenGodType>
                    primaryStandardTenGod

    ) {

        List<String> meaningful =
                analysis
                        .meaningfulSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        List<String> strong =
                analysis
                        .strongSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        return createFamilyEffect(

                NatalTenGodFunctionType
                        .RESOURCE_REALITY,

                analysis
                        .wealthFamilyPower(),

                analysis
                        .wealthFamilyShareBasisPoints(),

                analysis
                        .wealthPresent(),

                meaningful,

                strong,

                primaryStandardTenGod,

                analysis
                        .completeChart()
        );
    }

    /**
     * 정관·칠살 Family.
     */
    private NatalTenGodFamilyEffect
    createOfficerEffect(

            OfficerCompositeAnalysis analysis,

            Optional<StructureTenGodType>
                    primaryStandardTenGod

    ) {

        List<String> meaningful =
                analysis
                        .meaningfulSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        List<String> strong =
                analysis
                        .strongSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        return createFamilyEffect(

                NatalTenGodFunctionType
                        .RESPONSIBILITY,

                analysis
                        .officerFamilyPower(),

                analysis
                        .officerFamilyShareBasisPoints(),

                analysis
                        .officerPresent(),

                meaningful,

                strong,

                primaryStandardTenGod,

                analysis
                        .completeChart()
        );
    }

    /**
     * 정인·편인 Family.
     */
    private NatalTenGodFamilyEffect
    createResourceEffect(

            ResourceCompositeAnalysis analysis,

            Optional<StructureTenGodType>
                    primaryStandardTenGod

    ) {

        List<String> meaningful =
                analysis
                        .meaningfulSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        List<String> strong =
                analysis
                        .strongSignals()
                        .stream()
                        .map(
                                signal ->
                                        signal
                                                .patternType()
                                                .name()
                        )
                        .distinct()
                        .toList();

        return createFamilyEffect(

                NatalTenGodFunctionType
                        .PROCESSING,

                analysis
                        .resourceFamilyPower(),

                analysis
                        .resourceFamilyShareBasisPoints(),

                analysis
                        .resourcePresent(),

                meaningful,

                strong,

                primaryStandardTenGod,

                analysis
                        .completeChart()
        );
    }

    /**
     * 십성 Family 공통 변환.
     *
     * 새 threshold를 만들지 않는다.
     *
     * 활성 수준:
     *
     * Family 자체 없음
     * → ABSENT
     *
     * Family 존재
     * → PRESENT
     *
     * 기존 Composite의 Meaningful Pattern 존재
     * → MEANINGFUL
     *
     * 기존 Composite의 Strong Pattern 존재
     * → STRONG
     */
    private NatalTenGodFamilyEffect
    createFamilyEffect(

            NatalTenGodFunctionType functionType,

            int familyPower,

            int familyShareBasisPoints,

            boolean present,

            List<String> meaningfulPatternCodes,

            List<String> strongPatternCodes,

            Optional<StructureTenGodType>
                    primaryStandardTenGod,

            boolean completeChart

    ) {

        NatalFunctionalActivationLevel
                activationLevel =
                determineActivationLevel(

                        present,

                        meaningfulPatternCodes,

                        strongPatternCodes
                );

        Optional<StructureTenGodType>
                matchedPrimary =
                primaryStandardTenGod
                        .filter(
                                functionType::contains
                        );

        return new NatalTenGodFamilyEffect(

                functionType,

                activationLevel,

                familyPower,

                familyShareBasisPoints,

                matchedPrimary
                        .isPresent(),

                matchedPrimary,

                meaningfulPatternCodes,

                strongPatternCodes,

                completeChart
        );
    }

    private NatalFunctionalActivationLevel
    determineActivationLevel(

            boolean present,

            List<String> meaningfulPatternCodes,

            List<String> strongPatternCodes

    ) {

        if (
                !present
        ) {

            return NatalFunctionalActivationLevel
                    .ABSENT;
        }

        if (
                strongPatternCodes != null
                        && !strongPatternCodes.isEmpty()
        ) {

            return NatalFunctionalActivationLevel
                    .STRONG;
        }

        if (
                meaningfulPatternCodes != null
                        && !meaningfulPatternCodes.isEmpty()
        ) {

            return NatalFunctionalActivationLevel
                    .MEANINGFUL;
        }

        return NatalFunctionalActivationLevel
                .PRESENT;
    }

    /**
     * 5차 격국을 중심성 Evidence로 정규화.
     */
    private NatalStructuralCenterEffect
    createStructuralCenter(
            StructureAnalysis structure
    ) {

        return new NatalStructuralCenterEffect(

                structure
                        .status(),

                structure
                        .primaryNameKorean(),

                structure
                        .primaryStandardTenGod(),

                structure
                        .primarySpecialType(),

                structure
                        .primaryEstablished(),

                structure
                        .primaryBurdened(),

                structure
                        .primaryDamaged(),

                structure
                        .primaryStronglyDamaged(),

                structure
                        .primaryRelativelyPure(),

                structure
                        .primaryHighlyMixed(),

                structure
                        .requiresSpecialReview(),

                structure
                        .completeChart()
        );
    }

    /**
     * 8차 관계
     *
     * +
     *
     * 9차 합화
     *
     * 를 대인 기능 Evidence로 정규화한다.
     */
    private NatalInterpersonalEffect
    createInterpersonal(

            RelationAnalysis relation,

            CombinationTransformationAnalysis
                    transformation

    ) {

        return new NatalInterpersonalEffect(

                relation
                        .heavenlyStemRelationPairCount(),

                relation
                        .earthlyBranchRelationPairCount(),

                relation
                        .dayMasterInStemRelation(),

                relation
                        .dayBranchInRelation(),

                relation
                        .dayBranchRelationPairCount(),

                relation
                        .hasMixedEarthlyBranchRelationPair(),

                transformation
                        .hasAnyCombinationRelation(),

                transformation
                        .hasActiveTransformation(),

                transformation
                        .hasStrongTransformation(),

                transformation
                        .hasOriginalFunctionReduction(),

                transformation
                        .dayMasterInCombination(),

                transformation
                        .dayMasterFunctionReduced(),

                transformation
                        .dayBranchInCombination(),

                transformation
                        .hasMeaningfulEarthlyBranchObstruction(),

                transformation
                        .hasSevereEarthlyBranchObstruction(),

                transformation
                        .completeChart()
        );
    }

    /**
     * 모든 upstream 결과가
     * 정확히 같은 원국 분석에서 만들어졌는지 확인한다.
     *
     * 같은 내용을 두 번 계산한
     * 서로 다른 객체를 섞지 않는다.
     */
    private void validateConsistency(

            NatalChartAdvancedAnalysis
                    advancedAnalysis,

            TenGodCompositeAnalysis
                    tenGodCompositeAnalysis

    ) {

        if (
                advancedAnalysis
                        .coreAnalysis()
                        .dayMaster()

                        != tenGodCompositeAnalysis
                        .dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "원국 고차 분석과 십성 복합 분석의 "
                            + "일간이 일치하지 않습니다."
            );
        }

        if (
                advancedAnalysis
                        .dayMasterStrengthAnalysis()

                        != tenGodCompositeAnalysis
                        .dayMasterStrengthAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis는 "
                            + "동일한 4차 신강·신약 분석을 사용해야 합니다."
            );
        }

        if (
                advancedAnalysis
                        .structureAnalysis()

                        != tenGodCompositeAnalysis
                        .structureAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis는 "
                            + "동일한 5차 격국 분석을 사용해야 합니다."
            );
        }

        if (
                advancedAnalysis
                        .usefulGodAnalysis()

                        != tenGodCompositeAnalysis
                        .usefulGodAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis는 "
                            + "동일한 7차 용신 분석을 사용해야 합니다."
            );
        }

        if (
                advancedAnalysis
                        .combinationTransformationAnalysis()

                        != tenGodCompositeAnalysis
                        .combinationTransformationAnalysis()
        ) {

            throw new IllegalArgumentException(
                    "원국 Functional Analysis는 "
                            + "동일한 9차 합화 분석을 사용해야 합니다."
            );
        }

        if (
                advancedAnalysis
                        .hasHourPillar()

                        != tenGodCompositeAnalysis
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "원국 고차 분석과 십성 복합 분석의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}