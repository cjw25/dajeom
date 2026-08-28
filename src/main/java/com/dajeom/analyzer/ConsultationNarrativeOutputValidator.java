package com.dajeom.analyzer;

import com.dajeom.model.ConsultationMeaningClaim;
import com.dajeom.model.ConsultationMeaningPacket;
import com.dajeom.model.ConsultationNarrativeValidationResult;
import com.dajeom.model.ConsultationNarrativeValidationViolation;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationMeaningAuthority;
import com.dajeom.type.ConsultationMeaningOrigin;
import com.dajeom.type.ConsultationMeaningSpecificity;
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
 * 7.
 * 지지/부담 같은 deterministic 방향을
 * 임의의 긍정/부정 가치판단으로
 * 재해석하지 않았는가.
 *
 * 8.
 * Claim에 없는 결과/성과/중요도를
 * 새로 만들지 않았는가.
 *
 * =================================================
 * 표현 자유
 * =================================================
 *
 * Java Meaning Boundary는 엄격하게 유지한다.
 *
 * 다만 qualificationRequired Claim을
 * 사용자에게 자연스럽게 표현하기 위해:
 *
 * - 한쪽으로만 보기 어렵습니다.
 * - 단정하기 어렵습니다.
 * - 상황에 따라 달라질 수 있습니다.
 * - 가능성을 열어두고 봐야 합니다.
 *
 * 같은 일반 사용자용 제한 표현도
 * 공식 Qualification 표현으로 인정한다.
 *
 * 이것은 Meaning 확장이 아니라
 * Expression Freedom 확장이다.
 *
 * =================================================
 * 134-4 Meaning Boundary Hardening
 * =================================================
 *
 * Writer First 구조 적용 후
 * 실제 Runtime에서 확인된 의미 확장:
 *
 * - 노력 -> 가시적인 결과
 * - 지지 -> 긍정적 흐름
 * - 부담 -> 부정적 흐름
 * - 서로 다른 방향 -> 서로 다른 영향
 * - 일반 특징 -> 가장 큰 특징
 *
 * 을 Java Boundary에서 추가 차단한다.
 *
 * 단,
 * source Claim 자체에 해당 표현이 존재한다면
 * 새로 발명한 의미가 아니므로 허용한다.
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
     * 134-4
     * Claim에 없는 결과 구체화
     * =================================================
     *
     * 단순 지지/부담/작동 방향을
     * 실제 성과나 눈에 보이는 결과로
     * 확장하는 것을 차단한다.
     *
     * source Claim 자체에 동일 표현이 있다면
     * 허용한다.
     */

    private static final List<String> UNSUPPORTED_OUTCOME_TERMS =
            List.of(

                    "가시적인 결과",

                    "눈에 보이는 결과",

                    "뚜렷한 결과",

                    "구체적인 결과",

                    "실질적인 결과",

                    "성과",

                    "결실",

                    "보상"
            );

    /*
     * =================================================
     * 134-4
     * 가치판단 의미 확장
     * =================================================
     *
     * 프로젝트 공식 원칙:
     *
     * 지지
     * !=
     * 좋은 사건 / 긍정적 사건
     *
     * 부담
     * !=
     * 나쁜 사건 / 부정적 사건
     *
     * deterministic Claim에
     * 해당 가치판단이 직접 존재할 때만 허용한다.
     */

    private static final List<String> UNSUPPORTED_EVALUATIVE_TERMS =
            List.of(

                    "긍정적",

                    "부정적",

                    "좋은 흐름",

                    "좋은 방향",

                    "좋은 쪽",

                    "나쁜 흐름",

                    "나쁜 방향",

                    "나쁜 쪽",

                    "좋아지는",

                    "좋아질",

                    "좋아집니다",

                    "좋습니다",

                    "나빠지는",

                    "나빠질",

                    "나빠집니다",

                    "나쁩니다",

                    "악화"
            );

    /*
     * =================================================
     * 134-4
     * 중요도 재판단
     * =================================================
     *
     * Java가 중요도/대표성을 정하지 않았는데
     * AI가 임의로:
     *
     * - 가장 큰 특징
     * - 가장 중요한 특징
     * - 대표 특징
     *
     * 으로 승격하는 것을 차단한다.
     */

    private static final List<String> UNSUPPORTED_IMPORTANCE_TERMS =
            List.of(

                    "가장 큰 특징",

                    "가장 중요한",

                    "가장 핵심",

                    "핵심적인 특징",

                    "핵심 특징",

                    "대표적인 특징",

                    "대표 특징",

                    "가장 두드러",

                    "특히 두드러"
            );

    /*
     * =================================================
     * 관계 발명 탐지
     * =================================================
     *
     * 완성형뿐 아니라
     * 한국어 활용형 전체를 잡을 수 있도록
     * 일부 항목은 어간 단위로 둔다.
     *
     * source Claim 자체에 같은 표현이 있으면
     * 허용된다.
     */

    private static final List<String> CAUSAL_CONNECTORS =
            List.of(

                    "때문에",

                    "그 결과",

                    "결과적으로",

                    "이로 인해",

                    "그로 인해",

                    "로 인해",

                    "으로 인해",

                    "따라서",

                    "그러므로",

                    "그래서",

                    "결국",

                    "먼저",

                    "그다음",

                    "그 다음",

                    "이후",

                    "나중에",

                    "작동하여",

                    "작용하여",

                    "활성되어",

                    "활성되면서",

                    /*
                     * 134-4:
                     * "영향을 주어"만 검사하면
                     * "영향을 줍니다",
                     * "영향을 줄 수 있습니다"
                     * 등을 놓칠 수 있다.
                     */
                    "영향을 주",

                    "영향을 미치",

                    "영향을 받",

                    /*
                     * 이어집니다 / 이어질 / 이어지게 /
                     * 이어지는 / 이어졌습니다 등을
                     * 하나의 어간으로 검증한다.
                     */
                    "이어지",

                    /*
                     * 연결되어 / 연결될 / 연결되는
                     */
                    "연결되",

                    /*
                     * 초래합니다 / 초래할 / 초래하는
                     */
                    "초래하",

                    /*
                     * 불러옵니다 / 불러올 / 불러오는
                     */
                    "불러오",

                    /*
                     * 발생시켜 / 발생시킵니다 /
                     * 발생시키는
                     */
                    "발생시키",

                    "만들어내",

                    "만들어 냅",

                    /*
                     * 강화하여 / 강화합니다 / 강화할
                     */
                    "강화하",

                    /*
                     * 약화하여 / 약화합니다 / 약화할
                     */
                    "약화하"
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
     *
     * 정밀화 133.
     *
     * 기존에는:
     *
     * - 가능성이
     * - 수 있습니다
     * - 조건에 따라
     *
     * 등의 제한 표현만 인정했다.
     *
     * 이 경우 Writer가 같은 Meaning을
     * 일반 사용자에게 더 자연스럽게:
     *
     * - 한쪽으로만 보기 어렵습니다.
     * - 단정하기 어렵습니다.
     * - 가능성을 열어두고 봐야 합니다.
     * - 상황에 따라 달라질 수 있습니다.
     *
     * 로 표현해도 Java Validation에서
     * qualification 누락으로 판단될 수 있었다.
     *
     * 아래 표현들은 새로운 의미가 아니라
     * 모두 "단일 확정 금지"를 표현한다.
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

                    "상황에 따라",

                    "정도로",

                    "보일 수",

                    "느낄 수",

                    "나타날 수",

                    "달라질 수",

                    "다르게 나타날 수",

                    "다르게 드러날 수",

                    "단정하기 어렵",

                    "확정하기 어렵",

                    "한쪽으로 보기 어렵",

                    "한쪽으로만 보기 어렵",

                    "한 가지로 보기 어렵",

                    "한 가지로 정하기 어렵",

                    "일률적으로 보기 어렵",

                    "고정해서 보기 어렵",

                    "하나로 고정하기 어렵",

                    "여지를 두고",

                    "가능성을 열어두고",

                    "열어두고 봐야",

                    "단정할 수 없",

                    "볼 수는 없"
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
                        || emphasis.isEmpty()
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
                        || text.contains(
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
                !packetAllowsYear(
                        packet
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
                    "공식 Timeline 범위가 아닌 Packet에 연도 표현이 생성되었습니다."
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

        validateUnsupportedOutcomeTerms(
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

        validateUnsupportedEvaluativeTerms(
                packet,
                allowedSourceText,
                text,
                violations
        );

        validateUnsupportedImportanceTerms(
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
     * 134-4
     * 결과/성과 발명
     * =================================================
     */

    private void validateUnsupportedOutcomeTerms(

            ConsultationMeaningPacket packet,

            String allowedSourceText,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        for (
                String term
                : UNSUPPORTED_OUTCOME_TERMS
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
                        "현재 사용 Claim에 없는 구체적인 결과·성과 의미가 추가되었습니다: "
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
     * 134-4
     * 가치판단 의미 발명
     * =================================================
     *
     * 지지/부담을
     * 긍정/부정, 좋음/나쁨으로
     * 자동 변환하지 못하게 한다.
     */

    private void validateUnsupportedEvaluativeTerms(

            ConsultationMeaningPacket packet,

            String allowedSourceText,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        for (
                String term
                : UNSUPPORTED_EVALUATIVE_TERMS
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
                        ConsultationNarrativeViolationType.UNKNOWN_CLAIM,
                        "현재 사용 Claim에 없는 긍정/부정 가치판단 의미가 추가되었습니다: "
                                + term
                );
            }
        }
    }

    /*
     * =================================================
     * 134-4
     * 중요도 재판단 방지
     * =================================================
     */

    private void validateUnsupportedImportanceTerms(

            ConsultationMeaningPacket packet,

            String allowedSourceText,

            String text,

            List<ConsultationNarrativeValidationViolation> violations

    ) {

        for (
                String term
                : UNSUPPORTED_IMPORTANCE_TERMS
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
                        ConsultationNarrativeViolationType.UNKNOWN_CLAIM,
                        "현재 사용 Claim에 없는 중요도·대표성 판단이 추가되었습니다: "
                                + term
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

    private boolean packetAllowsYear(
            ConsultationMeaningPacket packet
    ) {

        Objects.requireNonNull(
                packet,
                "Year Scope 판정에는 Meaning Packet이 필요합니다."
        );

        ConsultationCategory category =
                packet.category();

        if (
                category
                        == ConsultationCategory.PAST_LUCK

                        ||

                        category
                                == ConsultationCategory.CURRENT_YEAR_LUCK

                        ||

                        category
                                == ConsultationCategory.FUTURE_LUCK
        ) {

            return true;
        }

        return packet.specificity()
                == ConsultationMeaningSpecificity.TIMING

                &&

                packet.origin()
                        == ConsultationMeaningOrigin.TIMELINE_CONTRACT

                &&

                packet.authority()
                        == ConsultationMeaningAuthority.TIMELINE_CONTRACT_RESULT;
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