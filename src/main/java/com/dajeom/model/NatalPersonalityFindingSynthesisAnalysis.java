package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisKind;
import com.dajeom.type.PersonalityFindingSynthesisType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Personality Finding Synthesis 전체 Aggregate.
 *
 * =================================================
 *
 * 구조:
 *
 * NatalPersonalityFindingAnalysis
 *
 * ↓
 *
 * NatalPersonalityFindingSynthesisAnalysis
 *
 * =================================================
 *
 * 여기에서는:
 *
 * 각 Finding을 따로 나열하는 것에서 끝내지 않고
 *
 * 서로 연결되는 구조를
 * 하나의 Synthesis로 보존한다.
 *
 * =================================================
 *
 * REFERENCE Synthesis도 삭제하지 않는다.
 *
 * 단:
 *
 * 직접적인 사용자 성향문장 후보에서는
 * 제외된다.
 */
public record NatalPersonalityFindingSynthesisAnalysis(

        NatalPersonalityFindingAnalysis findingAnalysis,

        List<NatalPersonalityFindingSynthesis> syntheses,

        boolean completeChart

) {

    public NatalPersonalityFindingSynthesisAnalysis {

        Objects.requireNonNull(
                findingAnalysis,
                "Personality Finding Analysis는 필수입니다."
        );

        syntheses =
                syntheses == null
                        ? List.of()
                        : List.copyOf(
                        syntheses
                );

        if (
                findingAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Finding Analysis와 "
                            + "Synthesis Analysis의 "
                            + "원국 완전성 상태가 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 동일 Synthesis 중복 생성 금지
         * =================================================
         */

        Set<PersonalityFindingSynthesisType> types =
                new HashSet<>();

        for (
                NatalPersonalityFindingSynthesis synthesis
                : syntheses
        ) {

            if (
                    synthesis.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Synthesis의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }

            if (
                    !types.add(
                            synthesis.synthesisType()
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 Personality Synthesis가 "
                                + "중복 생성되었습니다: "
                                + synthesis.synthesisType()
                );
            }
        }
    }

    public Optional<NatalPersonalityFindingSynthesis>
    synthesisOf(

            PersonalityFindingSynthesisType synthesisType

    ) {

        if (
                synthesisType == null
        ) {

            return Optional.empty();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis.synthesisType()
                                        == synthesisType
                )
                .findFirst();
    }

    /**
     * 사용자에게 직접 표현 가능한
     * Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    directlyAssertableSyntheses() {

        return syntheses
                .stream()
                .filter(
                        NatalPersonalityFindingSynthesis
                                ::userFacingCandidate
                )
                .toList();
    }

    /**
     * 가장 강한 Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    stronglyGroundedSyntheses() {

        return syntheses
                .stream()
                .filter(
                        NatalPersonalityFindingSynthesis
                                ::stronglyGrounded
                )
                .toList();
    }

    /**
     * 약하거나 조건적이어서
     * 내부에만 보존하는 Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    internalSyntheses() {

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                !synthesis
                                        .directAssertionAllowed()
                )
                .toList();
    }

    /**
     * 특정 기능축이 참여하는
     * 모든 Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    synthesesInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }

    /**
     * 구조적 결합 종류로 조회.
     */
    public List<NatalPersonalityFindingSynthesis>
    synthesesOfKind(

            PersonalityFindingSynthesisKind kind

    ) {

        if (
                kind == null
        ) {

            return List.of();
        }

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis.kind()
                                        == kind
                )
                .toList();
    }

    /**
     * 주격 중심 기능이 참여하는
     * Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    structurallyCenteredSyntheses() {

        return syntheses
                .stream()
                .filter(
                        NatalPersonalityFindingSynthesis
                                ::structurallyCentered
                )
                .toList();
    }

    /**
     * 조건/충돌 설명을
     * 같이 해야 하는 Synthesis.
     */
    public List<NatalPersonalityFindingSynthesis>
    qualifiedSyntheses() {

        return syntheses
                .stream()
                .filter(
                        NatalPersonalityFindingSynthesis
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * REFERENCE 단계만 조회.
     */
    public List<NatalPersonalityFindingSynthesis>
    referenceSyntheses() {

        return syntheses
                .stream()
                .filter(
                        synthesis ->
                                synthesis.groundingLevel()
                                        == PersonalityJudgmentGroundingLevel
                                        .REFERENCE
                )
                .toList();
    }
}