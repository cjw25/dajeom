package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.LifeDomainRuleMatch;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.ConsultationCategoryDeterministicLayer;
import com.dajeom.type.ConsultationCategoryDeterministicStatus;
import com.dajeom.type.LifeDomain;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CAREER 카테고리의
 * 원국 환경·사회적 작동 Palace Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * LifeDomain.CAREER에서 이미 계산된:
 *
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT
 *
 * 결과를 ConsultationCategory.CAREER의
 * 독립적인 NATAL Context로 보존한다.
 *
 * =================================================
 * Upstream
 * =================================================
 *
 * CareerEnvironmentalPalaceContextRule은
 * 12차 PalaceAnalysis의:
 *
 * MONTH Palace
 *
 * 를 사용한다.
 *
 * MONTH Palace의 공식 위치 Context는:
 *
 * ENVIRONMENTAL_OPERATION
 *
 * 이다.
 *
 * 즉 이 Source 자체가 이미:
 *
 * - 환경·사회적 작동 위치
 * - 원국 MONTH 위치
 * - 해당 위치의 TenGod Evidence
 * - 12운성 Context
 * - 천간 관계 Reference
 * - 지지 관계 Reference
 * - Transformation Context
 * - 실제 합화 Activation 여부
 * - 원기능 감소 여부
 *
 * 를 Java에서 계산하여 보존한다.
 *
 * 이 Category Rule에서는
 * 해당 계산을 다시 수행하지 않는다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는:
 *
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT
 *
 * 가 존재하더라도 추가로:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 중 최소 하나가 있어야
 * CAREER Category Match를 생성했다.
 *
 * 그리고 해당 Anchor Match들을
 * 환경 Palace Match와 같은 sourceMatches에
 * 함께 넣었다.
 *
 * 이제 그렇게 하지 않는다.
 *
 * =================================================
 * 독립 Context
 * =================================================
 *
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT는
 * 그 자체로 이미:
 *
 * "원국 MONTH Palace의 공식
 * ENVIRONMENTAL_OPERATION Context"
 *
 * 를 계산한 결과다.
 *
 * 다른 CAREER 구조가 존재해야만
 * 유효해지는 Source로 취급하지 않는다.
 *
 * =================================================
 * 구조 Anchor 제거
 * =================================================
 *
 * 이 Rule에서는 더 이상:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 * CAREER_OFFICER_STRUCTURE
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 을 조회하지 않는다.
 *
 * 따라서:
 *
 * Environmental Palace
 * +
 * Primary Officer Structure
 *
 * 또는:
 *
 * Environmental Palace
 * +
 * Officer Structure
 *
 * 또는:
 *
 * Environmental Palace
 * +
 * Cross Validated Pattern
 *
 * 을 이용한 새로운 Category 의미를 만들지 않는다.
 *
 * =================================================
 * 다른 CAREER 원국 계산과의 분리
 * =================================================
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
 * CareerFunctionIntegrationPatternRule
 *
 * -------------------------------------------------
 *
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT
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
 * 1 : 1 보존
 * =================================================
 *
 * LifeDomainRuleMatch 하나
 *
 * ->
 *
 * ConsultationCategoryDeterministicMatch 하나
 *
 * 로 보존한다.
 *
 * 현재 upstream은 MONTH Palace Context 하나를
 * 생성하지만 Category Rule은
 * Source Match별 1:1 구조를 유지한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이 Rule의 Status는 항상:
 *
 * CONTEXTUAL
 *
 * 이다.
 *
 * Palace 위치는:
 *
 * - 취업 성공
 * - 승진 가능성
 * - 직장 안정성
 * - 조직 적응력
 * - 직업 실패
 *
 * 같은 방향성 Evidence가 아니기 때문이다.
 *
 * =================================================
 * Source Status 전이 금지
 * =================================================
 *
 * upstream
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT 역시
 * CONTEXTUAL이다.
 *
 * 이 Rule에서는 다른 CAREER Source의:
 *
 * SUPPORTIVE
 * PRESSURE
 * MIXED
 *
 * 상태를 가져와
 * Palace Context에 전이하지 않는다.
 *
 * =================================================
 * 실제 Java Palace 계산 보존
 * =================================================
 *
 * upstream PalaceAnalysis에서 실제 계산된:
 *
 * - TenGod 존재
 * - 12운성
 * - 천간 관계
 * - 지지 관계
 * - Transformation 후보
 * - activated Transformation
 * - original function reduction
 *
 * 자체는 그대로 유효하다.
 *
 * 관계형 풀이 금지는
 * 이러한 실제 계산된 Palace 구조를
 * 삭제한다는 뜻이 아니다.
 *
 * =================================================
 * Category 단계 관계 생성 금지
 * =================================================
 *
 * 다음을 생성하지 않는다.
 *
 * - 관살 구조가 있으므로
 *   MONTH Palace가 직업적으로 중요하다
 *
 * - 주격 관성이 있기 때문에
 *   환경 작동성이 강화된다
 *
 * - Cross Validated Pattern이 있으므로
 *   환경에서 해당 Pattern이 나타난다
 *
 * - Palace Context 때문에
 *   직업 구조가 강화된다
 *
 * - 직업 구조 때문에
 *   환경 부담이 커진다
 *
 * - A 때문에 B
 *
 * - A가 B로 이어진다
 *
 * - A와 B가 함께 작동한다
 *
 * =================================================
 * MONTH 의미 확대 금지
 * =================================================
 *
 * MONTH
 * != 직장
 *
 * MONTH
 * != 회사
 *
 * MONTH
 * != 상사
 *
 * MONTH
 * != 조직생활 결과
 *
 * MONTH의 공식 의미는
 * upstream PalaceAnalysis가 확정한:
 *
 * ENVIRONMENTAL_OPERATION
 *
 * Context까지만 보존한다.
 *
 * =================================================
 * 특정 사건 생성 금지
 * =================================================
 *
 * MONTH 정관
 * != 승진
 *
 * MONTH 칠살
 * != 직장 갈등
 *
 * MONTH 재성
 * != 돈 버는 직업
 *
 * MONTH 충
 * != 이직
 *
 * MONTH 합
 * != 취업
 *
 * MONTH 합화
 * != 직업 전환
 *
 * =================================================
 * 특정 직업 생성 금지
 * =================================================
 *
 * MONTH 정관
 * != 공무원
 *
 * MONTH 칠살
 * != 군인
 * != 경찰
 *
 * MONTH 인성
 * != 교사
 *
 * MONTH 재성
 * != 금융업
 *
 * =================================================
 * 12운성 길흉 변환 금지
 * =================================================
 *
 * 장생
 * 건록
 * 제왕
 *
 * != SUPPORTIVE
 *
 * 병
 * 사
 * 묘
 * 절
 *
 * != PRESSURE
 *
 * 12운성은 upstream Palace Context에
 * 포함된 구조 정보로만 보존한다.
 *
 * =================================================
 * 관계 길흉 변환 금지
 * =================================================
 *
 * 합
 * != SUPPORTIVE
 *
 * 충
 * != PRESSURE
 *
 * 형
 * != 직장 갈등
 *
 * 파
 * != 퇴사
 *
 * 해
 * != 조직 문제
 *
 * PalaceAnalysis에 실제 관계 Reference가
 * 존재한다는 사실만 보존하며
 * Category 단계에서 재판정하지 않는다.
 *
 * =================================================
 * Transformation 경계
 * =================================================
 *
 * 다음 세 상태를 동일하게 취급하지 않는다.
 *
 * hasTransformations()
 *
 * !=
 *
 * hasActivatedTransformation()
 *
 * !=
 *
 * hasReducedOriginalFunction()
 *
 * 이 구분은 upstream Java 계산을 그대로 따른다.
 *
 * =================================================
 * 문자열 파싱 금지
 * =================================================
 *
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여:
 *
 * - TenGod
 * - 12운성
 * - 관계
 * - 합화
 * - 직업 사건
 * - 직종
 *
 * 을 새로 구성하지 않는다.
 *
 * =================================================
 * Match 개수 금지
 * =================================================
 *
 * Environmental Palace Match 개수를:
 *
 * - 직업 능력
 * - 사회적 성공
 * - 조직 적응력
 * - 승진 가능성
 * - 직업 안정성
 *
 * 점수로 사용하지 않는다.
 *
 * =================================================
 * 출생시간 미상
 * =================================================
 *
 * MONTH Palace는 출생시간 미상이어도
 * 기존 PalaceAnalysis에서 계산될 수 있다.
 *
 * HOUR가 미확정이라고 해서
 * MONTH Context를 삭제하거나
 * 가짜 HOUR 정보를 추가하지 않는다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Palace 재계산
 * - MONTH 위치 재계산
 * - TenGod 재계산
 * - 12운성 재계산
 * - 천간 관계 재계산
 * - 지지 관계 재계산
 * - Transformation 재계산
 * - CAREER 구조 Anchor 요구
 * - 다른 CAREER Source 조회
 * - 다른 CAREER Source와 결합
 * - Status 합성
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
public class CareerEnvironmentalOperationContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_ENVIRONMENTAL_OPERATION_CONTEXT";

    private static final String
            ENVIRONMENTAL_PALACE_RULE_CODE =
            "CAREER_ENVIRONMENTAL_PALACE_CONTEXT";


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
                "CareerEnvironmentalOperationContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
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
                    "CareerEnvironmentalOperationContextRule은 CAREER View만 평가할 수 있습니다."
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

        List<LifeDomainRuleMatch> environmentalMatches =
                findNatalMatches(
                        view,
                        ENVIRONMENTAL_PALACE_RULE_CODE
                );

        if (
                environmentalMatches.isEmpty()
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
                LifeDomainRuleMatch environmentalMatch
                : environmentalMatches
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            environmentalMatch,
                            "CAREER Environmental Palace Match는 null일 수 없습니다."
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
                    "CAREER_ENVIRONMENTAL_PALACE_CONTEXT에서 이미 계산된 하나의 MONTH ENVIRONMENTAL_OPERATION Context Match를 독립적인 CAREER Category Context로 보존했습니다."
            );

            reasons.add(
                    "CAREER_PRIMARY_OFFICER_STRUCTURE, CAREER_OFFICER_STRUCTURE, CAREER_CROSS_VALIDATED_PATTERN 같은 별도 CAREER 구조 Anchor의 존재를 이 Match의 생성 조건으로 요구하지 않았습니다."
            );

            reasons.add(
                    "다른 CAREER 원국 Match를 이 Category Match의 sourceMatches에 포함하지 않았습니다."
            );

            reasons.add(
                    "MONTH Palace Context 자체는 직업적 길흉 방향을 결정하는 Evidence가 아니므로 Category Status를 CONTEXTUAL로 유지합니다."
            );

            reasons.add(
                    "Upstream PalaceAnalysis에서 실제 계산된 TenGod, 12운성, 천간·지지 관계, Transformation 후보·활성·원기능 감소 구분은 그대로 인정하며 이 Rule에서 다시 계산하지 않았습니다."
            );

            reasons.add(
                    "MONTH Palace의 공식 위치 의미는 ENVIRONMENTAL_OPERATION까지이며 이를 특정 직장·회사·직종·취업·승진·이직·퇴사 사건으로 확대하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 파싱하여 새로운 TenGod 관계, 직업 구조, 사건 또는 직종을 생성하지 않았습니다."
            );

            reasons.add(
                    "다른 CAREER Source와의 새로운 강화·약화·상쇄·인과·순서 관계를 생성하지 않았습니다."
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

                            ConsultationCategoryDeterministicStatus
                                    .CONTEXTUAL,

                            null,

                            null,

                            List.of(),

                            "원국 MONTH Palace에서 계산된 ENVIRONMENTAL_OPERATION Context를 독립적으로 보존하며 해당 위치 자체에는 직업적 길흉이나 실제 직업 사건을 부여하지 않습니다.",

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
     * Source 판별 조건으로 사용하지 않는다.
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
                        "CAREER Environmental Palace sourceRuleCode가 필요합니다."
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