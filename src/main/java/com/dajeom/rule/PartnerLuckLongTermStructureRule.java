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
 * 관계 카테고리 정밀화.
 *
 * PARTNER_LUCK의
 * 장기 관계 구조 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * PARTNER_LUCK을
 * 기존 RELATIONSHIP 결과의 단순 복사본으로
 * 만들지 않는다.
 *
 * 이 Rule은 원국에서:
 *
 * 1.
 * 가까운 관계 자체의
 * 결합·마찰·중첩 구조
 *
 * +
 *
 * 2.
 * 장기 관계를 유지할 때 필요한
 * 현실적 주고받음·약속·책임·역할 구조
 *
 * 가 실제로 함께 존재할 때
 *
 * 장기적인 가까운 관계를
 * 유지하고 조정하는 구조를
 * PARTNER_LUCK 관점으로
 * deterministic하게 판단한다.
 *
 * =================================================
 *
 * 핵심 Source 1
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * DAY가 관여하는 가까운 관계망에서:
 *
 * - 관계 중첩
 * - 결합과 압박의 혼재
 * - 직접적인 압박
 * - 마찰 Context
 *
 * 가 어떻게 존재하는지를 사용한다.
 *
 * =================================================
 *
 * 핵심 Source 2
 * =================================================
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 원국 중심 구조에서:
 *
 * - 현실적인 주고받음
 * - 생활적 기대
 * - 약속
 * - 책임
 * - 역할
 * - 관계 안의 기준
 *
 * 을 다루는 기능이
 * 어떤 상태로 존재하는지를 사용한다.
 *
 * =================================================
 *
 * Context Source
 * =================================================
 *
 * RELATIONSHIP_SPOUSE_PALACE
 *
 * 이름 때문에:
 *
 * DAY 궁위 = 배우자
 *
 * 로 사용하지 않는다.
 *
 * 기존 18차 정책 그대로
 * 가까운 관계와 밀접 상호작용에서
 * 중요하게 참조되는 위치 Context로만 사용한다.
 *
 * 이 Source는
 * Category Status를 직접 변경하지 않는다.
 *
 * =================================================
 *
 * 중요한 차이
 * =================================================
 *
 * SOCIAL_RELATIONSHIP의
 * 상호작용 Pattern Rule은
 * 원국 관계망 자체를 중심으로 판단한다.
 *
 * 이 Rule은 반드시:
 *
 * 관계망 구조
 * +
 * 장기적인 역할·책임 구조
 *
 * 두 Source Family가 모두 존재해야 한다.
 *
 * 따라서 같은 관계 Match에
 * PARTNER_LUCK 이름만 붙이는 구조가 아니다.
 *
 * =================================================
 *
 * Match 생성 조건
 * =================================================
 *
 * 다음 두 Source Family가
 * 모두 실제 존재해야 한다.
 *
 * - RELATIONSHIP_NATAL_RELATION_OVERLAP
 * - RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 하나라도 없으면
 * 이 Rule은 Match를 생성하지 않는다.
 *
 * 다른 관계 Evidence를 대신 끌어와
 * 조건을 채우지 않는다.
 *
 * =================================================
 *
 * Status 계산
 * =================================================
 *
 * 두 핵심 Source Family의
 * 공식 LifeDomainSignalStatus만 사용한다.
 *
 * MIXED 존재
 * -> MIXED
 *
 * SUPPORTIVE + PRESSURE 동시 존재
 * -> MIXED
 *
 * PRESSURE 존재
 * -> PRESSURE
 *
 * SUPPORTIVE 존재
 * -> SUPPORTIVE
 *
 * 방향성이 확정되지 않는
 * CONTEXTUAL 근거만 존재
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * - Match 개수를 강도 점수로 사용
 * - summary 문자열 검색
 * - reasons 문자열 검색
 * - DAY 궁위를 배우자 자체로 해석
 * - SUPPORTIVE = 결혼 성공
 * - PRESSURE = 이별
 * - MIXED = 관계 불안정 확정
 * - 배우자 등장 시기 예측
 * - 결혼 여부 예측
 */
@Component
public class PartnerLuckLongTermStructureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_LONG_TERM_STRUCTURE";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

    private static final String
            PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String
            SPOUSE_PALACE_RULE_CODE =
            "RELATIONSHIP_SPOUSE_PALACE";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory
                .PARTNER_LUCK;
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
                "PartnerLuckLongTermStructureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckLongTermStructureRule은 PARTNER_LUCK View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 핵심 Source 1
         *
         * 가까운 관계망의 실제 구조
         * =================================================
         */

        List<LifeDomainRuleMatch>
                relationOverlapMatches =
                findNatalMatches(
                        view,
                        NATAL_RELATION_OVERLAP_RULE_CODE
                );

        /*
         * =================================================
         * 핵심 Source 2
         *
         * 현실적 주고받음 / 약속 / 책임 / 역할
         * =================================================
         */

        List<LifeDomainRuleMatch>
                structureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE
                );

        /*
         * =================================================
         * 두 핵심 Source가 모두 필요
         * =================================================
         *
         * 장기 관계 구조라는 Category 질문은
         *
         * 가까운 관계망
         * +
         * 역할·책임 구조
         *
         * 가 동시에 확인될 때만 계산한다.
         *
         * 한쪽이 없다고
         * PALACE나 다른 RELATIONSHIP Match를
         * 대신 사용하지 않는다.
         */

        if (
                relationOverlapMatches.isEmpty()
                        || structureMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 위치 Context
         * =================================================
         *
         * 방향 계산에는 사용하지 않는다.
         */

        List<LifeDomainRuleMatch>
                palaceMatches =
                findNatalMatches(
                        view,
                        SPOUSE_PALACE_RULE_CODE
                );

        /*
         * =================================================
         * 방향 계산 Source
         * =================================================
         */

        List<LifeDomainRuleMatch>
                directionalMatches =
                new ArrayList<>();

        directionalMatches.addAll(
                relationOverlapMatches
        );

        directionalMatches.addAll(
                structureMatches
        );

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        directionalMatches
                );

        /*
         * =================================================
         * Provenance
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                relationOverlapMatches
        );

        sourceMatches.addAll(
                structureMatches
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
                "원국의 가까운 관계망 구조와 현실적 주고받음·약속·책임·역할을 다루는 중심 구조가 모두 확인되어 PARTNER_LUCK의 장기 관계 구조를 계산했습니다."
        );

        reasons.add(
                "가까운 관계의 결합·마찰·중첩을 다루는 공식 관계망 Source Family가 실제 존재합니다."
        );

        reasons.add(
                "장기적인 관계에서 현실적 기대·약속·책임·역할을 다루는 공식 중심 구조 Source Family가 실제 존재합니다."
        );

        if (
                !palaceMatches.isEmpty()
        ) {

            reasons.add(
                    "가까운 관계와 밀접 상호작용에서 중요하게 참조되는 위치 Context도 함께 존재합니다."
            );

            reasons.add(
                    "해당 위치 Context는 장기 관계가 작동하는 배경 근거로만 사용하며 배우자 자체나 결혼 사건으로 해석하지 않습니다."
            );
        }

        appendStatusReason(
                reasons,
                directionalMatches
        );

        reasons.add(
                "관계망 Match와 역할·책임 Match의 개수를 비교하거나 장기 관계의 강도 점수로 환산하지 않았습니다."
        );

        reasons.add(
                "기존 LifeDomainRuleMatch의 summary 또는 reasons 문자열을 검색하여 새로운 관계 사실을 만들지 않았습니다."
        );

        reasons.add(
                "이 결과는 장기적인 가까운 관계에서 관계 구조와 현실적 역할·책임 구조가 함께 작동하는 방식을 설명합니다."
        );

        reasons.add(
                "이 결과를 배우자 존재 여부·결혼 성사·이별·재결합 같은 실제 사건으로 확정하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory
                                .PARTNER_LUCK,

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
     * 판단에 사용하는 구조:
     *
     * - domain
     * - ruleCode
     *
     * summary / reasons는
     * Source 판별에 사용하지 않는다.
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
                    "PARTNER_LUCK Natal Match는 null일 수 없습니다."
            );

            if (
                    match.domain()
                            != LifeDomain.RELATIONSHIP
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
     * Category Status
     * =================================================
     *
     * 두 핵심 Source Family 안에서
     * 공식 Status가 실제 존재하는지만 사용한다.
     *
     * Match 개수는 사용하지 않는다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveStatus(
            List<LifeDomainRuleMatch> directionalMatches
    ) {

        boolean supportiveExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * Upstream deterministic Rule에서
         * 이미 MIXED가 확정되어 있다면
         * 복합성을 제거하지 않는다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        /*
         * 가까운 관계 구조와
         * 장기 역할 구조를 함께 봤을 때
         * 지지와 부담 방향이 동시에 존재하면
         * 장기 관계 구조를 MIXED로 판단한다.
         */

        if (
                supportiveExists
                        && pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        if (
                pressureExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .PRESSURE;
        }

        if (
                supportiveExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .SUPPORTIVE;
        }

        return ConsultationCategoryDeterministicStatus
                .CONTEXTUAL;
    }


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
     * Status Reason
     * =================================================
     */

    private void appendStatusReason(

            List<String> reasons,

            List<LifeDomainRuleMatch> directionalMatches

    ) {

        boolean supportiveExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        directionalMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "가까운 관계망 또는 역할·책임 중심 구조에 기존 MIXED 판단이 존재하므로 장기 관계에서도 유지 요소와 조정이 필요한 요소가 함께 존재하는 복합 구조로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "가까운 관계망과 역할·책임 구조를 함께 보았을 때 SUPPORTIVE와 PRESSURE 방향이 동시에 존재하므로 장기 관계 구조를 한 방향으로 단순화하지 않고 MIXED로 판단합니다."
            );

            return;
        }

        if (
                pressureExists
        ) {

            reasons.add(
                    "가까운 관계망과 역할·책임 구조를 함께 보았을 때 PRESSURE 방향이 존재하므로 장기 관계에서 관계 조정이나 책임 부담 관리가 중요해지는 구조로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
        ) {

            reasons.add(
                    "가까운 관계망과 역할·책임 구조를 함께 보았을 때 SUPPORTIVE 방향이 존재하고 PRESSURE 또는 MIXED 방향이 확인되지 않아 장기 관계 유지 구조를 지지 방향으로 판단합니다."
            );

            return;
        }

        reasons.add(
                "가까운 관계망과 역할·책임 구조가 모두 실제 존재하지만 SUPPORTIVE·PRESSURE·MIXED 중 하나의 방향으로 확정할 근거가 없어 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     *
     * 특정 결혼 사건을 만들지 않고
     * 장기적인 관계 유지 구조만 요약한다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "가까운 관계의 상호작용과 현실적인 역할·책임 구조를 함께 보았을 때 장기 관계를 유지하고 조정하는 데 지지되는 구조적 근거가 존재합니다.";

            case PRESSURE ->
                    "가까운 관계의 상호작용과 현실적인 역할·책임이 함께 누적될 때 관계 조정이나 부담 관리가 중요해지는 장기 관계 구조가 존재합니다.";

            case MIXED ->
                    "장기적인 가까운 관계에서 연결과 유지에 도움이 되는 요소와 역할·책임 또는 관계 조정 부담이 함께 존재하는 복합 구조입니다.";

            case CONTEXTUAL ->
                    "가까운 관계망과 장기적인 역할·책임 구조가 모두 관여하지만 관계 유지 방향을 한쪽으로 단정할 수준은 아닙니다.";
        };
    }
}