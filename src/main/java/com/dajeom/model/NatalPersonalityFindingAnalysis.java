package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 기본성향 Finding 최종 Aggregate.
 *
 * =================================================
 *
 * 여기에는:
 *
 * 강한 Finding
 * 중간 Finding
 * 약한 Reference Finding
 *
 * 이 전부 남는다.
 *
 * =================================================
 *
 * 사용자 출력용에서는:
 *
 * directlyAssertableFindings()
 *
 * 를 우선 사용하고,
 *
 * 내부 교차검증에서는:
 *
 * 전체 findings
 *
 * 를 사용한다.
 */
public record NatalPersonalityFindingAnalysis(

        NatalPersonalityDeterministicAnalysis
        deterministicAnalysis,

        List<NatalPersonalityFinding>
        findings,

        boolean completeChart

) {

    public NatalPersonalityFindingAnalysis {

        Objects.requireNonNull(
                deterministicAnalysis,
                "Personality Deterministic Analysis는 필수입니다."
        );

        findings =
                findings == null
                        ? List.of()
                        : List.copyOf(
                        findings
                );

        if (
                deterministicAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Deterministic Analysis와 "
                            + "Finding Analysis의 "
                            + "원국 완전성 상태가 "
                            + "일치하지 않습니다."
            );
        }

        for (
                NatalPersonalityFinding finding
                : findings
        ) {

            if (
                    finding.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Finding의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 특정 Theme Finding.
     *
     * Cross Pattern Theme는 원국당
     * 하나의 원본 Pattern만 존재하므로
     * 첫 번째를 반환한다.
     */
    public Optional<NatalPersonalityFinding>
    findingOf(

            PersonalityFindingTheme theme

    ) {

        if (
                theme == null
        ) {

            return Optional.empty();
        }

        return findings
                .stream()
                .filter(
                        finding ->
                                finding.theme()
                                        == theme
                )
                .findFirst();
    }

    /**
     * 특정 기능축에 관련된
     * 모든 Finding.
     */
    public List<NatalPersonalityFinding>
    findingsInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return findings
                .stream()
                .filter(
                        finding ->
                                finding
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }

    /**
     * 특정 상황 Finding.
     */
    public List<NatalPersonalityFinding>
    findingsOfSituation(

            NatalResponseSituationType situationType

    ) {

        if (
                situationType == null
        ) {

            return List.of();
        }

        return findings
                .stream()
                .filter(
                        finding ->
                                finding
                                        .situationType()
                                        .map(
                                                type ->
                                                        type
                                                                == situationType
                                        )
                                        .orElse(
                                                false
                                        )
                )
                .toList();
    }

    /**
     * 사용자에게 직접 설명할 수 있는
     * Finding.
     */
    public List<NatalPersonalityFinding>
    directlyAssertableFindings() {

        return findings
                .stream()
                .filter(
                        NatalPersonalityFinding
                                ::userFacingCandidate
                )
                .toList();
    }

    /**
     * 가장 강한 Finding.
     */
    public List<NatalPersonalityFinding>
    stronglyGroundedFindings() {

        return findings
                .stream()
                .filter(
                        NatalPersonalityFinding
                                ::stronglyGrounded
                )
                .toList();
    }

    /**
     * 강하게 말할 수 없지만
     * 내부 Evidence로 보존된 Finding.
     *
     * 여기에는:
     *
     * REFERENCE
     * CONTEXTUAL
     *
     * 이 포함될 수 있다.
     */
    public List<NatalPersonalityFinding>
    internalReferenceFindings() {

        return findings
                .stream()
                .filter(
                        NatalPersonalityFinding
                                ::internalReferenceOnly
                )
                .toList();
    }

    /**
     * 조건/상충을 함께 설명해야 하는 Finding.
     */
    public List<NatalPersonalityFinding>
    qualifiedFindings() {

        return findings
                .stream()
                .filter(
                        NatalPersonalityFinding
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * 특정 Origin으로 조회.
     */
    public List<NatalPersonalityFinding>
    findingsOfOrigin(

            PersonalityFindingOriginType originType

    ) {

        if (
                originType == null
        ) {

            return List.of();
        }

        return findings
                .stream()
                .filter(
                        finding ->
                                finding.originType()
                                        == originType
                )
                .toList();
    }

    /**
     * REFERENCE 단계만 조회.
     *
     * 약한 근거를 삭제하지 않았다는 것을
     * 명확히 확인할 수 있다.
     */
    public List<NatalPersonalityFinding>
    referenceFindings() {

        return findings
                .stream()
                .filter(
                        finding ->
                                finding.groundingLevel()
                                        == PersonalityJudgmentGroundingLevel
                                        .REFERENCE
                )
                .toList();
    }
}