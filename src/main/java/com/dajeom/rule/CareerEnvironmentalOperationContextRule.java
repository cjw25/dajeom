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
 * 원국 환경·사회적 작동 Context Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * 기존 18차:
 *
 * CAREER_ENVIRONMENTAL_PALACE_CONTEXT
 *
 * 를 단순히 CAREER Category로 복사하지 않는다.
 *
 * 기존 Source는 12차 PalaceAnalysis의
 * MONTH Palace를 사용하며,
 *
 * MONTH
 * =
 * ENVIRONMENTAL_OPERATION
 *
 * 즉:
 *
 * - 외부 환경과 맞물리는 방식
 * - 사회적 작동 위치
 * - 역할이 작동하는 환경 Context
 * - 조직·사회 조건과 접촉하는 위치
 *
 * 를 보존한다.
 *
 * 이 Category Rule에서는
 * 별도의 실제 CAREER 구조 Anchor가 존재하는 경우에만
 * 해당 MONTH Context를
 * 직업·사회 기능의 환경 작동 Context로 채택한다.
 *
 * =================================================
 * 구조 Anchor
 * =================================================
 *
 * 다음 중 최소 하나가 필요하다.
 *
 * 1.
 * CAREER_PRIMARY_OFFICER_STRUCTURE
 *
 * 2.
 * CAREER_OFFICER_STRUCTURE
 *
 * 3.
 * CAREER_CROSS_VALIDATED_PATTERN
 *
 * 따라서:
 *
 * MONTH Palace Context 존재
 *
 * ->
 *
 * CAREER Category Match 자동 생성
 *
 * 을 하지 않는다.
 *
 * 실제 직업·사회 구조가 확인된 경우에만
 * 환경 Context를 연결한다.
 *
 * =================================================
 * Status
 * =================================================
 *
 * 이 Rule의 final status는
 * 항상 CONTEXTUAL이다.
 *
 * MONTH Palace는:
 *
 * - 직업 성공
 * - 직업 실패
 * - 조직 적응 성공
 * - 이직 가능성
 *
 * 같은 방향성 Evidence가 아니기 때문이다.
 *
 * =================================================
 * 방향성 전이 금지
 * =================================================
 *
 * 예를 들어:
 *
 * CAREER_OFFICER_STRUCTURE = PRESSURE
 *
 * 라고 해도
 *
 * MONTH Palace 자체를 PRESSURE로 바꾸지 않는다.
 *
 * 반대로:
 *
 * CAREER_PRIMARY_OFFICER_STRUCTURE = SUPPORTIVE
 *
 * 라고 해도
 *
 * MONTH Palace 자체를 SUPPORTIVE로 바꾸지 않는다.
 *
 * 방향성 판단은:
 *
 * CareerCoreRoleStructureRule
 * CareerFunctionIntegrationPatternRule
 *
 * 등의 책임이다.
 *
 * =================================================
 * 전통명리 해석 제한
 * =================================================
 *
 * MONTH
 * ->
 * 직장
 *
 * MONTH 정관
 * ->
 * 승진
 *
 * MONTH 칠살
 * ->
 * 조직 갈등
 *
 * MONTH 재성
 * ->
 * 돈 버는 직업
 *
 * MONTH 충
 * ->
 * 이직
 *
 * MONTH 합
 * ->
 * 취업
 *
 * 같은 사건 대응을 만들지 않는다.
 *
 * =================================================
 * 12운성 / 관계 / 합화
 * =================================================
 *
 * 기존 CareerEnvironmentalPalaceContextRule이
 * 이미 보존하는:
 *
 * - 12운성
 * - 천간 관계
 * - 지지 관계
 * - 합화 Context
 * - 합화 Activation
 * - 원기능 감소
 *
 * 등을 이 Rule에서 다시 길흉으로 해석하지 않는다.
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
 * 를 파싱해서
 * 십성, 관계, 합화, 사건 등을
 * 재구성하지 않는다.
 *
 * =================================================
 * Match 개수
 * =================================================
 *
 * Match 개수는:
 *
 * - 직업 능력
 * - 성공 가능성
 * - 승진 가능성
 * - 조직 적응력
 * - 사회적 지위
 *
 * 점수가 아니다.
 */
@Component
public class CareerEnvironmentalOperationContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "CAREER_ENVIRONMENTAL_OPERATION_CONTEXT";

    /*
     * =================================================
     * 환경 Context Source
     * =================================================
     */

    private static final String
            ENVIRONMENTAL_PALACE_RULE_CODE =
            "CAREER_ENVIRONMENTAL_PALACE_CONTEXT";

    /*
     * =================================================
     * CAREER 구조 Anchor
     * =================================================
     */

    private static final String
            PRIMARY_OFFICER_STRUCTURE_RULE_CODE =
            "CAREER_PRIMARY_OFFICER_STRUCTURE";

    private static final String
            OFFICER_STRUCTURE_RULE_CODE =
            "CAREER_OFFICER_STRUCTURE";

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
                "CareerEnvironmentalOperationContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category 검증
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
         * unavailable View
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * MONTH 환경·사회적 작동 Context
         * =================================================
         */

        List<LifeDomainRuleMatch>
                environmentalMatches =
                findNatalMatches(
                        view,
                        ENVIRONMENTAL_PALACE_RULE_CODE
                );

        /*
         * MONTH 환경 Context가 없다면
         * 이번 질문에 답할 수 없다.
         */

        if (
                environmentalMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * CAREER 구조 Anchor
         * =================================================
         */

        List<LifeDomainRuleMatch>
                primaryStructureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_OFFICER_STRUCTURE_RULE_CODE
                );

        List<LifeDomainRuleMatch>
                officerStructureMatches =
                findNatalMatches(
                        view,
                        OFFICER_STRUCTURE_RULE_CODE
                );

        List<LifeDomainRuleMatch>
                crossValidatedMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        /*
         * =================================================
         * 최소 하나의 독립 CAREER Anchor 필요
         * =================================================
         */

        boolean careerAnchorExists =
                !primaryStructureMatches.isEmpty()
                        || !officerStructureMatches.isEmpty()
                        || !crossValidatedMatches.isEmpty();

        if (
                !careerAnchorExists
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source Provenance
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                environmentalMatches
        );

        sourceMatches.addAll(
                primaryStructureMatches
        );

        sourceMatches.addAll(
                officerStructureMatches
        );

        sourceMatches.addAll(
                crossValidatedMatches
        );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "원국에서 MONTH 환경·사회적 작동 Palace Context가 확인되었고 별도의 CAREER 구조 Anchor도 존재하여, 직업·사회 기능이 외부 환경과 맞물리는 위치 Context로 채택했습니다."
        );

        appendAnchorReasons(
                reasons,
                primaryStructureMatches,
                officerStructureMatches,
                crossValidatedMatches
        );

        reasons.add(
                "MONTH Palace의 공식 위치 의미는 ENVIRONMENTAL_OPERATION이며 특정 직장, 회사, 직종 또는 직업 성패로 치환하지 않았습니다."
        );

        reasons.add(
                "MONTH Palace에 연결된 십성, 12운성, 천간·지지 관계, 합화 Context를 이 Rule에서 다시 SUPPORTIVE 또는 PRESSURE로 변환하지 않았습니다."
        );

        reasons.add(
                "CAREER 구조 Anchor의 방향성 Status 역시 환경 위치 자체의 방향성으로 전이하지 않았습니다."
        );

        reasons.add(
                "환경·사회적 작동 Context와 직업 구조가 함께 존재한다는 사실만 보존하며 취업, 승진, 이직, 퇴사, 조직 갈등 같은 실제 사건을 생성하지 않습니다."
        );

        reasons.add(
                "LifeDomain Match 개수를 직업 적합도, 조직 적응력, 사회적 성공, 승진 가능성 또는 직업 안정성 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과의 역할은 직업·사회 기능과 원국의 환경·사회적 작동 위치 사이의 연결 Context를 보존하는 것이므로 final status는 CONTEXTUAL로 유지합니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
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

                        "원국의 직업·사회 기능이 MONTH의 환경·사회적 작동 Context와 연결되어 있으나 그 위치 자체에는 직업적 길흉이나 사건 방향을 부여하지 않습니다.",

                        List.copyOf(
                                sourceMatches
                        ),

                        List.copyOf(
                                reasons
                        )
                )
        );
    }

    /*
     * =================================================
     * Natal Source 조회
     * =================================================
     *
     * 공식 구조값:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons는
     * Source 판단에 사용하지 않는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                sourceRuleCode == null
                        || sourceRuleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "CAREER Source ruleCode가 필요합니다."
            );
        }

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : view.natalMatches()
        ) {

            Objects.requireNonNull(
                    match,
                    "CAREER Natal Match는 null일 수 없습니다."
            );

            /*
             * 다른 LifeDomain Evidence는
             * 이번 Rule의 Source가 될 수 없다.
             */

            if (
                    match.domain()
                            != LifeDomain.CAREER
            ) {

                continue;
            }

            /*
             * 공식 ruleCode 기준으로만
             * Source Family를 구분한다.
             */

            if (
                    !sourceRuleCode.equals(
                            match.ruleCode()
                    )
            ) {

                continue;
            }

            result.add(
                    match
            );
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Anchor Provenance
     * =================================================
     *
     * Anchor 존재 여부만 기록한다.
     *
     * Match 개수나 Status를
     * 환경 Context의 방향성으로
     * 변환하지 않는다.
     */

    private void appendAnchorReasons(

            List<String> reasons,

            List<LifeDomainRuleMatch>
                    primaryStructureMatches,

            List<LifeDomainRuleMatch>
                    officerStructureMatches,

            List<LifeDomainRuleMatch>
                    crossValidatedMatches

    ) {

        Objects.requireNonNull(
                reasons,
                "reasons가 필요합니다."
        );

        if (
                !primaryStructureMatches.isEmpty()
        ) {

            reasons.add(
                    "정관·칠살의 주격 중심 구조 Evidence가 존재하여 MONTH 환경 Context를 직업·사회 영역과 연결할 중심 구조 Anchor가 확인됩니다."
            );
        }

        if (
                !officerStructureMatches.isEmpty()
        ) {

            reasons.add(
                    "관살의 복합 작동 구조 Evidence가 존재하여 MONTH 환경 Context를 직업·사회 영역과 연결할 역할·책임 Anchor가 확인됩니다."
            );
        }

        if (
                !crossValidatedMatches.isEmpty()
        ) {

            reasons.add(
                    "교차검증된 CAREER Pattern Evidence가 존재하여 MONTH 환경 Context를 직업·사회 기능 연결과 함께 볼 Pattern Anchor가 확인됩니다."
            );
        }
    }
}