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
 * WEALTH 원국 재성 중심 구조 deterministic Rule.
 *
 * =================================================
 * 목적
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE에서
 * 이미 계산된 원국 재성 중심 구조의
 * 공식 상태만 WEALTH Category에 보존한다.
 *
 * =================================================
 * 중요한 변경
 * =================================================
 *
 * 기존에는:
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * +
 *
 * WEALTH_CAPACITY
 *
 * 를 결합하여 하나의 finalStatus를 만들었다.
 *
 * 이제 두 Source Family를 합성하지 않는다.
 *
 * =================================================
 * 이 Rule의 공식 Anchor
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 *
 * 만 사용한다.
 *
 * 이 Source는 upstream에서 이미:
 *
 * - DIRECT_WEALTH / INDIRECT_WEALTH가
 *   실제 주격으로 선택되었는지
 *
 * - 재성 주격의 성립 상태
 *
 * - 재성 주격의 손상 상태
 *
 * - 재성 주격의 혼잡 상태
 *
 * - 재성 중심 구조의 부담 상태
 *
 * 등을 계산한 결과다.
 *
 * 이 Rule에서는 그 계산을 반복하지 않는다.
 *
 * =================================================
 * WEALTH_CAPACITY
 * =================================================
 *
 * WEALTH_CAPACITY는 이 Rule에서 사용하지 않는다.
 *
 * 재성 기능의:
 *
 * - 수용력
 * - 재다신약 부담
 * - 비겁의 재성 압박
 * - 정재 / 편재 중심
 *
 * 같은 Capacity 계산은 별도의
 * ConsultationCategory deterministic Rule로
 * 독립 보존한다.
 *
 * =================================================
 * 금지
 * =================================================
 *
 * 다음과 같은 Cross-Source 합성을 하지 않는다.
 *
 * PRIMARY STRUCTURE SUPPORTIVE
 * +
 * CAPACITY PRESSURE
 * =
 * MIXED
 *
 * 금지.
 *
 * PRIMARY STRUCTURE PRESSURE
 * +
 * CAPACITY SUPPORTIVE
 * =
 * MIXED
 *
 * 금지.
 *
 * 또한:
 *
 * - 중심 구조가 수용력을 보완한다
 * - 수용력이 중심 구조를 뒤집는다
 * - 수용력 때문에 재성 구조가 부담이 된다
 * - 두 구조가 함께 작동한다
 *
 * 같은 새로운 관계 의미도 생성하지 않는다.
 *
 * =================================================
 * Status 계산
 * =================================================
 *
 * WEALTH_PRIMARY_WEALTH_STRUCTURE
 * Source Family 내부에서만:
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE가
 * 같은 Source Family 안에 실제 존재
 * -> MIXED
 *
 * PRESSURE만 존재
 * -> PRESSURE
 *
 * SUPPORTIVE만 존재
 * -> SUPPORTIVE
 *
 * 그 외
 * -> CONTEXTUAL
 *
 * =================================================
 * 중요한 경계
 * =================================================
 *
 * 위 처리는 서로 다른 계산 결과를
 * 합성하는 것이 아니다.
 *
 * 동일한 Source Family 안에서
 * upstream이 이미 확정한 공식 Status를
 * Category 수준으로 정규화하는 것이다.
 *
 * =================================================
 * 사건 생성 금지
 * =================================================
 *
 * SUPPORTIVE
 * != 돈을 많이 번다
 * != 부자가 된다
 * != 투자 성공
 *
 * PRESSURE
 * != 돈을 잃는다
 * != 가난하다
 * != 투자 실패
 *
 * MIXED
 * != 재물 기복이 크다
 *
 * CONTEXTUAL
 * != 재물운이 없다
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - 재성 세력 재계산
 * - 신강신약 재계산
 * - 격국 재계산
 * - 용신 재계산
 * - 합충형파해 재계산
 * - 합화 재계산
 * - WEALTH_CAPACITY와 Status 합성
 * - 다른 Source Family와 Status 합성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 * - 재물 사건 생성
 * - 인과 생성
 * - 순서 생성
 */
@Component
public class WealthCoreStructureCapacityRule
        implements ConsultationCategoryDeterministicRule {

    /*
     * =================================================
     * Compatibility
     * =================================================
     *
     * 기존 Grounding / Contract 호환을 먼저
     * 유지하기 위해 class명과 Rule Code는
     * 현재 단계에서는 변경하지 않는다.
     *
     * 실제 계산 의미는 이제
     * PRIMARY WEALTH STRUCTURE 하나만 담당한다.
     *
     * 전체 Category Contract 연결이 끝난 뒤
     * identifier 정리는 별도로 검토한다.
     */

    private static final String RULE_CODE =
            "WEALTH_CORE_STRUCTURE_CAPACITY";

    private static final String
            PRIMARY_WEALTH_STRUCTURE_RULE_CODE =
            "WEALTH_PRIMARY_WEALTH_STRUCTURE";


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

        /*
         * =================================================
         * Category
         * =================================================
         */

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
         * 공식 Status Anchor
         * =================================================
         */

        List<LifeDomainRuleMatch>
                structureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_STRUCTURE_RULE_CODE
                );

        /*
         * 재성 주격 구조가 실제로 존재하지 않으면
         * 이 Rule에서 다른 재물 Evidence를 대신 가져와
         * 재성 중심 구조를 생성하지 않는다.
         */

        if (
                structureMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Status
         * =================================================
         */

        ConsultationCategoryDeterministicStatus status =
                resolveStructureStatus(
                        structureMatches
                );

        /*
         * =================================================
         * Reasons
         * =================================================
         *
         * 사용자 Story가 아니라
         * deterministic provenance 기록이다.
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "WEALTH의 이 원국 항목은 WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family만 공식 Status Anchor로 사용했습니다."
        );

        appendStatusReason(
                reasons,
                structureMatches,
                status
        );

        reasons.add(
                "WEALTH_CAPACITY Source Family는 이 Rule의 Status 계산에 사용하지 않았으며 별도의 deterministic 계산 항목으로 분리합니다."
        );

        reasons.add(
                "재성 중심 구조와 재성 수용력의 Status를 결합하여 새로운 SUPPORTIVE·PRESSURE·MIXED 상태를 만들지 않았습니다."
        );

        reasons.add(
                "Source Match의 반복 개수를 재물 규모·재물운 강도·수입 규모·자산 규모의 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 원국에서 재성이 중심 구조로 관여하는 공식 상태만 보존하며 실제 수입·자산 증가·투자 성공·금전 손실 사건을 생성하지 않습니다."
        );

        /*
         * =================================================
         * Result
         * =================================================
         */

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory.WEALTH,

                        ConsultationCategoryDeterministicLayer.NATAL,

                        RULE_CODE,

                        status,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                status
                        ),

                        List.copyOf(
                                structureMatches
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
     * 공식 구조 필드만 사용한다.
     *
     * - domain
     * - ruleCode
     *
     * summary / reasons 문자열은
     * 판단에 사용하지 않는다.
     */

    private List<LifeDomainRuleMatch>
    findNatalMatches(

            ConsultationCategoryEvidenceView view,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                view,
                "WEALTH Evidence View가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "WEALTH Source ruleCode가 필요합니다."
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
                            "WEALTH Natal Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.WEALTH
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
     * Primary Wealth Structure Status
     * =================================================
     *
     * 서로 다른 Source Family를 넣지 않는다.
     *
     * 오직:
     *
     * WEALTH_PRIMARY_WEALTH_STRUCTURE
     *
     * Family 내부의 공식 Status만 정규화한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveStructureStatus(
            List<LifeDomainRuleMatch> structureMatches
    ) {

        Objects.requireNonNull(
                structureMatches,
                "WEALTH Primary Structure Match 목록이 필요합니다."
        );

        if (
                structureMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "WEALTH Primary Structure Status 계산에는 최소 하나의 Match가 필요합니다."
            );
        }

        boolean supportiveExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * =================================================
         * upstream MIXED 보존
         * =================================================
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus.MIXED;
        }

        /*
         * =================================================
         * 동일 Source Family 내부에
         * 두 공식 방향이 모두 실제 존재
         * =================================================
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
     * Status 존재 여부
     * =================================================
     */

    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "WEALTH Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "LifeDomainSignalStatus가 필요합니다."
        );

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
     * Status Provenance
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            List<LifeDomainRuleMatch> structureMatches,

            ConsultationCategoryDeterministicStatus status

    ) {

        Objects.requireNonNull(
                reasons,
                "WEALTH reasons가 필요합니다."
        );

        Objects.requireNonNull(
                structureMatches,
                "WEALTH Primary Structure Match 목록이 필요합니다."
        );

        Objects.requireNonNull(
                status,
                "WEALTH Primary Structure Status가 필요합니다."
        );

        boolean supportiveExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        structureMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family에 upstream MIXED 상태가 존재하므로 재성 중심 구조 Status를 MIXED로 보존합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family 내부에 SUPPORTIVE와 PRESSURE가 모두 실제 존재하므로 어느 한 방향을 삭제하지 않고 MIXED로 보존합니다."
            );

            return;
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family에 PRESSURE 상태가 존재하고 SUPPORTIVE 또는 MIXED 상태가 없어 재성 중심 구조 Status를 PRESSURE로 확정합니다."
            );

            return;
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family에 SUPPORTIVE 상태가 존재하고 PRESSURE 또는 MIXED 상태가 없어 재성 중심 구조 Status를 SUPPORTIVE로 확정합니다."
            );

            return;
        }

        reasons.add(
                "WEALTH_PRIMARY_WEALTH_STRUCTURE Source Family는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 방향이 확정되지 않아 재성 중심 구조 Status를 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Deterministic Summary
     * =================================================
     *
     * 최종 사용자 Story가 아니다.
     *
     * Capacity와 연결하지 않는다.
     * 재물 사건으로 변환하지 않는다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        Objects.requireNonNull(
                status,
                "WEALTH Primary Structure Status가 필요합니다."
        );

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "원국의 재성 중심 구조에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "원국의 재성 중심 구조에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "원국의 재성 중심 구조에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "원국의 재성 중심 구조가 확인되며 방향은 CONTEXTUAL로 유지됩니다.";
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