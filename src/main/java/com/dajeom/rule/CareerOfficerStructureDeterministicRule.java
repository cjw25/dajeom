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
 * CAREER 원국 관살 구조 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * 결과를 ConsultationCategory.CAREER의
 * 독립 deterministic 결과로 보존한다.
 *
 * =================================================
 * Upstream 계산
 * =================================================
 *
 * CareerOfficerStructureRule은
 * 기존 OfficerCompositeAnalysis를 사용하여:
 *
 * - OFFICER_DEVELOPED
 * - DIRECT_OFFICER_CENTERED
 * - SEVEN_KILLINGS_CENTERED
 * - MIXED_OFFICER_KILLINGS
 * - WEAK_DAY_MASTER_OFFICER_PRESSURE
 * - OUTPUT_CONTROL_CONTEXT
 * - RESOURCE_MEDIATION_CONTEXT
 * - WEALTH_SUPPORT_CONTEXT
 *
 * 를 이미 계산한다.
 *
 * 이 Category Rule에서는
 * 위 내용을 다시 계산하지 않는다.
 *
 * =================================================
 * 실제 Java 관계 계산
 * =================================================
 *
 * OUTPUT_CONTROL_CONTEXT
 * RESOURCE_MEDIATION_CONTEXT
 * WEALTH_SUPPORT_CONTEXT
 *
 * 같은 항목은 upstream Java가
 * 실제 OfficerCompositeAnalysis에서
 * 계산한 구조적 Context다.
 *
 * 따라서 해당 계산 결과 자체는 보존한다.
 *
 * 단:
 *
 * 이 Category Rule에서
 * 여러 결과를 다시 연결하여
 * 새로운 관계·인과·순서를 만들지 않는다.
 *
 * =================================================
 * CAREER_PRIMARY_OFFICER_STRUCTURE와 분리
 * =================================================
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * 는:
 *
 * 관성이 실제 원국의 주격 중심축으로
 * 선택되었는지를 계산한다.
 *
 * CareerPrimaryOfficerStructureDeterministicRule이
 * 독립적으로 담당한다.
 *
 * -------------------------------------------------
 *
 * CAREER_OFFICER_STRUCTURE
 *
 * 는:
 *
 * 원국 전체에서 관살 Family가 가지는
 * 발달·중심성·혼재·압박·중재 Context를
 * 계산한다.
 *
 * 이 Rule이 독립적으로 담당한다.
 *
 * -------------------------------------------------
 *
 * 두 Source Family의 Status를
 * 서로 합성하지 않는다.
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
 * Match A = CONTEXTUAL
 * Match B = PRESSURE
 * Match C = MIXED
 *
 * 라면:
 *
 * Category Match A = CONTEXTUAL
 * Category Match B = PRESSURE
 * Category Match C = MIXED
 *
 * 로 각각 남긴다.
 *
 * A + B + C를 이용해
 * 새로운 CAREER finalStatus를 만들지 않는다.
 *
 * =================================================
 * 이유
 * =================================================
 *
 * 현재 LifeDomainRuleMatch에는:
 *
 * - OFFICER_DEVELOPED
 * - DIRECT_OFFICER_CENTERED
 * - SEVEN_KILLINGS_CENTERED
 * - MIXED_OFFICER_KILLINGS
 * - WEAK_DAY_MASTER_OFFICER_PRESSURE
 * - OUTPUT_CONTROL_CONTEXT
 * - RESOURCE_MEDIATION_CONTEXT
 * - WEALTH_SUPPORT_CONTEXT
 *
 * 중 정확히 어느 subtype인지 나타내는
 * 별도의 구조화된 필드가 없다.
 *
 * 따라서:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱해서 subtype을
 * 다시 추측하지 않는다.
 *
 * 원본 LifeDomainRuleMatch를
 * sourceMatches에 그대로 보존한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 각 LifeDomainRuleMatch의
 * 공식 LifeDomainSignalStatus를
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
 * 서로 다른 CAREER_OFFICER_STRUCTURE Match를 보고:
 *
 * - 관살이 발달했기 때문에 압박이 생긴다
 * - 압박 때문에 인성이 중재한다
 * - 식상이 관살을 조절해서 직업 능력이 좋아진다
 * - 재성이 관살을 지원해서 사회적 지위가 높아진다
 * - 정관에서 칠살로 바뀐다
 * - 처음에는 A이고 이후에는 B가 된다
 * - A 때문에 B
 * - A가 B로 이어진다
 *
 * 같은 새로운 의미를 생성하지 않는다.
 *
 * Upstream Java에서 실제 계산한
 * 개별 Context만 보존한다.
 *
 * =================================================
 * 특정 직종 생성 금지
 * =================================================
 *
 * DIRECT_OFFICER
 * != 공무원
 *
 * SEVEN_KILLINGS
 * != 군인
 * != 경찰
 * != 법조인
 *
 * RESOURCE_MEDIATION
 * != 자격증 직업
 *
 * OUTPUT_CONTROL
 * != 창업
 *
 * 같은 직종 대응을 만들지 않는다.
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
 * != 해고
 * != 퇴사
 * != 직장 갈등
 *
 * MIXED
 * != 이직
 * != 직업 변동
 *
 * CONTEXTUAL
 * != 직업운 없음
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * CAREER_OFFICER_STRUCTURE Match가
 * 여러 개 존재한다고:
 *
 * - 직업운이 강하다
 * - 사회적 지위가 높다
 * - 책임이 많다
 * - 직업 능력이 높다
 *
 * 로 계산하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 관살 세력 재계산
 * - 정관 / 칠살 중심성 재계산
 * - 관살혼재 재계산
 * - 신강신약 재계산
 * - 식상 제어 재계산
 * - 인성 중재 재계산
 * - 재성 지원 재계산
 * - CAREER_PRIMARY_OFFICER_STRUCTURE와 Status 합성
 * - Match 간 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 새로운 관계 생성
 * - 새로운 인과 생성
 * - 새로운 순서 생성
 * - 특정 직업군 생성
 * - 실제 직업 사건 생성
 */
@Component
public class CareerOfficerStructureDeterministicRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_OFFICER_STRUCTURE_CONTEXT";

    private static final String
            OFFICER_STRUCTURE_SOURCE_RULE_CODE =
            "CAREER_OFFICER_STRUCTURE";


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
                "CareerOfficerStructureDeterministicRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerOfficerStructureDeterministicRule은 CAREER View만 평가할 수 있습니다."
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
         * CAREER_OFFICER_STRUCTURE Source
         * =================================================
         */

        List<LifeDomainRuleMatch> officerStructureMatches =
                findNatalMatches(
                        view,
                        OFFICER_STRUCTURE_SOURCE_RULE_CODE
                );

        if (
                officerStructureMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Match 하나 -> Category Match 하나
         * =================================================
         */

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch sourceMatch
                : officerStructureMatches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            sourceMatch,
                            "CAREER_OFFICER_STRUCTURE Match는 null일 수 없습니다."
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
             * 최종 사용자 Story가 아니라
             * deterministic provenance 기록이다.
             */

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "CAREER_OFFICER_STRUCTURE에서 이미 계산된 하나의 LifeDomain Match를 독립적인 CAREER Category Match로 보존했습니다."
            );

            reasons.add(
                    "해당 LifeDomainRuleMatch의 공식 Status를 다른 CAREER_OFFICER_STRUCTURE Match 또는 다른 CAREER Source와 결합하지 않고 1:1로 Category Status에 보존했습니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE는 이 Rule에 사용하지 않았으며 CareerPrimaryOfficerStructureDeterministicRule에서 별도의 원국 Primary Officer Structure 결과로 처리합니다."
            );

            reasons.add(
                    "CAREER_OFFICER_STRUCTURE 내부의 세부 Pattern subtype은 현재 LifeDomainRuleMatch에 구조화된 별도 필드로 노출되지 않으므로 summary 또는 reasons 문자열을 파싱하여 재분류하지 않았습니다."
            );

            reasons.add(
                    "Upstream OfficerCompositeAnalysis에서 실제 계산된 관살 발달·정관 중심·칠살 중심·관살혼재·신약 관살압박·식상 제어·인성 중재·재성 지원 Context 자체는 그대로 인정합니다."
            );

            reasons.add(
                    "다른 CAREER_OFFICER_STRUCTURE Match와의 새로운 강화·약화·상쇄·전환·인과·순서를 생성하지 않았습니다."
            );

            reasons.add(
                    "Match의 존재 또는 반복 개수를 직업운 강도·사회적 지위·직업 능력·승진 가능성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "이 결과를 취업·승진·이직·퇴사·해고·창업 같은 실제 직업 사건이나 특정 직업군으로 변환하지 않습니다."
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
     * Source 판별에는:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * 판단 조건으로 사용하지 않는다.
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
                        "CAREER Officer Structure sourceRuleCode가 필요합니다."
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
     * 다른 Match를 참조하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    toCategoryStatus(
            LifeDomainSignalStatus sourceStatus
    ) {

        Objects.requireNonNull(
                sourceStatus,
                "CAREER Officer Structure에는 LifeDomainSignalStatus가 필요합니다."
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
     * 정확한 subtype은 추측하지 않는다.
     * Source Match 하나의 공식 방향만 보존한다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "CAREER Officer Structure Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국에서 이미 계산된 하나의 관살 구조 Context가 SUPPORTIVE 상태로 보존됩니다.";

            case PRESSURE ->
                    "원국에서 이미 계산된 하나의 관살 구조 Context가 PRESSURE 상태로 보존됩니다.";

            case MIXED ->
                    "원국에서 이미 계산된 하나의 관살 구조 Context가 MIXED 상태로 보존됩니다.";

            case CONTEXTUAL ->
                    "원국에서 이미 계산된 하나의 관살 구조 Context가 CONTEXTUAL 상태로 보존됩니다.";
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