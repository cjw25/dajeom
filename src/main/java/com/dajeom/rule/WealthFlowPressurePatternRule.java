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
 * WEALTH 카테고리의
 * 재물 흐름 / 경쟁 / 부담 Pattern Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * WealthCoreStructureCapacityRule과
 * 다른 질문을 담당한다.
 *
 * WealthCoreStructureCapacityRule:
 *
 * - 재성 주격 구조
 * - 재성 수용력
 *
 * 을 함께 보며
 *
 * "재성이 원국의 중심축으로 성립하는가"
 * +
 * "그 기능을 감당할 수 있는가"
 *
 * 를 본다.
 *
 * -------------------------------------------------
 *
 * 이 Rule:
 *
 * WEALTH_CROSS_VALIDATED_PATTERN에서
 * 이미 교차검증된 재성 관련 Pattern들이
 * 원국에서 어떤 방향으로 남아 있는지를 본다.
 *
 * =================================================
 *
 * 주요 Source 의미
 * =================================================
 *
 * WEALTH_CROSS_VALIDATED_PATTERN은
 * 기존 18차에서 이미:
 *
 * - 식상 -> 재성 Family Chain
 * - 비겁과 재성의 경쟁 / 분산 구조
 * - 재성의 무게와 일간 수용력 부담
 * - 재성 -> 관성 연결
 * - 식상 -> 재성 -> 관살 -> 인성 전체 Chain
 *
 * 등을 계산했다.
 *
 * 또한 해당 Pattern은
 * 이미:
 *
 * - 격국
 * - 신강신약
 * - 용신
 * - 합화
 *
 * 등과 교차검증된 상태를 사용한다.
 *
 * =================================================
 *
 * 매우 중요한 제한
 * =================================================
 *
 * LifeDomainRuleMatch에는
 * WEALTH_CROSS_VALIDATED_PATTERN 내부의
 * 개별 Pattern subtype을 나타내는
 * 구조화된 필드가 없다.
 *
 * 따라서 이 Rule은 절대로:
 *
 * - summary 문자열
 * - reasons 문자열
 *
 * 을 파싱해서
 *
 * "이 Match는 비겁쟁재"
 * "이 Match는 재다신약"
 * "이 Match는 식상생재"
 *
 * 라고 재분류하지 않는다.
 *
 * 현재 공식적으로 사용할 수 있는 것은:
 *
 * - domain
 * - ruleCode
 * - status
 *
 * 뿐이다.
 *
 * =================================================
 *
 * Palace Context
 * =================================================
 *
 * WEALTH_PALACE_DISTRIBUTION_CONTEXT가
 * 존재하면 Source Provenance에는 함께 보존한다.
 *
 * 그러나 Palace Match는 원래
 * 항상 CONTEXTUAL이며
 * 재물의 길흉 방향을 결정하지 않는다.
 *
 * 따라서 Palace Context는
 * 이 Rule의 final status를 변경하지 않는다.
 *
 * =================================================
 *
 * Match 개수
 * =================================================
 *
 * Match 개수가 많다고:
 *
 * - 재물운이 강하다
 * - 재물이 많다
 * - 부담이 더 세다
 *
 * 고 판단하지 않는다.
 *
 * 오직 공식 status의 존재 조합만 사용한다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * 이 Rule은:
 *
 * - 돈을 번다
 * - 돈을 잃는다
 * - 사업에 성공한다
 * - 투자가 성공한다
 * - 재산이 증가한다
 * - 재산이 감소한다
 *
 * 같은 실제 사건을 확정하지 않는다.
 */
@Component
public class WealthFlowPressurePatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_FLOW_PRESSURE_PATTERN";

    private static final String
            CROSS_VALIDATED_PATTERN_RULE_CODE =
            "WEALTH_CROSS_VALIDATED_PATTERN";

    private static final String
            PALACE_DISTRIBUTION_RULE_CODE =
            "WEALTH_PALACE_DISTRIBUTION_CONTEXT";

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
                "WealthFlowPressurePatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthFlowPressurePatternRule은 WEALTH View만 평가할 수 있습니다."
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
         * 핵심 Source
         * =================================================
         *
         * 실제 방향 판단은
         * WEALTH_CROSS_VALIDATED_PATTERN에서만
         * 가져온다.
         */

        List<LifeDomainRuleMatch>
                patternMatches =
                findNatalMatches(
                        view,
                        CROSS_VALIDATED_PATTERN_RULE_CODE
                );

        /*
         * 교차검증 Pattern 자체가 없다면
         * 이 Rule의 질문에 답할 근거가 없다.
         */

        if (
                patternMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 보조 Context
         * =================================================
         *
         * 재성이 어느 Palace Context에
         * 분포하는지는 Provenance로 보존한다.
         *
         * 단:
         *
         * Palace Distribution은
         * final status에 영향을 주지 않는다.
         */

        List<LifeDomainRuleMatch>
                palaceMatches =
                findNatalMatches(
                        view,
                        PALACE_DISTRIBUTION_RULE_CODE
                );

        /*
         * =================================================
         * Pattern Family 상태
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolvePatternStatus(
                        patternMatches
                );

        /*
         * =================================================
         * Source 보존
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                patternMatches
        );

        sourceMatches.addAll(
                palaceMatches
        );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "재성의 주격 성립 여부를 다시 계산하지 않고, 기존 WEALTH_CROSS_VALIDATED_PATTERN에서 이미 교차검증된 재물 관련 흐름·경쟁·부담 구조를 별도 축으로 사용했습니다."
        );

        reasons.add(
                "WEALTH_CROSS_VALIDATED_PATTERN 내부의 개별 Pattern subtype은 현재 LifeDomainRuleMatch에 구조화되어 있지 않으므로 summary 또는 reasons 문자열을 파싱해 재분류하지 않았습니다."
        );

        appendStatusReason(
                reasons,
                finalStatus
        );

        if (
                !palaceMatches.isEmpty()
        ) {

            reasons.add(
                    "재성 기능이 배치된 Palace Context가 존재하여 Source Provenance에는 함께 보존했지만, Palace Distribution은 방향성 Evidence가 아니므로 final status를 변경하지 않았습니다."
            );

        } else {

            reasons.add(
                    "현재 원국 Evidence에는 사용할 수 있는 WEALTH_PALACE_DISTRIBUTION_CONTEXT가 없어 교차검증 Pattern만으로 판단했습니다."
            );
        }

        reasons.add(
                "LifeDomain Match 개수의 많고 적음을 재물의 강도, 금액 규모, 성공 가능성 또는 위험 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 재성 관련 구조적 흐름과 경쟁·부담 Context를 설명하며 실제 수입, 손실, 투자 성과 또는 재산 사건을 확정하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.WEALTH,

                        ConsultationCategoryDeterministicLayer.NATAL,

                        RULE_CODE,

                        finalStatus,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                finalStatus
                        ),

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
     * Natal Match 조회
     * =================================================
     *
     * 문자열 의미해석을 하지 않는다.
     *
     * 공식 구조값인:
     *
     * - domain
     * - ruleCode
     *
     * 로만 Source Family를 찾는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

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

            if (
                    match.domain()
                            != LifeDomain.WEALTH
            ) {

                continue;
            }

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
     * Cross Validated Pattern Status
     * =================================================
     *
     * Match 수를 비교하지 않는다.
     *
     * Source Family 내부에
     * 어떤 공식 status가 존재하는지만 본다.
     *
     * 우선순위:
     *
     * 1. MIXED 존재
     *
     * 2. SUPPORTIVE + PRESSURE 동시 존재
     *
     * 3. PRESSURE 존재
     *
     * 4. SUPPORTIVE 존재
     *
     * 5. 그 외 CONTEXTUAL
     *
     * 현재 WEALTH_CROSS_VALIDATED_PATTERN의
     * 특성상 SUPPORTIVE가 없어도
     * 이 resolver는 공식 status 전체를
     * 안전하게 처리하도록 작성한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolvePatternStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        boolean supportiveExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        matches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        return ConsultationCategoryDeterministicStatus.CONTEXTUAL;
    }

    /*
     * =================================================
     * Status 존재 여부
     * =================================================
     */

    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        return matches
                .stream()
                .anyMatch(
                        match ->
                                match.status()
                                        == status
                );
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus status

    ) {

        switch (
                status
        ) {

            case SUPPORTIVE ->

                    reasons.add(
                            "교차검증된 재성 관련 구조에서 SUPPORTIVE 방향이 확인되고 이를 뒤집는 PRESSURE 또는 MIXED 상태가 없어 SUPPORTIVE로 판단합니다."
                    );

            case PRESSURE ->

                    reasons.add(
                            "교차검증된 재성 관련 Pattern Family에서 구조적 경쟁·부담 방향의 PRESSURE가 확인되어 이를 보존합니다."
                    );

            case MIXED ->

                    reasons.add(
                            "교차검증된 재성 관련 Pattern Family에 이미 MIXED가 존재하거나 서로 다른 방향의 상태가 함께 존재하여 한쪽으로 단순화하지 않고 MIXED로 판단합니다."
                    );

            case CONTEXTUAL ->

                    reasons.add(
                            "재성 관련 흐름 또는 연쇄 구조는 확인되지만 현재 공식 status만으로 지원이나 부담 방향을 확정할 수 없어 CONTEXTUAL로 유지합니다."
                    );
        }
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 사용자에게 직접 보여주는
     * 최종 Story 문장이 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 교차검증된 재성 관련 흐름이 비교적 지지되는 방향으로 유지됩니다.";

            case PRESSURE ->
                    "원국의 재성 관련 흐름에서 교차검증된 경쟁·수용 부담 구조가 확인됩니다.";

            case MIXED ->
                    "원국의 재성 관련 흐름에서 연결 구조와 부담 또는 상반된 조건이 함께 작동합니다.";

            case CONTEXTUAL ->
                    "원국에서 재성 관련 연쇄와 분포 Context가 확인되지만 현재 근거만으로 재물 구조의 방향을 단정하지 않습니다.";
        };
    }
}