package com.dajeom.engine;

import com.dajeom.model.NatalConditionalResponsePattern;
import com.dajeom.model.NatalCrossFunctionalPattern;
import com.dajeom.model.NatalInterpersonalEffect;
import com.dajeom.model.NatalPersonalityDeterministicAnalysis;
import com.dajeom.model.NatalPersonalityDeterministicJudgment;
import com.dajeom.model.NatalPersonalityFunctionProfile;
import com.dajeom.model.NatalPersonalityJudgmentEvidence;
import com.dajeom.model.NatalPersonalityPatternAnalysis;
import com.dajeom.model.NatalPersonalitySituationProfile;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityJudgmentEvidenceType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityJudgmentKind;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 기본성향의 실제 Deterministic Judgment를 만든다.
 *
 * =================================================
 *
 * 목표:
 *
 * "근거가 있으면 똑부러지게 말하고,
 * 근거가 약하면 강한 단정을 하지 않는다."
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * 새로운 점수 없음.
 *
 * 2.
 * Evidence 단순 개수 세기 없음.
 *
 * 3.
 * 동일 계산계통의 여러 값은
 * 하나의 Evidence Family로 취급.
 *
 * 4.
 * 서로 다른 구조적 근거가
 * 같은 방향을 지지할 때만
 * Grounding Level을 올린다.
 *
 * 5.
 * QUALIFIED / WEAKENED / CONFLICTED
 * 근거는 삭제하지 않는다.
 *
 * qualificationRequired=true
 *
 * 로 보존한다.
 *
 * 6.
 * 출생시간 없는 불완전 원국에서는
 * STRONGLY_GROUNDED를
 * GROUNDED로 제한한다.
 *
 * 누락된 시주로 인한 과도한 단정을
 * 막기 위한 정책이다.
 */
@Component
public class NatalPersonalityDeterministicJudgmentEngine {

    public NatalPersonalityDeterministicAnalysis analyze(

            NatalPersonalityPatternAnalysis patternAnalysis

    ) {

        Objects.requireNonNull(
                patternAnalysis,
                "Personality Pattern Analysis는 필수입니다."
        );

        List<NatalPersonalityDeterministicJudgment> judgments =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * CoreSelf
         * =================================================
         */

        judgments.add(
                createCoreSelfJudgment(
                        patternAnalysis
                )
        );

        /*
         * =================================================
         * 2.
         * 십성 5대 기능축
         * =================================================
         */

        for (
                NatalPersonalityFunctionProfile profile
                : patternAnalysis.functionProfiles()
        ) {

            judgments.add(
                    createFunctionJudgment(
                            profile,
                            patternAnalysis.completeChart()
                    )
            );
        }

        /*
         * =================================================
         * 3.
         * 실제 Evidence가 있는 상황만
         * Judgment 생성
         *
         * Pattern이 없는 상황에
         * 억지 성격을 만들지 않는다.
         * =================================================
         */

        for (
                NatalPersonalitySituationProfile situationProfile
                : patternAnalysis.situationProfiles()
        ) {

            if (
                    !situationProfile.evidenced()
            ) {

                continue;
            }

            judgments.add(
                    createSituationJudgment(
                            situationProfile,
                            patternAnalysis.completeChart()
                    )
            );
        }

        /*
         * =================================================
         * 4.
         * 대인관계 원국 Context
         *
         * 관계 사실이 실제 존재할 때만 생성.
         *
         * 행동심리로 확대하지 않는다.
         * =================================================
         */

        NatalInterpersonalEffect interpersonal =
                patternAnalysis.interpersonal();

        if (
                hasInterpersonalEvidence(
                        interpersonal
                )
        ) {

            judgments.add(
                    createInterpersonalContextJudgment(
                            interpersonal,
                            patternAnalysis.completeChart()
                    )
            );
        }

        return new NatalPersonalityDeterministicAnalysis(

                patternAnalysis,

                judgments,

                patternAnalysis.completeChart()
        );
    }

    /**
     * =================================================
     * CoreSelf
     * =================================================
     *
     * 4차 신강·신약 자체는
     * 이미 Deterministic 계산결과이므로
     * GROUNDED로 보존한다.
     *
     * 단:
     *
     * 이것만 가지고
     *
     * 신강 = 고집
     * 신약 = 소심
     *
     * 같은 성격문장을 만들면 안 된다.
     */
    private NatalPersonalityDeterministicJudgment
    createCoreSelfJudgment(

            NatalPersonalityPatternAnalysis patternAnalysis

    ) {

        var coreSelf =
                patternAnalysis.coreSelf();

        NatalPersonalityJudgmentEvidence evidence =
                new NatalPersonalityJudgmentEvidence(

                        PersonalityJudgmentEvidenceType
                                .CORE_SELF,

                        "DAY_MASTER_STRENGTH:"
                                + coreSelf
                                .strengthLevel()
                                .name(),

                        coreSelf.extreme(),

                        true,

                        coreSelf
                                .followStructureReviewRequired()
                );

        return new NatalPersonalityDeterministicJudgment(

                PersonalityJudgmentKind
                        .CORE_SELF,

                Optional.empty(),

                Optional.empty(),

                PersonalityJudgmentGroundingLevel
                        .GROUNDED,

                List.of(
                        evidence
                ),

                true,

                coreSelf
                        .followStructureReviewRequired(),

                patternAnalysis
                        .completeChart()
        );
    }

    /**
     * =================================================
     * 십성 기능축 Judgment
     * =================================================
     */
    private NatalPersonalityDeterministicJudgment
    createFunctionJudgment(

            NatalPersonalityFunctionProfile profile,

            boolean completeChart

    ) {

        List<NatalPersonalityJudgmentEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * FUNCTION_ACTIVATION
         *
         * 같은 Family 안의 Power/Share/Pattern을
         * 여러 독립근거로 세지 않는다.
         * =================================================
         */

        if (
                profile.present()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .FUNCTION_ACTIVATION,

                            profile
                                    .functionType()
                                    .name()

                                    + ":"

                                    + profile
                                    .functionEffect()
                                    .activationLevel()
                                    .name(),

                            profile.strong(),

                            profile.meaningful(),

                            false
                    )
            );
        }

        /*
         * =================================================
         * STRUCTURAL_CENTER
         * =================================================
         */

        if (
                profile.structuralCenter()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .STRUCTURAL_CENTER,

                            "PRIMARY_STRUCTURE:"
                                    + profile
                                    .functionType()
                                    .name(),

                            true,

                            true,

                            false
                    )
            );
        }

        /*
         * =================================================
         * CROSS_FUNCTIONAL_PATTERN
         *
         * 여러 Pattern이 있어도
         * 같은 Evidence Family로 취급된다.
         * =================================================
         */

        for (
                NatalCrossFunctionalPattern pattern
                : profile.crossPatterns()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .CROSS_FUNCTIONAL_PATTERN,

                            pattern
                                    .patternType()
                                    .name(),

                            pattern.strongSourcePattern()
                                    && pattern.supported(),

                            pattern.structurallyEffective()
                                    && pattern.supported(),

                            pattern.structurallyEffective()

                                    && (
                                    pattern.qualified()

                                            || pattern.weakened()

                                            || pattern.conflicted()
                            )
                    )
            );
        }

        /*
         * =================================================
         * CONDITIONAL_RESPONSE
         * =================================================
         */

        for (
                NatalConditionalResponsePattern pattern
                : profile.conditionalPatterns()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .CONDITIONAL_RESPONSE,

                            pattern
                                    .patternType()
                                    .name(),

                            pattern
                                    .stronglyGrounded(),

                            pattern
                                    .directlyAssertable(),

                            pattern
                                    .requiresQualification()
                    )
            );
        }

        PersonalityJudgmentGroundingLevel grounding =
                determineFunctionGrounding(
                        profile,
                        completeChart
                );

        boolean qualificationRequired =
                profile
                        .requiresConditionalQualification();

        return new NatalPersonalityDeterministicJudgment(

                PersonalityJudgmentKind
                        .FUNCTION,

                Optional.of(
                        profile.functionType()
                ),

                Optional.empty(),

                grounding,

                evidences,

                grounding
                        .directAssertionAllowed(),

                qualificationRequired,

                completeChart
        );
    }

    /**
     * 기능축 Grounding.
     *
     * =================================================
     *
     * 새 점수 없이
     * 구조 조합으로만 판단한다.
     * =================================================
     */
    private PersonalityJudgmentGroundingLevel
    determineFunctionGrounding(

            NatalPersonalityFunctionProfile profile,

            boolean completeChart

    ) {

        /*
         * Family 자체도 없고
         * 실제 Cross Pattern도 없으면
         * 직접 판단하지 않는다.
         */
        if (
                !profile.present()

                        && !profile
                        .hasEffectiveCrossPattern()
        ) {

            return PersonalityJudgmentGroundingLevel
                    .REFERENCE;
        }

        /*
         * 가장 강한 경우.
         *
         * 기능 자체 STRONG
         *
         * +
         *
         * 주격 중심
         *
         * 또는
         *
         * 강한 상황 Pattern
         *
         * 또는
         *
         * 지원된 Cross Pattern
         */
        boolean stronglyGrounded =
                profile.strong()

                        && (
                        profile.structuralCenter()

                                || profile
                                .hasStrongConditionalResponse()

                                || profile
                                .hasSupportedCrossPattern()
                );

        if (
                stronglyGrounded
        ) {

            /*
             * 시주가 없으면
             * 강한 단정 한 단계를 제한한다.
             */
            return completeChart

                    ? PersonalityJudgmentGroundingLevel
                    .STRONGLY_GROUNDED

                    : PersonalityJudgmentGroundingLevel
                    .GROUNDED;
        }

        /*
         * FUNCTION 자체가 MEANINGFUL
         *
         * +
         *
         * 별도의 중심성 / 교차Pattern /
         * 상황Pattern 중 하나가 확인됨.
         */
        boolean grounded =
                profile.meaningful()

                        && (
                        profile.structuralCenter()

                                || profile
                                .hasSupportedCrossPattern()

                                || profile
                                .hasDirectConditionalResponse()
                );

        if (
                grounded
        ) {

            return PersonalityJudgmentGroundingLevel
                    .GROUNDED;
        }

        /*
         * 기능은 존재하나
         * 강한 독립 보강근거는 없는 상태.
         */
        return PersonalityJudgmentGroundingLevel
                .CONTEXTUAL;
    }

    /**
     * =================================================
     * 상황별 Judgment
     * =================================================
     */
    private NatalPersonalityDeterministicJudgment
    createSituationJudgment(

            NatalPersonalitySituationProfile profile,

            boolean completeChart

    ) {

        List<NatalPersonalityJudgmentEvidence> evidences =
                new ArrayList<>();

        for (
                NatalConditionalResponsePattern pattern
                : profile.patterns()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .CONDITIONAL_RESPONSE,

                            pattern
                                    .patternType()
                                    .name(),

                            pattern
                                    .stronglyGrounded(),

                            pattern
                                    .directlyAssertable(),

                            pattern
                                    .requiresQualification()
                    )
            );
        }

        PersonalityJudgmentGroundingLevel grounding;

        if (
                profile.stronglyGrounded()
        ) {

            grounding =
                    completeChart

                            ? PersonalityJudgmentGroundingLevel
                            .STRONGLY_GROUNDED

                            : PersonalityJudgmentGroundingLevel
                            .GROUNDED;

        } else if (
                profile.directlyAssertable()
        ) {

            grounding =
                    PersonalityJudgmentGroundingLevel
                            .GROUNDED;

        } else {

            grounding =
                    PersonalityJudgmentGroundingLevel
                            .CONTEXTUAL;
        }

        return new NatalPersonalityDeterministicJudgment(

                PersonalityJudgmentKind
                        .SITUATION,

                Optional.empty(),

                Optional.of(
                        profile.situationType()
                ),

                grounding,

                evidences,

                grounding
                        .directAssertionAllowed(),

                profile
                        .requiresQualification(),

                completeChart
        );
    }

    /**
     * =================================================
     * 대인 Context 존재 여부
     * =================================================
     */
    private boolean hasInterpersonalEvidence(

            NatalInterpersonalEffect interpersonal

    ) {

        return interpersonal
                .heavenlyStemRelationPairCount()
                > 0

                || interpersonal
                .earthlyBranchRelationPairCount()
                > 0

                || interpersonal
                .anyCombinationRelation()

                || interpersonal
                .activeTransformation()

                || interpersonal
                .meaningfulEarthlyBranchObstruction();
    }

    /**
     * =================================================
     * 대인관계 Context
     * =================================================
     *
     * 관계 자체로:
     *
     * - 사교적
     * - 내향적
     * - 사람을 못 믿음
     *
     * 등을 만들지 않는다.
     *
     * 아직 CONTEXTUAL로만 보존한다.
     */
    private NatalPersonalityDeterministicJudgment
    createInterpersonalContextJudgment(

            NatalInterpersonalEffect interpersonal,

            boolean completeChart

    ) {

        List<NatalPersonalityJudgmentEvidence> evidences =
                new ArrayList<>();

        evidences.add(
                new NatalPersonalityJudgmentEvidence(

                        PersonalityJudgmentEvidenceType
                                .INTERPERSONAL_CONTEXT,

                        "NATAL_RELATION_CONTEXT",

                        false,

                        false,

                        interpersonal
                                .mixedEarthlyBranchRelationPair()

                                || interpersonal
                                .severeEarthlyBranchObstruction()
                )
        );

        if (
                interpersonal.activeTransformation()
        ) {

            evidences.add(
                    new NatalPersonalityJudgmentEvidence(

                            PersonalityJudgmentEvidenceType
                                    .INTERPERSONAL_CONTEXT,

                            "ACTIVE_TRANSFORMATION",

                            false,

                            false,

                            interpersonal
                                    .originalFunctionReduction()
                    )
            );
        }

        return new NatalPersonalityDeterministicJudgment(

                PersonalityJudgmentKind
                        .INTERPERSONAL_CONTEXT,

                Optional.empty(),

                Optional.empty(),

                PersonalityJudgmentGroundingLevel
                        .CONTEXTUAL,

                evidences,

                false,

                interpersonal
                        .mixedEarthlyBranchRelationPair()

                        || interpersonal
                        .severeEarthlyBranchObstruction()

                        || interpersonal
                        .originalFunctionReduction(),

                completeChart
        );
    }
}