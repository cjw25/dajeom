package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityJudgmentEvidenceType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityJudgmentKind;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 기본성향의 실제 Deterministic Judgment.
 *
 * =================================================
 *
 * 이 객체부터:
 *
 * "이 내용을 사용자에게
 * 어느 정도 강도로 말할 수 있는가"
 *
 * 가 결정된다.
 *
 * =================================================
 *
 * 하지만 아직도:
 *
 * - 사용자 문장
 * - 심리진단
 * - 사건 예측
 *
 * 은 만들지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * kind
 * =
 * FUNCTION
 *
 * functionType
 * =
 * EXPRESSION
 *
 * groundingLevel
 * =
 * GROUNDED
 *
 * directAssertionAllowed
 * =
 * true
 *
 * qualificationRequired
 * =
 * true
 *
 * =================================================
 *
 * 이것은:
 *
 * "표현 기능에 대한 직접 설명은 가능하지만
 * 충돌/중재 조건까지 같이 설명해야 한다"
 *
 * 는 의미다.
 */
public record NatalPersonalityDeterministicJudgment(

        PersonalityJudgmentKind kind,

        Optional<NatalTenGodFunctionType> functionType,

        Optional<NatalResponseSituationType> situationType,

        PersonalityJudgmentGroundingLevel groundingLevel,

        List<NatalPersonalityJudgmentEvidence> evidences,

        boolean directAssertionAllowed,

        boolean qualificationRequired,

        boolean completeChart

) {

    public NatalPersonalityDeterministicJudgment {

        Objects.requireNonNull(
                kind,
                "Personality Judgment 종류는 필수입니다."
        );

        functionType =
                functionType == null
                        ? Optional.empty()
                        : functionType;

        situationType =
                situationType == null
                        ? Optional.empty()
                        : situationType;

        Objects.requireNonNull(
                groundingLevel,
                "Personality Judgment 근거 수준은 필수입니다."
        );

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        /*
         * =================================================
         * Kind별 필드 검증
         * =================================================
         */

        switch (
                kind
        ) {

            case CORE_SELF,
                 INTERPERSONAL_CONTEXT -> {

                if (
                        functionType.isPresent()
                                || situationType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            kind
                                    + " Judgment에는 "
                                    + "functionType/situationType을 "
                                    + "사용하지 않습니다."
                    );
                }
            }

            case FUNCTION -> {

                if (
                        functionType.isEmpty()
                                || situationType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "FUNCTION Judgment에는 "
                                    + "functionType만 필요합니다."
                    );
                }
            }

            case SITUATION -> {

                if (
                        situationType.isEmpty()
                                || functionType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "SITUATION Judgment에는 "
                                    + "situationType만 필요합니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * 직접출력 가능 상태 검증
         * =================================================
         */

        if (
                directAssertionAllowed
                        && !groundingLevel
                        .directAssertionAllowed()
        ) {

            throw new IllegalArgumentException(
                    "REFERENCE/CONTEXTUAL Judgment를 "
                            + "직접 단정용으로 사용할 수 없습니다."
            );
        }

        /*
         * =================================================
         * 강한 Grounding에는
         * 실제 Strong Evidence 필요.
         * =================================================
         */

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED

                        && evidences
                        .stream()
                        .noneMatch(
                                NatalPersonalityJudgmentEvidence
                                        ::strong
                        )
        ) {

            throw new IllegalArgumentException(
                    "STRONGLY_GROUNDED Judgment에는 "
                            + "최소 하나의 강한 Evidence가 필요합니다."
            );
        }

        /*
         * =================================================
         * 동일 Evidence Family가 여러 Source를 가져도
         * 독립 Family 수는 중복 계산하지 않는다.
         * =================================================
         */

        Set<PersonalityJudgmentEvidenceType>
                independentTypes =
                EnumSet.noneOf(
                        PersonalityJudgmentEvidenceType.class
                );

        for (
                NatalPersonalityJudgmentEvidence evidence
                : evidences
        ) {

            independentTypes.add(
                    evidence.evidenceType()
            );
        }
    }

    /**
     * 서로 독립적인 계산계통 Evidence의 개수.
     *
     * Source 항목 개수가 아니라
     * Evidence Family 개수다.
     */
    public int independentEvidenceFamilyCount() {

        Set<PersonalityJudgmentEvidenceType> result =
                EnumSet.noneOf(
                        PersonalityJudgmentEvidenceType.class
                );

        for (
                NatalPersonalityJudgmentEvidence evidence
                : evidences
        ) {

            result.add(
                    evidence.evidenceType()
            );
        }

        return result.size();
    }

    /**
     * 특정 Evidence Family가 포함됐는지.
     */
    public boolean hasEvidenceType(

            PersonalityJudgmentEvidenceType evidenceType

    ) {

        if (
                evidenceType == null
        ) {

            return false;
        }

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.evidenceType()
                                        == evidenceType
                );
    }

    public boolean stronglyGrounded() {

        return groundingLevel
                .stronglyGrounded();
    }
}