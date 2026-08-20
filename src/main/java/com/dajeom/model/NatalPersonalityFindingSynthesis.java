package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityFindingSynthesisKind;
import com.dajeom.type.PersonalityFindingSynthesisType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentEvidenceType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 여러 Personality Finding이
 * 하나의 구조로 연결된
 * Synthesis 하나.
 *
 * =================================================
 *
 * 예:
 *
 * EXPRESSION_STANDARD_CONFLICT
 *
 * +
 *
 * EXPRESSION_REGULATED_BY_PROCESSING
 *
 * ↓
 *
 * EXPRESSION_STANDARD_TENSION_WITH_PROCESSING_REGULATION
 *
 * =================================================
 *
 * 이 객체에서도:
 *
 * - 사용자 문장
 * - 심리 단정
 * - 사건 예측
 *
 * 을 만들지 않는다.
 *
 * =================================================
 *
 * structurallyCentered
 *
 * 참여 기능축 중 최소 하나가
 * 원국 주격의 중심 기능인지 나타낸다.
 *
 * 이것은 추가 점수가 아니다.
 *
 * =================================================
 *
 * qualificationRequired
 *
 * Source Finding 중:
 *
 * - QUALIFIED
 * - WEAKENED
 * - CONFLICTED
 *
 * 등의 조건이 있어
 * 후속 설명에서 같이 말해야 하는 경우다.
 */
public record NatalPersonalityFindingSynthesis(

        PersonalityFindingSynthesisType synthesisType,

        List<NatalPersonalityFinding> sourceFindings,

        List<NatalTenGodFunctionType> involvedFunctions,

        PersonalityJudgmentGroundingLevel groundingLevel,

        boolean directAssertionAllowed,

        boolean qualificationRequired,

        boolean structurallyCentered,

        boolean completeChart

) {

    public NatalPersonalityFindingSynthesis {

        Objects.requireNonNull(
                synthesisType,
                "Personality Synthesis Type은 필수입니다."
        );

        sourceFindings =
                sourceFindings == null
                        ? List.of()
                        : List.copyOf(
                        sourceFindings
                );

        involvedFunctions =
                involvedFunctions == null
                        ? List.of()
                        : List.copyOf(
                        involvedFunctions
                );

        Objects.requireNonNull(
                groundingLevel,
                "Personality Synthesis Grounding Level은 필수입니다."
        );

        /*
         * =================================================
         * 필요한 Finding이 정확히 들어왔는지 검증
         * =================================================
         */

        if (
                sourceFindings.size()
                        != synthesisType
                        .requiredThemes()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "Personality Synthesis의 "
                            + "Source Finding 수가 "
                            + "정의와 일치하지 않습니다: "
                            + synthesisType
            );
        }

        Set<PersonalityFindingTheme> actualThemes =
                EnumSet.noneOf(
                        PersonalityFindingTheme.class
                );

        for (
                NatalPersonalityFinding finding
                : sourceFindings
        ) {

            if (
                    finding.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Synthesis Source Finding의 "
                                + "원국 완전성 상태가 "
                                + "일치하지 않습니다."
                );
            }

            actualThemes.add(
                    finding.theme()
            );
        }

        Set<PersonalityFindingTheme> requiredThemes =
                EnumSet.noneOf(
                        PersonalityFindingTheme.class
                );

        requiredThemes.addAll(
                synthesisType.requiredThemes()
        );

        if (
                !actualThemes.equals(
                        requiredThemes
                )
        ) {

            throw new IllegalArgumentException(
                    "Personality Synthesis의 "
                            + "Source Finding Theme이 "
                            + "정의와 일치하지 않습니다: "
                            + synthesisType
            );
        }

        /*
         * =================================================
         * involvedFunctions 중복 금지
         * =================================================
         */

        Set<NatalTenGodFunctionType> uniqueFunctions =
                new LinkedHashSet<>();

        for (
                NatalTenGodFunctionType functionType
                : involvedFunctions
        ) {

            Objects.requireNonNull(
                    functionType,
                    "Synthesis 기능축에는 null을 사용할 수 없습니다."
            );

            if (
                    !uniqueFunctions.add(
                            functionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Synthesis 기능축이 중복되었습니다: "
                                + functionType
                );
            }
        }

        /*
         * =================================================
         * Direct Assertion 검증
         * =================================================
         */

        if (
                directAssertionAllowed

                        && !groundingLevel
                        .directAssertionAllowed()
        ) {

            throw new IllegalArgumentException(
                    "REFERENCE/CONTEXTUAL Synthesis를 "
                            + "직접 단정용으로 사용할 수 없습니다."
            );
        }

        /*
         * =================================================
         * STRONGLY_GROUNDED 검증
         *
         * 최소 하나의 Source Finding 자체가
         * strongly grounded여야 한다.
         * =================================================
         */

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED

                        && sourceFindings
                        .stream()
                        .noneMatch(
                                NatalPersonalityFinding
                                        ::stronglyGrounded
                        )
        ) {

            throw new IllegalArgumentException(
                    "STRONGLY_GROUNDED Synthesis에는 "
                            + "최소 하나의 강한 Source Finding이 "
                            + "필요합니다."
            );
        }
    }

    public PersonalityFindingSynthesisKind kind() {

        return synthesisType
                .kind();
    }

    /**
     * Synthesis에 사용된
     * 모든 Evidence Family.
     *
     * 같은 Family가 여러 Finding에서
     * 반복되어도 하나로 본다.
     */
    public Set<PersonalityJudgmentEvidenceType>
    independentEvidenceTypes() {

        Set<PersonalityJudgmentEvidenceType> result =
                EnumSet.noneOf(
                        PersonalityJudgmentEvidenceType.class
                );

        for (
                NatalPersonalityFinding finding
                : sourceFindings
        ) {

            for (
                    NatalPersonalityJudgmentEvidence evidence
                    : finding.evidences()
            ) {

                result.add(
                        evidence.evidenceType()
                );
            }
        }

        return Set.copyOf(
                result
        );
    }

    /**
     * Source Finding의 실제 개수가 아니라
     * 서로 독립적인 Evidence Family 개수.
     */
    public int independentEvidenceFamilyCount() {

        return independentEvidenceTypes()
                .size();
    }

    public boolean userFacingCandidate() {

        return directAssertionAllowed;
    }

    public boolean stronglyGrounded() {

        return groundingLevel
                == PersonalityJudgmentGroundingLevel
                .STRONGLY_GROUNDED;
    }
}