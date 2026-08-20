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
 * WEALTH 카테고리의
 * 재성 기능 배치 / 생활축 Context Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 이 Rule은 재물의:
 *
 * - 성립
 * - 강약
 * - 성공 가능성
 * - 수입 규모
 * - 손실 가능성
 *
 * 을 다시 계산하지 않는다.
 *
 * 대신 기존 18차에서 이미 계산된:
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 를 이용하여,
 *
 * "재성 기능이 원국의 어느 생활축 Context에
 * 실제로 배치되어 있는가"
 *
 * 를 WEALTH 카테고리의
 * 독립적인 Context 축으로 보존한다.
 *
 * =================================================
 *
 * 단순 복사 금지
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT가
 * 존재한다는 이유만으로
 * ConsultationCategory Match를 만들지 않는다.
 *
 * 반드시 다음 재물 구조 Anchor 중
 * 최소 하나가 함께 존재해야 한다.
 *
 * 1.
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * 2.
 * WEALTH_CAPACITY
 *
 * 3.
 * WEALTH_CROSS_VALIDATED_PATTERN
 *
 * 즉:
 *
 * 단순 위치 정보
 *
 * ->
 *
 * WEALTH Category 복사
 *
 * 를 하지 않는다.
 *
 * 원국에서 실제 재물 구조가 확인된 경우에만
 * 그 재성 기능의 Palace 배치를
 * 별도 상담 Context로 채택한다.
 *
 * =================================================
 *
 * Palace Source
 * =================================================
 *
 * 기존:
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT
 *
 * 는 PalaceAnalysis의 공식 Context를 사용한다.
 *
 * YEAR
 * -> BACKGROUND_FOUNDATION
 *
 * MONTH
 * -> ENVIRONMENTAL_OPERATION
 *
 * DAY
 * -> CORE_LIFE_AXIS
 *
 * HOUR
 * -> EXTENSION_LATER_DEVELOPMENT
 *
 * =================================================
 *
 * 매우 중요한 해석 제한
 * =================================================
 *
 * 이 Rule에서는:
 *
 * YEAR 재성
 * -> 집안이 부유하다
 *
 * MONTH 재성
 * -> 직장에서 돈을 많이 번다
 *
 * DAY 재성
 * -> 배우자가 돈이 많다
 *
 * HOUR 재성
 * -> 말년에 부자가 된다
 *
 * 같은 사건식 해석을 만들지 않는다.
 *
 * =================================================
 *
 * Status
 * =================================================
 *
 * 이 Rule의 최종 Status는
 * 항상 CONTEXTUAL이다.
 *
 * 이유:
 *
 * 재성의 위치는
 * "어디에서 기능이 관여하는가"
 * 를 보여주는 Context이지,
 *
 * 그 자체로:
 *
 * SUPPORTIVE
 * PRESSURE
 *
 * 를 결정하는 방향성 Evidence가 아니기 때문이다.
 *
 * =================================================
 *
 * Anchor Status
 * =================================================
 *
 * 재물 구조 Anchor들의 Status 역시
 * 이번 Rule의 final status를 변경하지 않는다.
 *
 * 예:
 *
 * WEALTH_CAPACITY = PRESSURE
 *
 * 라고 하더라도
 *
 * Palace 위치 자체까지
 * PRESSURE로 변환하지 않는다.
 *
 * 해당 부담 판단은
 * WealthCoreStructureCapacityRule 또는
 * WealthFlowPressurePatternRule의 책임이다.
 *
 * =================================================
 *
 * 문자열 파싱 금지
 * =================================================
 *
 * LifeDomainRuleMatch의:
 *
 * - summary
 * - reasons
 *
 * 문자열을 파싱하여
 *
 * YEAR / MONTH / DAY / HOUR
 *
 * 를 재구성하지 않는다.
 *
 * 기존 Source Match 자체를
 * Provenance로 보존한다.
 *
 * =================================================
 *
 * Match 개수
 * =================================================
 *
 * 재성이 여러 Palace에 있다고 해서:
 *
 * - 재물복이 더 크다
 * - 돈이 더 많다
 * - 재물운이 더 강하다
 *
 * 로 판단하지 않는다.
 *
 * Match 개수는 방향성 또는 강도 점수가 아니다.
 *
 * =================================================
 *
 * 시간 미상
 * =================================================
 *
 * HOUR가 알려지지 않은 경우
 * 기존 PalaceAnalysis 및
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT가
 * HOUR를 생성하지 않는다.
 *
 * 이 Rule도 누락된 HOUR를
 * 임의 추정하지 않는다.
 */
@Component
public class WealthFunctionDistributionContextRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_FUNCTION_DISTRIBUTION_CONTEXT";

    /*
     * =================================================
     * Palace Source
     * =================================================
     */

    private static final String
            PALACE_DISTRIBUTION_RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT";

    /*
     * =================================================
     * Wealth Structure Anchors
     * =================================================
     *
     * Palace Context만 존재한다고 해서
     * Category Match를 만들지 않기 위한
     * 독립적인 구조 Anchor.
     */

    private static final String
            PRIMARY_WEALTH_STRUCTURE_RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE";

    private static final String
            WEALTH_CAPACITY_RULE_CODE =
            "WEALTH_CAPACITY";

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN";

    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.WEALTH;
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
                "WealthFunctionDistributionContextRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        /*
         * =================================================
         * Category 검증
         * =================================================
         */

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthFunctionDistributionContextRule은 WEALTH View만 평가할 수 있습니다."
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
         * Palace Distribution
         * =================================================
         */

        List<LifeDomainRuleMatch>
                palaceMatches =
                findNatalMatches(
                        view,
                        PALACE_DISTRIBUTION_RULE_CODE
                );

        /*
         * 재성 Palace 배치가 없다면
         * 이번 질문에 답할 근거가 없다.
         */

        if (
                palaceMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 구조 Anchor
         * =================================================
         *
         * 최소 하나의 독립적인 WEALTH 구조가
         * 실제로 확인되어야 한다.
         */

        List<LifeDomainRuleMatch>
                primaryStructureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_STRUCTURE_RULE_CODE
                );

        List<LifeDomainRuleMatch>
                capacityMatches =
                findNatalMatches(
                        view,
                        WEALTH_CAPACITY_RULE_CODE
                );

        List<LifeDomainRuleMatch>
                crossValidatedMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        boolean structureAnchorExists =
                !primaryStructureMatches.isEmpty()
                        || !capacityMatches.isEmpty()
                        || !crossValidatedMatches.isEmpty();

        /*
         * =================================================
         * 단순 Palace 복사 방지
         * =================================================
         */

        if (
                !structureAnchorExists
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 실제 사용 Source 보존
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                palaceMatches
        );

        sourceMatches.addAll(
                primaryStructureMatches
        );

        sourceMatches.addAll(
                capacityMatches
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
                "원국에서 재성 기능의 Palace 배치가 실제로 확인되었고, 별도의 재물 구조 Anchor도 함께 존재하여 이를 WEALTH의 생활축 배치 Context로 채택했습니다."
        );

        appendAnchorReasons(
                reasons,
                primaryStructureMatches,
                capacityMatches,
                crossValidatedMatches
        );

        reasons.add(
                "Palace 위치는 재성 기능이 원국의 어느 생활 Context에 관여하는지를 보존하는 근거이며 그 위치 자체를 재물의 길흉 방향으로 변환하지 않았습니다."
        );

        reasons.add(
                "YEAR, MONTH, DAY, HOUR 위치를 가족 재산, 직장 수입, 배우자 재산, 말년 재산 같은 실제 사건으로 치환하지 않았습니다."
        );

        reasons.add(
                "Palace Source의 summary 또는 reasons 문자열을 파싱하여 위치나 사건 의미를 새로 생성하지 않았습니다."
        );

        reasons.add(
                "재성이 배치된 Palace Match 개수의 많고 적음을 재물의 크기, 재산 규모, 성공 가능성 또는 재물운 강도로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과의 역할은 재성 기능의 생활축 배치 Context를 보존하는 것이므로 최종 status는 방향성을 부여하지 않고 CONTEXTUAL로 유지합니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.WEALTH,

                        ConsultationCategoryDeterministicLayer
                                .NATAL,

                        RULE_CODE,

                        ConsultationCategoryDeterministicStatus
                                .CONTEXTUAL,

                        null,

                        null,

                        List.of(),

                        "원국에서 재성 기능이 실제 재물 구조와 함께 여러 생활축 Context에 배치되어 있으며, 그 위치 자체에는 길흉 방향을 부여하지 않습니다.",

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
     * 공식 구조 필드:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
     *
     * summary / reasons 문자열은
     * Source 선택에 사용하지 않는다.
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
                    "WEALTH Source ruleCode가 필요합니다."
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
                    "WEALTH Natal Match는 null일 수 없습니다."
            );

            /*
             * 다른 LifeDomain의 Match는
             * 이 Rule의 Source가 될 수 없다.
             */

            if (
                    match.domain()
                            != LifeDomain.WEALTH
            ) {

                continue;
            }

            /*
             * 공식 ruleCode로만
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
     * Anchor Provenance Reason
     * =================================================
     *
     * 각 Anchor의 존재 여부만 기록한다.
     *
     * Anchor Match 수를 세어
     * 강도 점수로 사용하지 않는다.
     */

    private void appendAnchorReasons(

            List<String> reasons,

            List<LifeDomainRuleMatch>
                    primaryStructureMatches,

            List<LifeDomainRuleMatch>
                    capacityMatches,

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
                    "재성 주격 중심 구조 Evidence가 존재하여 재성 기능의 Palace 배치를 해석할 독립적인 구조 Anchor가 확인됩니다."
            );
        }

        if (
                !capacityMatches.isEmpty()
        ) {

            reasons.add(
                    "재성 수용·부담 구조 Evidence가 존재하여 재성 기능의 Palace 배치를 해석할 독립적인 수용력 Anchor가 확인됩니다."
            );
        }

        if (
                !crossValidatedMatches.isEmpty()
        ) {

            reasons.add(
                    "교차검증된 재성 Pattern Evidence가 존재하여 재성 기능의 Palace 배치를 해석할 독립적인 구조적 흐름 Anchor가 확인됩니다."
            );
        }
    }
}