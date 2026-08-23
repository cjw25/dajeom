package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import com.dajeom.type.LifeDomainSignalStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CAREER 카테고리의
 * 원국 직업 기능 교차검증 Pattern 보존 Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 결과를 ConsultationCategory.CAREER의
 * 독립 deterministic 결과로 보존한다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * CAREER_CROSS_VALIDATED_PATTERN은
 * 기존 LifeDomain 계산에서 이미:
 *
 * - 식신제살
 * - 상관견관
 * - 정관생인
 * - 칠살생인
 * - 정관·칠살 혼재
 * - 재생정관
 *
 * 및:
 *
 * - 식상 -> 관살
 * - 재성 -> 관살
 * - 관살 -> 인성
 * - 식상 -> 재성 -> 관살 -> 인성
 *
 * 등의 Pattern / Family Chain을
 * 계산한 결과다.
 *
 * =================================================
 * Upstream 교차검증
 * =================================================
 *
 * 해당 Source는 기존 계산에서:
 *
 * - 핵심 십성 Pattern
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 등과 교차검증된 결과를 사용할 수 있다.
 *
 * 이 Category Rule에서는
 * 위 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 매우 중요한 원칙
 * =================================================
 *
 * Java upstream에서 실제 계산된:
 *
 * - 생 관계
 * - 극 관계
 * - Family Chain
 * - Core Pattern
 * - Cross Validation
 *
 * 자체는 유효한 deterministic 계산이다.
 *
 * 관계형 풀이 금지는
 * upstream Java가 실제 계산한 관계를
 * 삭제한다는 뜻이 아니다.
 *
 * 이 Rule에서 금지하는 것은:
 *
 * 이미 계산된 서로 다른 Pattern 결과를
 * 다시 임의로 연결하거나 합성하여
 * 새로운 의미를 만드는 것이다.
 *
 * =================================================
 * 기존 문제
 * =================================================
 *
 * 기존에는 여러:
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * Match의 Status를 한꺼번에 모아:
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE
 * -> MIXED
 *
 * PRESSURE 존재
 * -> PRESSURE
 *
 * SUPPORTIVE 존재
 * -> SUPPORTIVE
 *
 * 같은 방식으로
 * 하나의 Category finalStatus를 만들었다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 1 : 1 보존
 * =================================================
 *
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 그대로 보존한다.
 *
 * 예:
 *
 * Pattern A = SUPPORTIVE
 * Pattern B = PRESSURE
 * Pattern C = CONTEXTUAL
 *
 * 이라면:
 *
 * Category Match A = SUPPORTIVE
 * Category Match B = PRESSURE
 * Category Match C = CONTEXTUAL
 *
 * 로 각각 남긴다.
 *
 * A + B + C를 합쳐
 * 새로운 MIXED를 만들지 않는다.
 *
 * =================================================
 * Structured subtype 제한
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는:
 *
 * - 식신제살
 * - 상관견관
 * - 정관생인
 * - 칠살생인
 * - 재생정관
 *
 * 중 정확히 어느 Pattern인지 나타내는
 * 별도의 구조화된 patternType 필드가 없다.
 *
 * 따라서 이 Rule은:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여
 * Pattern subtype을 재구성하지 않는다.
 *
 * 원본 LifeDomainRuleMatch 자체를
 * sourceMatches에 그대로 보존한다.
 *
 * =================================================
 * CAREER 다른 원국 계산과의 분리
 * =================================================
 *
 * 이 Rule에서는:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 *
 * 를 사용하지 않는다.
 *
 * -------------------------------------------------
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * ->
 *
 * CareerCoreRoleStructureRule
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * ->
 *
 * CareerOfficerStructureDeterministicRule
 *
 * -------------------------------------------------
 *
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * ->
 *
 * 이 Rule
 *
 * -------------------------------------------------
 *
 * 각각 독립적인 deterministic 결과다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각 CAREER_CROSS_VALIDATED_PATTERN
 * LifeDomainRuleMatch의 공식 Status를
 * 그대로 1:1 변환한다.
 *
 * SUPPORTIVE
 * -> SUPPORTIVE
 *
 * PRESSURE
 * -> PRESSURE
 *
 * MIXED
 * -> MIXED
 *
 * CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 * 관계형 풀이 금지
 * =================================================
 *
 * 서로 다른 Pattern Match를 보고:
 *
 * - A 때문에 B
 * - A가 B로 이어진다
 * - A와 B가 함께 작동한다
 * - A가 B를 강화한다
 * - A가 B를 약화한다
 * - A를 B가 보완한다
 * - 먼저 A가 나타나고 이후 B가 된다
 *
 * 같은 새로운 관계를 생성하지 않는다.
 *
 * =================================================
 * 실제 계산된 관계와의 구분
 * =================================================
 *
 * 예를 들어 upstream Java가 실제로:
 *
 * OUTPUT_CONTROLS_PRESSURE
 *
 * 또는:
 *
 * RESOURCE_GENERATES_RESPONSIBILITY
 *
 * 같은 관계를 확정했다면
 * 그 계산 결과 자체는 보존할 수 있다.
 *
 * 하지만:
 *
 * 서로 별개로 존재하는 Pattern A와 Pattern B를
 * 이 Rule에서 새로 연결하지 않는다.
 *
 * =================================================
 * 특정 직업 대응 금지
 * =================================================
 *
 * 식신제살
 * != 특정 전문직
 *
 * 정관생인
 * != 공무원
 *
 * 칠살생인
 * != 군인
 * != 경찰
 *
 * 상관견관
 * != 조직생활 불가
 *
 * 재생관
 * != 사업 성공
 *
 * Pattern을 특정 직업으로
 * 직접 치환하지 않는다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 취업 성공
 * != 승진
 *
 * PRESSURE
 * != 퇴사
 * != 해고
 * != 직장 갈등
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 직업운 없음
 *
 * 또한:
 *
 * - 취업
 * - 승진
 * - 이직
 * - 퇴사
 * - 창업
 * - 사업 성공
 *
 * 같은 실제 사건을 생성하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Pattern Match가 여러 개 존재한다고:
 *
 * - 직업운이 강하다
 * - 직업 능력이 높다
 * - 성공 가능성이 높다
 * - 사회적 지위가 높다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 십성 재계산
 * - Family Chain 재계산
 * - Core Pattern 재계산
 * - Cross Validation 재계산
 * - Pattern 간 Status 합성
 * - 다른 CAREER Source와 Status 합성
 * - Pattern Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerFunctionIntegrationPatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_FUNCTION_INTEGRATION_PATTERN";

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "CAREER_CROSS_VALIDATED_PATTERN";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.CAREER;
    }


    @Override
    public String code() {

        return RULE_CODE;
    }


    @Override
    public List<ConsultationCategoryDeterministicMatch>
    evaluate(
            ConsultationCategoryEvidenceView view
    ) {

        Objects.requireNonNull(
                view,
                "CareerFunctionIntegrationPatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.CAREER
        ) {

            throw new IllegalArgumentException(
                    "CareerFunctionIntegrationPatternRule은 CAREER View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * unavailable
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 공식 Source
         * =================================================
         */

        List<LifeDomainRuleMatch> patternMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        if (
                patternMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Match 1개 -> Category Match 1개
         * =================================================
         *
         * 여러 Pattern Match를
         * 하나의 finalStatus로 합성하지 않는다.
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch patternMatch
                : patternMatches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            patternMatch,
                            "CAREER Cross Validated Pattern Match는 null일 수 없습니다."
                    );

            ConsultationCategoryDeterministicStatus status =
                    toCategoryStatus(
                            normalized.status()
                    );

            /*
             * =================================================
             * Reasons
             * =================================================
             *
             * 사용자 Story가 아니다.
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "CAREER_CROSS_VALIDATED_PATTERN에서 이미 계산된 하나의 LifeDomain Match를 독립적인 CAREER Category Match로 보존했습니다."
            );

            reasons.add(
                    "해당 LifeDomainRuleMatch의 공식 Status를 다른 CAREER_CROSS_VALIDATED_PATTERN Match와 결합하지 않고 1:1로 Category Status에 보존했습니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE와 CAREER_OFFICER_STRUCTURE는 이 Rule의 Source 또는 Status 계산에 사용하지 않았습니다."
            );

            reasons.add(
                    "Upstream Java에서 실제 계산된 십성 Pattern, Family Chain, Core Pattern 및 Cross Validation 결과 자체는 그대로 인정하며 이 Category Rule에서 다시 계산하지 않았습니다."
            );

            reasons.add(
                    "CAREER_CROSS_VALIDATED_PATTERN의 세부 Pattern subtype은 현재 LifeDomainRuleMatch의 구조화된 별도 필드로 노출되지 않으므로 summary 또는 reasons 문자열을 파싱하여 재분류하지 않았습니다."
            );

            reasons.add(
                    "다른 CAREER_CROSS_VALIDATED_PATTERN Match가 존재하더라도 서로 연결하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
            );

            reasons.add(
                    "Pattern Match의 존재 또는 반복 개수를 직업운 강도·직업 능력·사회적 지위·승진 가능성·사업 성공 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 결과를 취업·승진·이직·퇴사·해고·창업·사업 성공 같은 실제 직업 사건이나 특정 직업군으로 변환하지 않습니다."
            );

            /*
             * =================================================
             * Result
             * =================================================
             */

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.CAREER,

                            ConsultationCategoryDeterministicLayer
                                    .NATAL,

                            RULE_CODE,

                            status,

                            null,

                            null,

                            List.of(),

                            summaryOf(
                                    status
                            ),

                            List.of(
                                    normalized
                            ),

                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Natal Source 조회
     * =================================================
     *
     * Source 선택에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 판별에 사용하지 않는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "CAREER Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "CAREER Cross Validated Pattern sourceRuleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "CAREER Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            if (
                    !normalizedRuleCode.equals(
                            normalized.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    normalized
            );
        }

        return List.copyOf(
                result
        );
    }


    /*
     * =================================================
     * Status 1:1 변환
     * =================================================
     *
     * 다른 Pattern Match 또는
     * 다른 CAREER Source를 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Cross Validated Pattern에는 LifeDomainSignalStatus가 필요합니다."
        );

        return switch (
                sourceStatus
                ) {

            case SUPPORTIVE ->
                    ConsultationCategoryDeterministicStatus
                            .SUPPORTIVE;

            case PRESSURE ->
                    ConsultationCategoryDeterministicStatus
                            .PRESSURE;

            case MIXED ->
                    ConsultationCategoryDeterministicStatus
                            .MIXED;

            case CONTEXTUAL ->
                    ConsultationCategoryDeterministicStatus
                            .CONTEXTUAL;
        };
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * Pattern subtype을 추측하지 않는다.
     * 해당 Match 하나의 공식 방향만 보존한다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Cross Validated Pattern Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "교차검증된 하나의 CAREER Pattern 결과가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "교차검증된 하나의 CAREER Pattern 결과가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "교차검증된 하나의 CAREER Pattern 결과가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "교차검증된 하나의 CAREER Pattern 결과가 CONTEXTUAL 상태로 보존됩니다.";
        };
    }


    /*
     * =================================================
     * Text
     * =================================================
     */

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