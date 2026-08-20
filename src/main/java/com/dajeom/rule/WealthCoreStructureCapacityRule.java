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
 * 원국 핵심 재성 구조 / 수용력 결합 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 기존 LifeDomain.WEALTH 결과를
 * ConsultationCategory.WEALTH에
 * 그대로 복사하지 않는다.
 *
 * 이 Rule은 서로 다른 두 deterministic 축:
 *
 * 1.
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * 2.
 * WEALTH_CAPACITY
 *
 * 를 함께 사용해
 *
 * "재성이 원국의 중심 구조로 관여하는 상태와
 * 그 재성 기능을 실제로 감당하는 수용 구조가
 * 서로 어떤 관계를 이루는가"
 *
 * 를 WEALTH 카테고리 관점에서
 * 다시 판단한다.
 *
 * =================================================
 *
 * Source 1
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * StructureAnalysis에서:
 *
 * - DIRECT_WEALTH
 * - INDIRECT_WEALTH
 *
 * 가 실제 primaryStandardTenGod로
 * 선택된 경우의 중심 구조 상태.
 *
 * 이 Source는:
 *
 * - 성립
 * - 부담
 * - 손상
 * - 혼잡
 *
 * 등을 이미 계산한 결과를 제공한다.
 *
 * =================================================
 *
 * Source 2
 * =================================================
 *
 * WEALTH_CAPACITY
 *
 * TenGodCompositeAnalysis
 * +
 * DayMasterStrengthAnalysis
 *
 * 를 통해 이미 확정된:
 *
 * - 재성 발달
 * - 정재/편재 중심
 * - 정재/편재 혼재
 * - 재다신약 부담
 * - 비겁의 재성 압박
 *
 * 등을 사용한다.
 *
 * =================================================
 *
 * 중요한 원칙
 * =================================================
 *
 * 이 Rule은:
 *
 * - 재성 세력을 다시 계산하지 않는다.
 * - 신강신약을 다시 계산하지 않는다.
 * - 격국을 다시 계산하지 않는다.
 * - Match 개수를 점수로 사용하지 않는다.
 * - summary 문자열을 파싱하지 않는다.
 * - reasons 문자열을 파싱하지 않는다.
 *
 * 기존 Java deterministic 결과의
 * 공식 구조 필드만 사용한다.
 *
 * =================================================
 *
 * 결과가 없는 경우
 * =================================================
 *
 * 이 Rule은 두 Source Family가
 * 모두 실제로 존재할 때만 Match를 만든다.
 *
 * 즉:
 *
 * 재성 주격 구조가 없는데
 * 억지로 "재성격" 판단을 만들지 않는다.
 *
 * 또한 재성 수용력 Source가 없는데
 * 상태를 추정하지 않는다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * 이 결과로:
 *
 * - 돈을 많이 번다
 * - 부자가 된다
 * - 투자에 성공한다
 * - 재산이 늘어난다
 * - 돈을 잃는다
 *
 * 같은 실제 사건이나 결과를
 * 확정하지 않는다.
 */
@Component
public class WealthCoreStructureCapacityRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "WEALTH_CORE_STRUCTURE_CAPACITY";

    private static final String
            PRIMARY_WEALTH_STRUCTURE_RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE";

    private static final String
            WEALTH_CAPACITY_RULE_CODE =
            "WEALTH_CAPACITY";

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
                "WealthCoreStructureCapacityRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.WEALTH
        ) {

            throw new IllegalArgumentException(
                    "WealthCoreStructureCapacityRule은 WEALTH View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * 사용할 수 없는 View
         * =================================================
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source 1
         * 재성 주격 중심 구조
         * =================================================
         */

        List<LifeDomainRuleMatch>
                structureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_STRUCTURE_RULE_CODE
                );

        /*
         * =================================================
         * Source 2
         * 재성 수용 / 부담 구조
         * =================================================
         */

        List<LifeDomainRuleMatch>
                capacityMatches =
                findNatalMatches(
                        view,
                        WEALTH_CAPACITY_RULE_CODE
                );

        /*
         * =================================================
         * 두 Source 모두 필수
         * =================================================
         *
         * 재성이 주격이 아닌데
         * 재성 주격 구조를 추정하지 않는다.
         *
         * 또한 Capacity Evidence가 없는데
         * 감당 구조를 임의 생성하지 않는다.
         */

        if (
                structureMatches.isEmpty()
                        || capacityMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source Family별 상태
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                structureStatus =
                resolveFamilyStatus(
                        structureMatches
                );

        ConsultationCategoryDeterministicStatus
                capacityStatus =
                resolveFamilyStatus(
                        capacityMatches
                );

        /*
         * =================================================
         * WEALTH Category 전용 결합
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolveCombinedStatus(
                        structureStatus,
                        capacityStatus
                );

        /*
         * =================================================
         * 실제 사용 Source 보존
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                structureMatches
        );

        sourceMatches.addAll(
                capacityMatches
        );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "재성이 원국의 중심 구조로 관여하는 상태와 재성 기능을 감당하는 수용·부담 구조를 서로 다른 deterministic 축으로 확인했습니다."
        );

        reasons.add(
                "재성 중심 구조 Source Family의 상태는 "
                        + structureStatus.name()
                        + "입니다."
        );

        reasons.add(
                "재성 수용력 Source Family의 상태는 "
                        + capacityStatus.name()
                        + "입니다."
        );

        appendCombinationReason(
                reasons,
                structureStatus,
                capacityStatus,
                finalStatus
        );

        reasons.add(
                "LifeDomain Match 개수를 재물의 강도·크기·수입 규모 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국의 재성 중심축과 수용 구조의 결합 상태를 설명하며 실제 수입, 재산 증가, 투자 성공, 금전 손실 사건을 확정하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.WEALTH,

                        ConsultationCategoryDeterministicLayer
                                .NATAL,

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
     * Natal Source 조회
     * =================================================
     *
     * summary / reasons 문자열은
     * 판단에 사용하지 않는다.
     *
     * 공식 구조 필드:
     *
     * - domain
     * - ruleCode
     *
     * 만 사용한다.
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
     * Source Family Status
     * =================================================
     *
     * Match 수를 비교하지 않는다.
     *
     * 해당 Source Family 안에
     * 어떤 공식 Status가 존재하는지만 본다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveFamilyStatus(
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

        /*
         * 기존 Source가 이미 MIXED를
         * 확정했다면 우선 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * 하나의 Family 안에
         * SUPPORTIVE와 PRESSURE가
         * 동시에 존재해도 MIXED다.
         */

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
     * WEALTH 전용 결합 Matrix
     * =================================================
     *
     * structureStatus:
     *
     * 재성이 원국 중심 구조로
     * 어떤 상태로 관여하는가.
     *
     * capacityStatus:
     *
     * 그 재성 기능을
     * 실제로 감당하는 과정에서
     * 어떤 수용/부담 상태가 존재하는가.
     *
     * =================================================
     *
     * 핵심:
     *
     * 재성격이 성립했다고 해서
     * Capacity 부담을 지워버리지 않는다.
     *
     * 반대로 Capacity가 지지 방향이라고 해서
     * 주격 구조가 성립하지 않은 상태를
     * 재성 중심 구조 SUPPORTIVE로
     * 자동 승격하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveCombinedStatus(

            ConsultationCategoryDeterministicStatus structureStatus,

            ConsultationCategoryDeterministicStatus capacityStatus

    ) {

        /*
         * =================================================
         * 어느 한 축 자체가 MIXED
         * =================================================
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
                        || capacityStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 재성 중심 구조가 SUPPORTIVE
         * =================================================
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            /*
             * 중심축은 성립하지만
             * 실제 감당 구조에 부담이 있다.
             */

            if (
                    capacityStatus
                            == ConsultationCategoryDeterministicStatus.PRESSURE
            ) {

                return ConsultationCategoryDeterministicStatus.MIXED;
            }

            /*
             * Capacity가 CONTEXTUAL이라고 해서
             * 이미 성립된 주격 구조를
             * 무효로 만들지 않는다.
             */

            return ConsultationCategoryDeterministicStatus.SUPPORTIVE;
        }

        /*
         * =================================================
         * 재성 중심 구조가 PRESSURE
         * =================================================
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            /*
             * 중심 구조에는 부담이 있지만
             * 수용 축은 지지된다면
             * 한 방향으로 단순화하지 않는다.
             */

            if (
                    capacityStatus
                            == ConsultationCategoryDeterministicStatus.SUPPORTIVE
            ) {

                return ConsultationCategoryDeterministicStatus.MIXED;
            }

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * =================================================
         * 중심 구조가 CONTEXTUAL
         * =================================================
         *
         * 실제 수용 축에 PRESSURE가 존재한다면
         * 그 부담은 보존한다.
         */

        if (
                capacityStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus.PRESSURE;
        }

        /*
         * Capacity SUPPORTIVE만으로
         * 재성 주격 중심 구조 자체를
         * SUPPORTIVE로 승격하지 않는다.
         */

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
     * Combination Reason
     * =================================================
     */

    private void appendCombinationReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus structureStatus,

            ConsultationCategoryDeterministicStatus capacityStatus,

            ConsultationCategoryDeterministicStatus finalStatus

    ) {

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            reasons.add(
                    "재성 중심축의 성립 방향과 실제 수용·부담 방향이 한쪽으로 일치하지 않거나, 두 축 중 하나에 이미 혼재가 존재하여 MIXED로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "재성 중심 구조 또는 재성 수용 구조에 확인된 부담이 다른 축에서 충분히 상쇄되지 않아 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                finalStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "재성 중심 구조가 성립된 방향이고 수용 구조에서 이를 뒤집는 부담이 확인되지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        reasons.add(
                "재성 중심축의 관여와 수용 구조는 확인되지만 현재 공식 status만으로 한 방향의 재물 운용 구조를 확정하지 않아 CONTEXTUAL로 유지합니다."
        );
    }

    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국에서 재성 중심 구조와 이를 감당하는 수용 구조가 비교적 함께 성립하는 방향입니다.";

            case PRESSURE ->
                    "원국에서 재성 기능이 중요한 축으로 관여하지만 이를 감당하거나 유지하는 과정에 부담 구조가 함께 존재합니다.";

            case MIXED ->
                    "원국에서 재성 중심 구조의 성립 요소와 실제 수용·부담 요소가 함께 작동하는 복합적인 재물 구조입니다.";

            case CONTEXTUAL ->
                    "원국에서 재성 중심축과 수용 구조가 관여하지만 현재 근거만으로 재물 구조를 한 방향으로 단정하지 않습니다.";
        };
    }
}