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
 * 가까운 인간관계 상호작용 패턴 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 기존 LifeDomain.RELATIONSHIP 결과를
 * SOCIAL_RELATIONSHIP에 그대로 복사하지 않는다.
 *
 * 이 Rule은 원국에서:
 *
 * - 사람이 가까워졌을 때
 * - 관계가 복합적으로 얽힐 때
 * - 관계 안에서 마찰이나 압박이 생길 때
 * - 관계를 한 방향으로 단순화하기 어려울 때
 *
 * 나타나는 상호작용 구조를
 * SOCIAL_RELATIONSHIP 관점으로 다시 판단한다.
 *
 * =================================================
 *
 * 핵심 Source
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * DAY가 실제 원국 관계망에 관여하는지,
 * 그리고 해당 관계망에:
 *
 * - MIXED
 * - PRESSURE
 * - CONTEXTUAL
 *
 * 구조가 존재하는지를 사용한다.
 *
 * =================================================
 *
 * Context Source
 * =================================================
 *
 * RELATIONSHIP_SPOUSE_PALACE
 *
 * 이름 때문에 배우자 전용으로 사용하지 않는다.
 *
 * 기존 18차 Rule의 실제 정책대로
 * 가까운 관계에서 중요하게 참조되는
 * 위치 Context로만 사용한다.
 *
 * =================================================
 *
 * Auxiliary Source
 * =================================================
 *
 * RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
 *
 * 대인 접점·주목·상호작용 Context를
 * 보조하는 근거로만 사용한다.
 *
 * 이 Auxiliary Match는
 * Category Status를 변경하지 않는다.
 *
 * =================================================
 *
 * 중요한 금지
 * =================================================
 *
 * - summary 문자열 검색 금지
 * - reasons 문자열 검색 금지
 * - Match 개수를 관계 강도 점수로 사용 금지
 * - 도화만으로 SUPPORTIVE 판정 금지
 * - DAY 궁위를 배우자 자체로 해석 금지
 * - 연애/결혼/이별 사건 생성 금지
 * - 기존 LifeDomainRuleMatch를 단순 복사 금지
 *
 * =================================================
 *
 * Status 결정
 * =================================================
 *
 * 방향 Status는
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 * Source Family만으로 계산한다.
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
 * 그 외
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * 현재 기존
 * RelationshipNatalRelationOverlapRule은
 * SUPPORTIVE를 생성하지 않지만,
 *
 * 향후 기존 Rule 정책이 확장되어도
 * 의미를 임의 변경하지 않도록
 * SUPPORTIVE도 안전하게 처리한다.
 */
@Component
public class SocialRelationshipInteractionPatternRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_INTERACTION_PATTERN";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

    private static final String
            SPOUSE_PALACE_RULE_CODE =
            "RELATIONSHIP_SPOUSE_PALACE";

    private static final String
            PEACH_BLOSSOM_AUXILIARY_RULE_CODE =
            "RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY";


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
                "SocialRelationshipInteractionPatternRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory
                        .SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipInteractionPatternRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
            );
        }

        /*
         * =================================================
         * 사용할 수 없는 View
         * =================================================
         *
         * unavailable 상태에는 Evidence가 존재하지 않는다는
         * ConsultationCategoryEvidenceView 계약을 그대로 따른다.
         */

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 핵심 원국 관계망 Source
         * =================================================
         *
         * 이 Source가 없으면
         * 이 Rule이 묻는:
         *
         * "가까운 인간관계에서 관계망이
         * 어떻게 작동하는가"
         *
         * 를 계산할 수 없다.
         *
         * PALACE 또는 AUXILIARY만으로
         * 이 Match를 만들지 않는다.
         */

        List<LifeDomainRuleMatch>
                relationOverlapMatches =
                findNatalMatches(
                        view,
                        NATAL_RELATION_OVERLAP_RULE_CODE
                );

        if (
                relationOverlapMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Context Source
         * =================================================
         */

        List<LifeDomainRuleMatch>
                palaceMatches =
                findNatalMatches(
                        view,
                        SPOUSE_PALACE_RULE_CODE
                );

        /*
         * =================================================
         * Auxiliary Source
         * =================================================
         */

        List<LifeDomainRuleMatch>
                peachBlossomMatches =
                findNatalMatches(
                        view,
                        PEACH_BLOSSOM_AUXILIARY_RULE_CODE
                );

        /*
         * =================================================
         * Category Status
         * =================================================
         *
         * Status 방향은
         * 핵심 관계망 Source만 사용한다.
         *
         * Palace / Peach Blossom은
         * 방향을 변경하지 않는다.
         */

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        relationOverlapMatches
                );

        /*
         * =================================================
         * 실제 사용 Source
         * =================================================
         *
         * 핵심 Source
         * +
         * 실제 존재하는 Context
         * +
         * 실제 존재하는 Auxiliary
         *
         * 를 provenance로 보존한다.
         *
         * Match 개수 자체를
         * 강도나 점수로 사용하지 않는다.
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                relationOverlapMatches
        );

        sourceMatches.addAll(
                palaceMatches
        );

        sourceMatches.addAll(
                peachBlossomMatches
        );

        /*
         * =================================================
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "원국의 가까운 관계망에 대한 deterministic 결과를 SOCIAL_RELATIONSHIP의 인간관계 상호작용 관점으로 다시 평가했습니다."
        );

        appendStatusReason(
                reasons,
                relationOverlapMatches
        );

        if (
                !palaceMatches.isEmpty()
        ) {

            reasons.add(
                    "가까운 관계에서 중요하게 참조되는 위치 Context가 실제 원국 관계 Evidence에 포함되어 있습니다."
            );

            reasons.add(
                    "해당 위치 Context는 사람 사이의 가까운 상호작용을 보완하는 근거로 사용하며 배우자 자체 또는 연애·결혼 사건으로 해석하지 않습니다."
            );
        }

        if (
                !peachBlossomMatches.isEmpty()
        ) {

            reasons.add(
                    "대인 접점·주목·상호작용이 두드러지는 Auxiliary Context가 함께 존재합니다."
            );

            reasons.add(
                    "Auxiliary Context는 사람과의 접점에 대한 보조 근거일 뿐 SUPPORTIVE·PRESSURE·MIXED 방향을 변경하지 않습니다."
            );
        }

        reasons.add(
                "서로 다른 LifeDomain 간 Match 개수나 Match 총량을 관계 강도 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "이 결과는 가까운 인간관계에서의 상호작용과 거리 조정 Context를 설명하며 특정 만남·연애·결혼·이별 사건을 생성하지 않습니다."
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
                                status,
                                !peachBlossomMatches.isEmpty()
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
     * summary / reasons 문자열은
     * 판단에 사용하지 않는다.
     *
     * 공식 구조 필드인:
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
     * Match 개수를 비교하지 않는다.
     *
     * 특정 방향이 하나라도 실제 존재하는지만
     * deterministic condition으로 사용한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolveStatus(
            List<LifeDomainRuleMatch> coreMatches
    ) {

        boolean supportiveExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.MIXED
                );

        /*
         * 기존 관계망 Rule이
         * 이미 한 Source 내부에서
         * 결합/압박 혼재를 MIXED로 확정했다면
         * 그 구조를 우선 보존한다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        /*
         * 향후 기존 Rule이 SUPPORTIVE를
         * 생성하게 되는 경우를 대비한다.
         *
         * 서로 반대 방향이 실제 함께 존재할 때만
         * 이 Category Rule이 MIXED를 계산한다.
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

            List<LifeDomainRuleMatch> coreMatches

    ) {

        boolean supportiveExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.SUPPORTIVE
                );

        boolean pressureExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.PRESSURE
                );

        boolean mixedExists =
                hasStatus(
                        coreMatches,
                        LifeDomainSignalStatus.MIXED
                );

        if (
                mixedExists
        ) {

            reasons.add(
                    "기존 원국 관계망 deterministic 결과에 MIXED 구조가 존재하므로 가까운 인간관계에서도 결합되는 요소와 조정이 필요한 요소가 함께 존재하는 Context로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "원국 관계망에 SUPPORTIVE와 PRESSURE 방향이 함께 존재하므로 가까운 인간관계의 상호작용은 한 방향으로 단순화하지 않고 MIXED로 판단합니다."
            );

            return;
        }

        if (
                pressureExists
        ) {

            reasons.add(
                    "원국 관계망에 직접적인 PRESSURE 구조가 존재하므로 가까운 인간관계에서 거리 조정과 마찰 관리가 필요한 방향으로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
        ) {

            reasons.add(
                    "원국 관계망에 SUPPORTIVE 구조가 존재하고 별도의 PRESSURE 또는 MIXED 방향이 확인되지 않아 가까운 인간관계의 상호작용을 지지 방향으로 판단합니다."
            );

            return;
        }

        reasons.add(
                "원국 관계망의 관여는 확인되지만 SUPPORTIVE·PRESSURE·MIXED 중 하나의 방향으로 확정할 근거가 없어 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     *
     * Grounding이 이후
     * 생활언어 상담문으로 변환할 수 있도록
     * Category 의미만 요약한다.
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus status,

            boolean interactionAuxiliaryExists

    ) {

        String base =
                switch (
                        status
                        ) {

                    case SUPPORTIVE ->
                            "가까운 인간관계에서 상호작용과 거리 조절이 비교적 지지되는 구조가 존재합니다.";

                    case PRESSURE ->
                            "가까운 인간관계에서 관계가 깊어질수록 거리 조절과 마찰 관리가 필요한 구조가 존재합니다.";

                    case MIXED ->
                            "가까운 인간관계에서 서로 연결되는 요소와 거리 조정이 필요한 요소가 함께 존재하는 복합적인 상호작용 구조입니다.";

                    case CONTEXTUAL ->
                            "가까운 인간관계의 상호작용 구조가 원국에 관여하지만 한 방향의 관계 성향으로 단정할 수준은 아닙니다.";
                };

        if (
                interactionAuxiliaryExists
        ) {

            return base
                    + " 사람과의 접점이나 상호작용이 두드러지는 보조 Context도 함께 존재합니다.";
        }

        return base;
    }
}