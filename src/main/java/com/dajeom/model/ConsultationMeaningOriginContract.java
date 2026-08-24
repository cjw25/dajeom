package com.dajeom.model;

import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;

import java.util.Locale;
import java.util.Objects;

/**
 * Meaning Claim의 출처와 공식 근거 권한을 고정하는 Contract.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * EXACT_TEN_GOD
 * FORMAL_PATTERN
 * DETERMINISTIC_RULE
 * TIMELINE_CONTRACT
 * NATAL_CONTEXT
 *
 * 를 같은 종류의 의미처럼 섞지 않는다.
 *
 * 이 Contract는 새로운 명리 의미를 계산하지 않는다.
 * 이미 생성되는 Claim의 sourceCode / specificity를 이용해
 * 공식 Origin만 분류한다.
 *
 * =================================================
 * Specificity Contract
 * =================================================
 *
 * Origin이 확정되면
 * 해당 Origin이 허용하는 Specificity도 동시에 고정된다.
 *
 * 예:
 *
 * EXACT_TEN_GOD
 * -> TIMING 금지
 * -> DOMAIN 금지
 *
 * NATAL_CONTEXT
 * -> TIMING 금지
 * -> DOMAIN 금지
 *
 * TIMELINE_CONTRACT
 * -> TIMING만 허용
 */
public record ConsultationMeaningOriginContract(

        ConsultationMeaningOrigin origin,

        ConsultationMeaningAuthority authority

) {

    public ConsultationMeaningOriginContract {

        Objects.requireNonNull(
                origin,
                "Meaning Origin은 필수입니다."
        );

        Objects.requireNonNull(
                authority,
                "Meaning Authority는 필수입니다."
        );

        ConsultationMeaningAuthority expected =
                expectedAuthority(
                        origin
                );

        if (
                authority != expected
        ) {

            throw new IllegalArgumentException(
                    "Meaning Origin과 Authority가 일치하지 않습니다. origin="
                            + origin
                            + ", authority="
                            + authority
                            + ", expected="
                            + expected
            );
        }
    }

    /**
     * 기존 NarrativePlan 생성 코드를 깨뜨리지 않으면서
     * Claim 생성 순간 Origin Contract를 결정한다.
     */
    public static ConsultationMeaningOriginContract resolve(

            String sourceCode,

            ConsultationMeaningSpecificity specificity

    ) {

        String normalizedSourceCode =
                requireText(
                        sourceCode,
                        "Meaning Origin 판정에는 sourceCode가 필요합니다."
                )
                        .toUpperCase(
                                Locale.ROOT
                        );

        Objects.requireNonNull(
                specificity,
                "Meaning Origin 판정에는 specificity가 필요합니다."
        );

        /*
         * =================================================
         * Timeline은 specificity가 공식 경계다.
         * =================================================
         */

        if (
                specificity.temporal()
        ) {

            return timelineContract();
        }

        /*
         * =================================================
         * 정확한 10십성
         * =================================================
         */

        if (
                normalizedSourceCode.startsWith(
                        "PERSONALITY_EXACT_TEN_GOD_"
                )
        ) {

            return exactTenGod();
        }

        /*
         * =================================================
         * 프로젝트 2차 Synthesis가 다시 Claim으로 유입되는 것을
         * 여기서도 차단한다.
         * =================================================
         */

        if (
                normalizedSourceCode.startsWith(
                        "PERSONALITY_SYNTHESIS"
                )
        ) {

            throw new IllegalArgumentException(
                    "프로젝트 2차 Personality Synthesis는 Meaning Claim Origin으로 사용할 수 없습니다: "
                            + sourceCode
            );
        }

        /*
         * =================================================
         * Personality Finding
         * =================================================
         *
         * 기능축 자체 / CoreSelf / 대인 Context는
         * 전통 Pattern으로 승격하지 않고 NATAL_CONTEXT로 둔다.
         *
         * 그 외 Personality Finding은
         * 현재 공식 실행선에서 성립 확인된 독립 Pattern으로 취급한다.
         */

        if (
                normalizedSourceCode.startsWith(
                        "PERSONALITY_FINDING_"
                )
        ) {

            if (
                    normalizedSourceCode.endsWith(
                            "_FUNCTION"
                    )
                            || normalizedSourceCode.endsWith(
                            "CORE_OPERATING_CAPACITY"
                    )
                            || normalizedSourceCode.endsWith(
                            "INTERPERSONAL_RELATION_CONTEXT"
                    )
            ) {

                return natalContext();
            }

            return formalPattern();
        }

        if (
                normalizedSourceCode.equals(
                        "PERSONALITY_CORE_SELF"
                )
                        || normalizedSourceCode.equals(
                        "PERSONALITY_FUNCTION"
                )
                        || normalizedSourceCode.equals(
                        "PERSONALITY_INTERPERSONAL_CONTEXT"
                )
        ) {

            return natalContext();
        }

        if (
                normalizedSourceCode.equals(
                        "PERSONALITY_CROSS_PATTERN"
                )
        ) {

            return formalPattern();
        }

        /*
         * =================================================
         * 일반 Category deterministic Conclusion
         * =================================================
         */

        return deterministicRule();
    }

    public static ConsultationMeaningOriginContract exactTenGod() {

        return new ConsultationMeaningOriginContract(
                ConsultationMeaningOrigin.EXACT_TEN_GOD,
                ConsultationMeaningAuthority.EXACT_TEN_GOD_FACT
        );
    }

    public static ConsultationMeaningOriginContract formalPattern() {

        return new ConsultationMeaningOriginContract(
                ConsultationMeaningOrigin.FORMAL_PATTERN,
                ConsultationMeaningAuthority.FORMAL_PATTERN_FACT
        );
    }

    public static ConsultationMeaningOriginContract deterministicRule() {

        return new ConsultationMeaningOriginContract(
                ConsultationMeaningOrigin.DETERMINISTIC_RULE,
                ConsultationMeaningAuthority.DETERMINISTIC_RULE_RESULT
        );
    }

    public static ConsultationMeaningOriginContract timelineContract() {

        return new ConsultationMeaningOriginContract(
                ConsultationMeaningOrigin.TIMELINE_CONTRACT,
                ConsultationMeaningAuthority.TIMELINE_CONTRACT_RESULT
        );
    }

    public static ConsultationMeaningOriginContract natalContext() {

        return new ConsultationMeaningOriginContract(
                ConsultationMeaningOrigin.NATAL_CONTEXT,
                ConsultationMeaningAuthority.NATAL_CONTEXT_FACT
        );
    }

    /**
     * 현재 Origin의 공식 Specificity Contract.
     */
    public ConsultationMeaningSpecificityContract specificityContract() {

        return ConsultationMeaningSpecificityContract.forOrigin(
                origin
        );
    }

    public boolean allowsSpecificity(
            ConsultationMeaningSpecificity specificity
    ) {

        return specificityContract()
                .allows(
                        specificity
                );
    }

    public void requireSpecificity(
            ConsultationMeaningSpecificity specificity
    ) {

        specificityContract()
                .requireAllowed(
                        specificity
                );
    }

    private static ConsultationMeaningAuthority expectedAuthority(
            ConsultationMeaningOrigin origin
    ) {

        return switch (origin) {

            case EXACT_TEN_GOD ->
                    ConsultationMeaningAuthority.EXACT_TEN_GOD_FACT;

            case FORMAL_PATTERN ->
                    ConsultationMeaningAuthority.FORMAL_PATTERN_FACT;

            case DETERMINISTIC_RULE ->
                    ConsultationMeaningAuthority.DETERMINISTIC_RULE_RESULT;

            case TIMELINE_CONTRACT ->
                    ConsultationMeaningAuthority.TIMELINE_CONTRACT_RESULT;

            case NATAL_CONTEXT ->
                    ConsultationMeaningAuthority.NATAL_CONTEXT_FACT;
        };
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}