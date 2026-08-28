package com.dajeom.model;

import com.dajeom.type.NatalCrossFunctionalPatternType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityStatementFactType;
import com.dajeom.type.TenGodCorePatternType;

import java.util.List;
import java.util.Objects;

/**
 * 실제 성립한 하나의 전통 십성 Pattern을
 * 사용자 Narrative Meaning 경계로 변환한 객체.
 *
 * =================================================
 * 중요한 점
 * =================================================
 *
 * 이 객체는 단순:
 *
 * PersonalityFindingTheme
 *
 * 를 보고 만들어지지 않는다.
 *
 * 반드시:
 *
 * CROSS_PATTERN Statement Fact
 * ->
 * 정확히 하나의 Source Finding
 * ->
 * PersonalityFindingOriginType.CROSS_PATTERN
 * ->
 * NatalCrossFunctionalPatternType
 * ->
 * TenGodCorePatternType
 *
 * 연결이 모두 확인되어야 한다.
 *
 * =================================================
 * 따라서
 * =================================================
 *
 * 단순히:
 *
 * EXPRESSION
 * +
 * RESPONSIBILITY
 *
 * 가 같이 존재한다는 이유로
 * 상관견관 Meaning을 만들 수 없다.
 *
 * Java가 실제 HURTING_OFFICER_MEETS_DIRECT_OFFICER
 * Pattern을 계산한 경우에만 생성된다.
 */
public record NatalPersonalityFormalPatternMeaning(

        NatalPersonalityStatementFact sourceFact,

        NatalPersonalityFinding sourceFinding,

        NatalCrossFunctionalPatternType crossPatternType,

        NatalPersonalityFormalPatternMeaningPolicy policy,

        boolean strongExpressionAllowed,

        boolean qualificationRequired

) {

    public NatalPersonalityFormalPatternMeaning {

        Objects.requireNonNull(
                sourceFact,
                "Formal Pattern Meaning sourceFact는 필수입니다."
        );

        Objects.requireNonNull(
                sourceFinding,
                "Formal Pattern Meaning sourceFinding은 필수입니다."
        );

        Objects.requireNonNull(
                crossPatternType,
                "Formal Pattern Meaning crossPatternType은 필수입니다."
        );

        Objects.requireNonNull(
                policy,
                "Formal Pattern Meaning policy는 필수입니다."
        );

        /*
         * =================================================
         * Statement Fact Type
         * =================================================
         */

        if (
                sourceFact.factType()
                        != PersonalityStatementFactType.CROSS_PATTERN
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning은 CROSS_PATTERN Fact에서만 생성할 수 있습니다."
            );
        }

        /*
         * =================================================
         * 정확히 하나의 Source Finding
         * =================================================
         */

        if (
                sourceFact.sourceFindings().size()
                        != 1
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Statement Fact에는 정확히 하나의 Source Finding이 필요합니다."
            );
        }

        if (
                sourceFact.sourceFindings().get(0)
                        != sourceFinding
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning의 Source Finding이 Statement Fact의 공식 Source와 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Finding Origin
         * =================================================
         */

        if (
                sourceFinding.originType()
                        != PersonalityFindingOriginType.CROSS_PATTERN
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning의 Source Finding은 CROSS_PATTERN Origin이어야 합니다."
            );
        }

        /*
         * =================================================
         * 실제 Cross Pattern Type
         * =================================================
         */

        NatalCrossFunctionalPatternType canonicalCrossPattern =
                sourceFinding
                        .crossPatternType()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "CROSS_PATTERN Finding에 실제 Cross Pattern Type이 없습니다."
                                        )
                        );

        if (
                canonicalCrossPattern
                        != crossPatternType
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning의 Cross Pattern Type이 Source Finding과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Finding Theme도 실제 Pattern과 일치해야 한다.
         * =================================================
         */

        PersonalityFindingTheme expectedTheme =
                PersonalityFindingTheme.fromCrossPattern(
                        crossPatternType
                );

        if (
                sourceFinding.theme()
                        != expectedTheme
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Finding Theme이 실제 Cross Pattern Type과 일치하지 않습니다. expected="
                            + expectedTheme
                            + ", actual="
                            + sourceFinding.theme()
            );
        }

        if (
                sourceFact
                        .findingTheme()
                        .orElseThrow()
                        != expectedTheme
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Statement Fact Theme이 Source Pattern과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 원본 전통 Pattern Type 확인
         * =================================================
         */

        TenGodCorePatternType sourcePatternType =
                crossPatternType
                        .sourcePatternType();

        if (
                policy.patternType()
                        != sourcePatternType
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning Policy가 실제 TenGodCorePatternType과 일치하지 않습니다. expected="
                            + sourcePatternType
                            + ", actual="
                            + policy.patternType()
            );
        }

        /*
         * =================================================
         * User Facing
         * =================================================
         */

        if (
                !sourceFinding.userFacingCandidate()
                        ||
                        !sourceFact.userFacingCandidate()
        ) {

            throw new IllegalArgumentException(
                    "사용자 직접 출력이 허용되지 않은 Pattern으로 Formal Pattern Meaning을 만들 수 없습니다."
            );
        }

        /*
         * =================================================
         * Expression Boundary Identity
         * =================================================
         */

        if (
                strongExpressionAllowed
                        != sourceFact.strongUserFacingCandidate()
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning의 strongExpressionAllowed가 공식 Statement Fact와 일치하지 않습니다."
            );
        }

        if (
                qualificationRequired
                        != sourceFact.qualificationRequired()
        ) {

            throw new IllegalArgumentException(
                    "Formal Pattern Meaning의 qualificationRequired가 공식 Statement Fact와 일치하지 않습니다."
            );
        }
    }

    public TenGodCorePatternType patternType() {

        return crossPatternType
                .sourcePatternType();
    }

    public String patternCode() {

        return patternType()
                .name();
    }

    public String koreanName() {

        return patternType()
                .koreanName();
    }

    public String userMeaning() {

        return policy
                .userMeaning();
    }

    public List<String> forbiddenExtensions() {

        return policy
                .forbiddenExtensions();
    }

    public boolean completeChart() {

        return sourceFact
                .completeChart();
    }
}