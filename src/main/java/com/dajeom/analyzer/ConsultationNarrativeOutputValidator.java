package com.dajeom.analyzer;

import com.dajeom.model.ConsultationMeaningClaim;
import com.dajeom.model.ConsultationMeaningPacket;
import com.dajeom.model.ConsultationNarrativeValidationResult;
import com.dajeom.model.ConsultationNarrativeValidationViolation;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationNarrativeViolationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * AI가 반환한 하나의 Meaning Packet 문장을
 * Java Claim 경계 안에서 검증한다.
 *
 * =================================================
 * 검증 목표
 * =================================================
 *
 * 1.
 * 다른 Claim을 사용하지 않았는가.
 *
 * 2.
 * Claim에 없는 현실 사건을
 * 새로 만들지 않았는가.
 *
 * 3.
 * Claim에 없는 원인/결과/선후 관계를
 * 새로 만들지 않았는가.
 *
 * 4.
 * Claim보다 강하게 단정하지 않았는가.
 *
 * 5.
 * qualificationRequired인데
 * 확정문으로 바꾸지 않았는가.
 *
 * 6.
 * 명리 내부 용어와 코드가
 * 사용자에게 노출되지 않았는가.
 *
 * =================================================
 * 중요한 구조
 * =================================================
 *
 * 이 Validator 하나로
 * 자연어 의미를 완전히 증명하려 하지 않는다.
 *
 * OpenAI Adapter 자체도:
 *
 * AI 요청 1회
 * =
 * Meaning Packet 1개
 *
 * 로 격리한다.
 *
 * 따라서:
 *
 * Single Packet Isolation
 * +
 * Claim ID Validation
 * +
 * Deterministic Meaning Guard
 *
 * 를 함께 사용한다.
 */
@Component
public class ConsultationNarrativeOutputValidator {

    private static final Pattern LATIN_TOKEN_PATTERN =
            Pattern.compile(
                    "[A-Za-z_][A-Za-z0-9_-]*"
            );

    private static final Pattern HTML_TAG_PATTERN =
            Pattern.compile(
                    "<\\s*/?\\s*[A-Za-z][^>]*>"
            );

    private static final Pattern YEAR_PATTERN =
            Pattern.compile(
                    "(?<!\\d)(?:19|20)\\d{2}년"
            );

    /*
     * =================================================
     * 사용자 비노출 명리 내부 용어
     * =================================================
     */

    private static final List<String> FORBIDDEN_SAJU_TERMS =
            List.of(

                    "칠살",

                    "편관",

                    "정관",

                    "관살",

                    "관성",

                    "정재",

                    "편재",

                    "재성",

                    "비견",

                    "겁재",

                    "비겁",

                    "식신",

                    "상관",

                    "식상",

                    "정인",

                    "편인",

                    "인성",

                    "일간",

                    "신강",

                    "신약",

                    "용신",

                    "희신",

                    "기신",

                    "격국",

                    "통근",

                    "투간",

                    "지장간",

                    "천간",

                    "합화",

                    "삼합",

                    "방합",

                    "육합",

                    "신살",

                    "대운",

                    "세운",

                    "월운",

                    "오행",

                    "십성",

                    "십신",

                    "십이운성",

                    "12운성",

                    "득령",

                    "득지",

                    "득세"
            );

    /*
     * =================================================
     * 현실 사건 보호어
     * =================================================
     *
     * AI 출력에 아래 표현이 등장하려면
     * 현재 실제 사용 Claim 원문에도
     * 동일 표현이 존재해야 한다.
     *
     * 즉:
     *
     * 성향 Claim
     * ↓
     * AI가 결혼/퇴사/합격 등 사건 생성
     *
     * 을 차단한다.
     */

    private static final List<String> SENSITIVE_EVENT_TERMS =
            List.of(

                    "결혼",

                    "혼인",

                    "이혼",

                    "파혼",

                    "이별",

                    "재회",

                    "임신",

                    "출산",

                    "퇴사",

                    "이직",

                    "승진",

                    "취업",

                    "합격",

                    "불합격",

                    "해고",

                    "폐업",

                    "창업",

                    "대박",

                    "큰돈",

                    "파산",

                    "손실",

                    "사고",

                    "수술",

                    "입원",

                    "질병",

                    "진단",

                    "소송",

                    "고소",

                    "고발",

                    "승소",

                    "패소",

                    "처벌"
            );

    /*
     * =================================================
     * 관계 발명 탐지
     * =================================================
     *
     * Claim 원문 자체에 존재하지 않는다면
     * AI가 임의로 삽입할 수 없다.
     */

    private static final List<String> CAUSAL_CONNECTORS =
            List.of(

                    "때문에",

                    "그 결과",

                    "결과적으로",

                    "이로 인해",

                    "그로 인해",

                    "따라서",

                    "그러므로",

                    "그래서",

                    "결국",

                    "먼저",

                    "그다음",

                    "그 다음",

                    "이후",

                    "나중에"
            );

    /*
     * =================================================
     * 과도한 확정 표현
     * =================================================
     */

    private static final List<String> STRONG_ASSERTION_TERMS =
            List.of(

                    "반드시",

                    "무조건",

                    "틀림없이",

                    "확실히",

                    "확실합니다",

                    "분명히",

                    "분명합니다",

                    "확정적으로",

                    "확정됩니다",

                    "100%"
            );

    /*
     * =================================================
     * qualificationRequired 대응 표현
     * =================================================
     */

    private static final List<String> QUALIFICATION_MARKERS =
            List.of(

                    "경향",

                    "편입니다",

                    "편이에요",

                    "쪽입니다",

                    "쪽이에요",

                    "수 있습니다",

                    "가능성이",

                    "가능할",

                    "경우",

                    "때에 따라",

                    "조건에 따라",

                    "정도로",

                    "보일 수",

                    "느낄 수",

                    "나타날 수"
            );

    private static final List<String>
            FORBIDDEN_LEGAL_RESULT_PHRASES =
            List.of(

                    "승소합니다",

                    "패소합니다",

                    "처벌받습니다",

                    "반드시 승소",

                    "반드시 패소",

                    "처벌을 받게"
            );

    private static final List<String>
            FORBIDDEN_MEDICAL_ASSERTION_PHRASES =
            List.of(

                    "질병이 생깁니다",

                    "병이 생깁니다",

                    "진단받습니다",

                    "치료가 필요합니다",

                    "치료를 받아야"
            );

    public ConsultationNarrativeValidationResult validate(

            ConsultationMeaningPacket packet,

            List<String> usedClaimIds,

            String text,

            List<String> emphasis

    ) {

        Objects.requireNonNull(
                packet,
                "Narrative Output 검증에는 Meaning Packet이 필요합니다."
        );

        List<ConsultationNarrativeValidationViolation> violations =
                new ArrayList<>();

        String normalizedText =
                text == null
                        ? ""
                        : text.trim();

        List<String> normalizedUsedClaimIds =
                usedClaimIds == null
                        ? List.of()
                        : usedClaimIds
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                value ->
                                        !value.isBlank()
                        )
                        .distinct()
                        .toList();

        /*
         * =================================================
         * 기본 구조
         * =================================================
         */

        if (
                normalizedText.isBlank()
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.EMPTY_TEXT,
                    "본문이 비어 있습니다."
            );
        }

        if (
                normalizedUsedClaimIds.isEmpty()
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.EMPTY_USED_CLAIMS,
                    "usedClaimIds가 비어 있습니다."
            );
        }

        for (
                String claimId
                : normalizedUsedClaimIds
        ) {

            if (
                    !packet.allowsClaim(
                            claimId
                    )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.UNKNOWN_CLAIM,
                        "현재 Packet에 없는 Claim을 사용했습니다: "
                                + claimId
                );
            }
        }

        validateEmphasis(
                packet,
                normalizedText,
                emphasis,
                violations
        );

        if (
                !normalizedText.isBlank()
        ) {

            validateSurface(
                    packet,
                    normalizedText,
                    violations
            );

            validateMeaningBoundary(
                    packet,
                    normalizedUsedClaimIds,
                    normalizedText,
                    violations
            );
        }

        return new ConsultationNarrativeValidationResult(
                packet.packetId(),
                violations
        );
    }

    /*
     * =================================================
     * Emphasis
     * =================================================
     */

    private void validateEmphasis(

            ConsultationMeaningPacket packet,

            String text,

            List<String> emphasis,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        if (
                emphasis == null
                        ||
                        emphasis.isEmpty()
        ) {

            return;
        }

        for (
                String raw
                : emphasis
        ) {

            if (
                    raw == null
            ) {

                continue;
            }

            String phrase =
                    raw.trim();

            if (
                    phrase.isBlank()
            ) {

                continue;
            }

            if (
                    !text.contains(
                            phrase
                    )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.EMPHASIS_NOT_IN_TEXT,
                        "본문에 존재하지 않는 강조 구절입니다: "
                                + phrase
                );
            }
        }
    }

    /*
     * =================================================
     * Surface
     * =================================================
     */

    private void validateSurface(

            ConsultationMeaningPacket packet,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        if (
                text.contains(
                        "**"
                )
                        ||
                        text.contains(
                                "__"
                        )
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.MARKDOWN_EXPOSED,
                    "Markdown 강조 마커가 포함되었습니다."
            );
        }

        if (
                HTML_TAG_PATTERN
                        .matcher(
                                text
                        )
                        .find()
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.HTML_EXPOSED,
                    "HTML 태그가 포함되었습니다."
            );
        }

        if (
                LATIN_TOKEN_PATTERN
                        .matcher(
                                text
                        )
                        .find()
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.INTERNAL_TOKEN_EXPOSED,
                    "영문 또는 내부 코드 토큰이 포함되었습니다."
            );
        }

        /*
         * =================================================
         * 명리 내부 용어
         * =================================================
         */

        for (
                String term
                : FORBIDDEN_SAJU_TERMS
        ) {

            if (
                    text.contains(
                            term
                    )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.SAJU_TERM_EXPOSED,
                        "사용자 비노출 명리 용어가 포함되었습니다: "
                                + term
                );
            }
        }

        /*
         * =================================================
         * 연도 Scope
         * =================================================
         */

        if (
                !categoryAllowsYear(
                        packet.category()
                )
                        &&
                        YEAR_PATTERN
                                .matcher(
                                        text
                                )
                                .find()
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.YEAR_SCOPE_EXCEEDED,
                    "연도 허용 Category가 아닌데 연도 표현이 생성되었습니다."
            );
        }

        /*
         * =================================================
         * 법률
         * =================================================
         */

        if (
                packet.category()
                        == ConsultationCategory.LEGAL_RISK
        ) {

            for (
                    String phrase
                    : FORBIDDEN_LEGAL_RESULT_PHRASES
            ) {

                if (
                        text.contains(
                                phrase
                        )
                ) {

                    add(
                            violations,
                            packet,
                            ConsultationNarrativeViolationType.LEGAL_RESULT_ASSERTED,
                            "법률 결과를 단정했습니다: "
                                    + phrase
                    );
                }
            }
        }

        /*
         * =================================================
         * 의료
         * =================================================
         */

        if (
                packet.category()
                        == ConsultationCategory.HEALTH_TENDENCY
        ) {

            for (
                    String phrase
                    : FORBIDDEN_MEDICAL_ASSERTION_PHRASES
            ) {

                if (
                        text.contains(
                                phrase
                        )
                ) {

                    add(
                            violations,
                            packet,
                            ConsultationNarrativeViolationType.MEDICAL_RESULT_ASSERTED,
                            "의료 결과를 단정했습니다: "
                                    + phrase
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * Meaning Boundary
     * =================================================
     */

    private void validateMeaningBoundary(

            ConsultationMeaningPacket packet,

            List<String> usedClaimIds,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        List<ConsultationMeaningClaim> usedClaims =
                packet
                        .claims()
                        .stream()
                        .filter(
                                claim ->
                                        usedClaimIds.contains(
                                                claim.claimId()
                                        )
                        )
                        .toList();

        String allowedSourceText =
                String.join(
                        " ",
                        usedClaims
                                .stream()
                                .map(
                                        ConsultationMeaningClaim::text
                                )
                                .toList()
                );

        validateSensitiveEventTerms(
                packet,
                allowedSourceText,
                text,
                violations
        );

        validateCausalConnectors(
                packet,
                allowedSourceText,
                text,
                violations
        );

        validateAssertionStrength(
                packet,
                usedClaims,
                text,
                violations
        );

        validateQualification(
                packet,
                usedClaims,
                text,
                violations
        );
    }

    /*
     * =================================================
     * 사건 발명
     * =================================================
     */

    private void validateSensitiveEventTerms(

            ConsultationMeaningPacket packet,

            String allowedSourceText,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        for (
                String term
                : SENSITIVE_EVENT_TERMS
        ) {

            if (
                    text.contains(
                            term
                    )
                            &&
                            !allowedSourceText.contains(
                                    term
                            )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.SENSITIVE_EVENT_INVENTED,
                        "현재 사용 Claim에 없는 현실 사건 표현이 추가되었습니다: "
                                + term
                );
            }
        }
    }

    /*
     * =================================================
     * 관계 발명
     * =================================================
     */

    private void validateCausalConnectors(

            ConsultationMeaningPacket packet,

            String allowedSourceText,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        for (
                String connector
                : CAUSAL_CONNECTORS
        ) {

            if (
                    text.contains(
                            connector
                    )
                            &&
                            !allowedSourceText.contains(
                                    connector
                            )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.CAUSAL_RELATION_INVENTED,
                        "현재 사용 Claim에 없는 원인·결과·선후 연결 표현이 추가되었습니다: "
                                + connector
                );
            }
        }
    }

    /*
     * =================================================
     * 강한 단정 초과
     * =================================================
     */

    private void validateAssertionStrength(

            ConsultationMeaningPacket packet,

            List<ConsultationMeaningClaim> usedClaims,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        /*
         * 사용한 Claim 모두가
         * strongExpressionAllowed일 때만
         * 강한 단정을 허용한다.
         */

        boolean strongAllowed =
                !usedClaims.isEmpty()
                        &&
                        usedClaims
                                .stream()
                                .allMatch(
                                        ConsultationMeaningClaim
                                                ::strongExpressionAllowed
                                );

        if (
                strongAllowed
        ) {

            return;
        }

        for (
                String strongTerm
                : STRONG_ASSERTION_TERMS
        ) {

            if (
                    text.contains(
                            strongTerm
                    )
            ) {

                add(
                        violations,
                        packet,
                        ConsultationNarrativeViolationType.STRONG_ASSERTION_EXCEEDED,
                        "강한 표현이 허용되지 않은 Claim을 과도하게 단정했습니다: "
                                + strongTerm
                );
            }
        }
    }

    /*
     * =================================================
     * Qualification
     * =================================================
     */

    private void validateQualification(

            ConsultationMeaningPacket packet,

            List<ConsultationMeaningClaim> usedClaims,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        boolean qualificationRequired =
                usedClaims
                        .stream()
                        .anyMatch(
                                ConsultationMeaningClaim
                                        ::qualificationRequired
                        );

        if (
                !qualificationRequired
        ) {

            return;
        }

        boolean qualified =
                QUALIFICATION_MARKERS
                        .stream()
                        .anyMatch(
                                text::contains
                        );

        if (
                !qualified
        ) {

            add(
                    violations,
                    packet,
                    ConsultationNarrativeViolationType.REQUIRED_QUALIFICATION_MISSING,
                    "제한 표현이 필요한 Claim인데 경향/가능성/조건 표현이 없습니다."
            );
        }
    }

    /*
     * =================================================
     * Year Scope
     * =================================================
     */

    private boolean categoryAllowsYear(
            ConsultationCategory category
    ) {

        return category
                == ConsultationCategory.PAST_LUCK

                ||

                category
                        == ConsultationCategory.CURRENT_YEAR_LUCK

                ||

                category
                        == ConsultationCategory.FUTURE_LUCK;
    }

    /*
     * =================================================
     * Add
     * =================================================
     */

    private void add(

            List<ConsultationNarrativeValidationViolation> violations,

            ConsultationMeaningPacket packet,

            ConsultationNarrativeViolationType type,

            String detail

    ) {

        violations.add(
                new ConsultationNarrativeValidationViolation(
                        type,
                        packet.packetId(),
                        detail
                )
        );
    }
}