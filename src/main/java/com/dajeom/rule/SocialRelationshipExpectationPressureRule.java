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
 * SOCIAL_RELATIONSHIP의
 * 기대·역할·현실적 요구 수용 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 일반적인 인간관계에서:
 *
 * - 상대의 기대
 * - 역할 요구
 * - 책임
 * - 현실적인 주고받음
 * - 반복되는 관계 부담
 *
 * 이 커졌을 때
 * 이를 받아들이고 조정하는 구조를
 * SOCIAL_RELATIONSHIP 관점에서
 * deterministic하게 다시 판단한다.
 *
 * =================================================
 *
 * 사용하는 기존 공식 Source Family
 * =================================================
 *
 * 1.
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 원국 중심 구조에서:
 *
 * - 현실적 주고받음
 * - 생활적 기대
 * - 책임
 * - 약속
 * - 역할
 * - 기준
 *
 * 을 다루는 기능의
 * 성립·부담·손상·혼잡 Context.
 *
 * -------------------------------------------------
 *
 * 2.
 * RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * 실제 복합구조에서:
 *
 * - 현실적 자원 요구
 * - 역할·책임
 * - 통제 요구
 * - 수용 부담
 *
 * 에 관한 deterministic Context.
 *
 * =================================================
 *
 * 중요한 원칙
 * =================================================
 *
 * 기존 두 Rule의 Match 개수를
 * 강도 점수로 사용하지 않는다.
 *
 * 동일 Rule Code 아래 Match가 여러 개 있어도
 * 몇 개가 발생했는지를 비교하지 않는다.
 *
 * 각 Source Family 안에서
 * 어떤 공식 LifeDomainSignalStatus가
 * 실제 존재하는지만 사용한다.
 *
 * =================================================
 *
 * 문자열 파싱 금지
 * =================================================
 *
 * 다음은 사용하지 않는다.
 *
 * - summary.contains(...)
 * - reasons.contains(...)
 * - 내부 Pattern 이름 문자열 검색
 *
 * =================================================
 *
 * Category 의미
 * =================================================
 *
 * 이 Rule은:
 *
 * "연애가 잘 되는가"
 * "배우자운이 좋은가"
 *
 * 를 판단하지 않는다.
 *
 * 일반적인 사람 관계에서
 * 기대와 책임이 커질 때
 * 그 요구를 처리하는 구조를 판단한다.
 *
 * =================================================
 *
 * Status 계산 원칙
 * =================================================
 *
 * 어느 Source Family에서든
 * MIXED가 실제 존재하면
 * 복합 구조를 보존하기 위해 MIXED.
 *
 * PRESSURE와 SUPPORTIVE가
 * 서로 다른 또는 같은 Source Family에
 * 함께 존재하면 MIXED.
 *
 * PRESSURE가 존재하고
 * SUPPORTIVE가 없으면 PRESSURE.
 *
 * SUPPORTIVE가 존재하고
 * PRESSURE/MIXED가 없으면 SUPPORTIVE.
 *
 * 방향성이 확정되지 않는
 * CONTEXTUAL 근거만 존재하면 CONTEXTUAL.
 *
 * =================================================
 *
 * 주의
 * =================================================
 *
 * Engine이 상태를 자동 합산하는 것이 아니다.
 *
 * 이 Rule 자체가
 *
 * "인간관계에서 기대·책임·현실 요구를
 * 감당하는 방식"
 *
 * 이라는 하나의 명확한 질문에 대해
 * 두 독립 deterministic Source Family를
 * 조합하여 Category Status를 계산한다.
 */
@Component
public class SocialRelationshipExpectationPressureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_EXPECTATION_PRESSURE";

    private static final String
            PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String
            TEN_GOD_CONTEXT_RULE_CODE =
            "RELATIONSHIP_TEN_GOD_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory
                .SOCIAL_RELATIONSHIP;
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
                "SocialRelationshipExpectationPressureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory
                        .SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipExpectationPressureRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source Family 조회
         * =================================================
         */

        List<LifeDomainRuleMatch>
                structureMatches =
                findNatalMatches(
                        view,
                        PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE
                );

        List<LifeDomainRuleMatch>
                tenGodMatches =
                findNatalMatches(
                        view,
                        TEN_GOD_CONTEXT_RULE_CODE
                );

        /*
         * =================================================
         * 둘 다 없으면 계산 불가
         * =================================================
         *
         * 다른 관계 Rule을 대신 가져오지 않는다.
         *
         * 이 Rule의 질문에 필요한
         * 공식 Source Family가 없으면
         * Match를 생성하지 않는다.
         */

        if (
                structureMatches.isEmpty()
                        && tenGodMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 실제 사용 Source
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                structureMatches
        );

        sourceMatches.addAll(
                tenGodMatches
        );

        /*
         * =================================================
         * Category Status
         * =================================================
         */

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        sourceMatches
                );

        /*
         * =================================================
         * Reason
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "원국의 관계영역 deterministic 결과 중 현실적 주고받음·기대·역할·책임과 직접 관련된 공식 Source Family를 SOCIAL_RELATIONSHIP 관점으로 조합했습니다."
        );

        if (
                !structureMatches.isEmpty()
        ) {

            reasons.add(
                    "원국 중심 구조에서 현실적 주고받음과 역할·책임을 다루는 구조 Evidence가 실제로 존재합니다."
            );
        }

        if (
                !tenGodMatches.isEmpty()
        ) {

            reasons.add(
                    "관계에서 현실적 요구와 역할·책임의 수용 부담을 설명하는 복합구조 Evidence가 실제로 존재합니다."
            );
        }

        appendStatusReason(
                reasons,
                sourceMatches
        );

        reasons.add(
                "각 Source Family의 Match 개수를 서로 비교하거나 관계 강도 점수로 환산하지 않았습니다."
        );

        reasons.add(
                "기존 Match의 summary 또는 reasons 문자열을 검색하여 새로운 사주 사실을 만들지 않았습니다."
        );

        reasons.add(
                "이 결과는 일반적인 인간관계에서 기대·책임·현실적 요구가 커질 때 나타나는 수용과 조정 구조를 설명합니다."
        );

        reasons.add(
                "이 결과를 연애 성공·배우자 유무·결혼·이별 같은 특정 관계 사건으로 해석하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory
                                .SOCIAL_RELATIONSHIP,

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
     * 구조 필드만 사용한다.
     *
     * - LifeDomain
     * - ruleCode
     *
     * summary / reasons는
     * Rule 선택 조건으로 사용하지 않는다.
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
                    "SOCIAL_RELATIONSHIP Natal Match는 null일 수 없습니다."
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
     * Category Status 계산
     * =================================================
     *
     * Match 개수는 사용하지 않는다.
     *
     * 공식 Status의 존재 여부만 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveStatus(
            List<LifeDomainRuleMatch> sourceMatches
    ) {

        boolean supportiveExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * 이미 upstream deterministic Rule에서
         * MIXED로 확정한 구조가 존재한다면
         * 해당 복합성을 삭제하지 않는다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        /*
         * 기대·책임 처리 구조 안에서
         * 지지 방향과 부담 방향이
         * 실제로 함께 존재한다면
         * 이 Category 질문의 답을 MIXED로 계산한다.
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

            List<LifeDomainRuleMatch> sourceMatches

    ) {

        boolean supportiveExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        sourceMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "기존 deterministic 결과에 MIXED 상태가 존재하므로 인간관계의 기대·역할·현실 요구를 처리하는 구조에도 성립과 부담 또는 복합성이 함께 존재하는 것으로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "기대·역할·현실 요구와 관련된 공식 Source에서 SUPPORTIVE와 PRESSURE 방향이 함께 존재하므로 한 방향으로 단순화하지 않고 MIXED로 판단합니다."
            );

            return;
        }

        if (
                pressureExists
        ) {

            reasons.add(
                    "기대·역할·현실 요구와 관련된 공식 Source에 PRESSURE가 존재하므로 사람 사이에서 요구와 책임이 커질 때 부담과 조정 필요성이 커지는 방향으로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
        ) {

            reasons.add(
                    "기대·역할·현실 요구와 관련된 공식 Source에 SUPPORTIVE가 존재하고 PRESSURE 또는 MIXED 방향이 확인되지 않아 해당 요구를 다루는 구조를 지지 방향으로 판단합니다."
            );

            return;
        }

        reasons.add(
                "기대·역할·현실 요구와 관련된 구조는 존재하지만 SUPPORTIVE·PRESSURE·MIXED 중 하나의 방향으로 확정할 근거가 없어 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자용 storyText가 아니다.
     *
     * 이후 Grounding이
     * 생활언어 상담문으로 변환하기 위한
     * Category deterministic 요약이다.
     */

    private String summaryOf(
            ConsultationCategoryDeterministicStatus status
    ) {

        return switch (
                status
                ) {

            case SUPPORTIVE ->
                    "사람 사이에서 기대·역할·현실적인 요구가 커져도 이를 비교적 구조적으로 받아들이고 조정할 수 있는 근거가 존재합니다.";

            case PRESSURE ->
                    "사람 사이에서 기대·역할·현실적인 요구가 커질수록 이를 감당하고 조정하는 과정에 부담이 커질 수 있는 구조가 존재합니다.";

            case MIXED ->
                    "사람 사이의 기대·역할·현실적인 요구를 다루는 능력과 부담 요소가 함께 존재하여 상황에 따라 반응 차이가 커질 수 있는 복합 구조입니다.";

            case CONTEXTUAL ->
                    "사람 사이의 기대·역할·현실적인 요구를 다루는 구조가 원국에 관여하지만 한 방향의 대인 반응으로 단정할 수준은 아닙니다.";
        };
    }
}