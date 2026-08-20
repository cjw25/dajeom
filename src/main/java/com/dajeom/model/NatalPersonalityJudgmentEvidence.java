package com.dajeom.model;

import com.dajeom.type.PersonalityJudgmentEvidenceType;

import java.util.Objects;

/**
 * 기본성향 Deterministic Judgment에 사용된
 * 하나의 구조적 Evidence.
 *
 * =================================================
 *
 * sourceCode
 *
 * 원본 구조를 추적하기 위한 코드다.
 *
 * 예:
 *
 * SELF_ASSERTION:STRONG
 *
 * HURTING_OFFICER_MEETS_DIRECT_OFFICER
 *
 * RULES_AND_EXPRESSION
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * sourceCode 문자열을
 * 나중에 파싱해서 명리판정을 하면 안 된다.
 *
 * 실제 판정 로직은:
 *
 * evidenceType
 * strong
 * direct
 * qualified
 *
 * 같은 구조화 필드만 사용한다.
 */
public record NatalPersonalityJudgmentEvidence(

        PersonalityJudgmentEvidenceType evidenceType,

        String sourceCode,

        boolean strong,

        boolean direct,

        boolean qualified

) {

    public NatalPersonalityJudgmentEvidence {

        Objects.requireNonNull(
                evidenceType,
                "Personality Evidence 종류는 필수입니다."
        );

        Objects.requireNonNull(
                sourceCode,
                "Personality Evidence sourceCode는 필수입니다."
        );

        if (
                sourceCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Personality Evidence sourceCode는 "
                            + "비어 있을 수 없습니다."
            );
        }

        if (
                strong
                        && !direct
        ) {

            throw new IllegalArgumentException(
                    "강한 Evidence는 "
                            + "직접 사용할 수 있는 Evidence여야 합니다."
            );
        }
    }
}