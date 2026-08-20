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
 * LOVE_STYLE의
 * 친밀도 상승 시 반응 구조 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 연애 성향을 단순히
 * 기존 RELATIONSHIP 결과의 복사본으로
 * 만들지 않는다.
 *
 * 이 Rule은:
 *
 * - 관계가 가까워졌을 때
 * - 친밀감이 높아졌을 때
 * - 가까운 관계 안에서 마찰이 생겼을 때
 * - 현실적인 기대나 역할 요구가 함께 커졌을 때
 *
 * 나타나는 반응 구조를
 * LOVE_STYLE 관점에서
 * deterministic하게 판단한다.
 *
 * =================================================
 *
 * 중요한 제한
 * =================================================
 *
 * 현재 공식 Evidence만으로는:
 *
 * - 먼저 고백하는가
 * - 감정을 직접 표현하는가
 * - 연락을 자주 하는가
 * - 상대 반응을 어떻게 확인하는가
 *
 * 같은 구체적 행동을
 * deterministic하게 확정할 수 없다.
 *
 * 따라서 그런 내용을
 * 이 Rule에서 새로 만들어내지 않는다.
 *
 * =================================================
 *
 * 핵심 Source
 * =================================================
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 *
 * 가까운 관계망에:
 *
 * - 결합과 압박의 혼재
 * - 직접적인 압박
 * - 관계 중첩 Context
 *
 * 가 존재하는지를 사용한다.
 *
 * =================================================
 *
 * 추가 핵심 Source
 * =================================================
 *
 * RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * 가까운 관계에서:
 *
 * - 현실적 요구
 * - 역할
 * - 책임
 * - 통제
 * - 수용 부담
 *
 * 등이 친밀한 관계 안에서
 * 부담 Context로 작동하는지를
 * 함께 사용한다.
 *
 * =================================================
 *
 * Context Source
 * =================================================
 *
 * RELATIONSHIP_SPOUSE_PALACE
 *
 * 이름을 배우자 전용 의미로 사용하지 않는다.
 *
 * 기존 18차 정책대로
 * 가까운 관계와 밀접 상호작용에서
 * 중요하게 참조되는 위치 Context로만 사용한다.
 *
 * 이 Source의 CONTEXTUAL Match는
 * 단독으로 LOVE_STYLE 방향을 결정하지 않는다.
 *
 * =================================================
 *
 * Auxiliary Source
 * =================================================
 *
 * RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY
 *
 * 사람과의 접점·주목·상호작용 Context를
 * 보조하는 근거로만 사용한다.
 *
 * 이 Source는
 * LOVE_STYLE Status를 변경하지 않는다.
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * - summary 문자열 검색
 * - reasons 문자열 검색
 * - 도화 = 연애 성공
 * - DAY 궁위 = 배우자
 * - PRESSURE = 이별
 * - MIXED = 불안정한 연애
 * - Match 개수를 강도 점수로 사용
 * - 특정 연애 사건 생성
 *
 * =================================================
 *
 * Status 계산
 * =================================================
 *
 * 방향 계산에는:
 *
 * RELATIONSHIP_NATAL_RELATION_OVERLAP
 * +
 * RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * 두 Source Family만 사용한다.
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
 * 방향성 Source가 CONTEXTUAL뿐이면
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * 이 Rule이 묻는 질문
 * =================================================
 *
 * "사람을 전반적으로 어떻게 대하는가?"
 *
 * 가 아니다.
 *
 * "관계가 가까워지고 친밀도가 높아졌을 때,
 * 관계 안의 마찰·기대·역할 요구에
 * 어떤 구조적 반응이 나타나는가?"
 *
 * 를 계산한다.
 */
@Component
public class LoveStyleClosenessResponseRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "LOVE_STYLE_CLOSENESS_RESPONSE";

    private static final String
            NATAL_RELATION_OVERLAP_RULE_CODE =
            "RELATIONSHIP_NATAL_RELATION_OVERLAP";

    private static final String
            TEN_GOD_CONTEXT_RULE_CODE =
            "RELATIONSHIP_TEN_GOD_CONTEXT";

    private static final String
            SPOUSE_PALACE_RULE_CODE =
            "RELATIONSHIP_SPOUSE_PALACE";

    private static final String
            PEACH_BLOSSOM_AUXILIARY_RULE_CODE =
            "RELATIONSHIP_PEACH_BLOSSOM_AUXILIARY";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory
                .LOVE_STYLE;
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
                "LoveStyleClosenessResponseRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.LOVE_STYLE
        ) {

            throw new IllegalArgumentException(
                    "LoveStyleClosenessResponseRule은 LOVE_STYLE View만 평가할 수 있습니다."
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
         * 가까운 관계망
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
         * 가까운 관계의 현실 요구 / 역할 / 부담
         * =================================================
         */

        List<LifeDomainRuleMatch>
                tenGodMatches =
                findNatalMatches(
                        view,
                        TEN_GOD_CONTEXT_RULE_CODE
                );

        /*
         * =================================================
         * 최소 핵심 근거
         * =================================================
         *
         * 둘 중 하나도 없다면
         * 이 Rule이 묻는 친밀도 상승 반응을
         * 계산할 공식 Source가 없다.
         *
         * PALACE나 AUXILIARY만으로
         * LOVE_STYLE Match를 만들지 않는다.
         */

        if (
                relationOverlapMatches.isEmpty()
                        && tenGodMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 가까운 관계 위치 Context
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
         * Auxiliary Context
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
                tenGodMatches
        );

        ConsultationCategoryDeterministicStatus status =
                resolveStatus(
                        directionalMatches
                );

        /*
         * =================================================
         * 실제 Provenance
         * =================================================
         */

        List<LifeDomainRuleMatch> sourceMatches =
                new ArrayList<>();

        sourceMatches.addAll(
                relationOverlapMatches
        );

        sourceMatches.addAll(
                tenGodMatches
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
                "원국의 가까운 관계망과 관계 안의 현실적 요구·역할·책임에 대한 deterministic 결과를 LOVE_STYLE의 친밀도 상승 반응 관점으로 조합했습니다."
        );

        if (
                !relationOverlapMatches.isEmpty()
        ) {

            reasons.add(
                    "가까운 관계망에서 결합·마찰·중첩 또는 압박 방향을 설명하는 공식 원국 Evidence가 실제로 존재합니다."
            );
        }

        if (
                !tenGodMatches.isEmpty()
        ) {

            reasons.add(
                    "가까운 관계에서 현실적인 요구·역할·책임과 그 수용 부담을 설명하는 공식 deterministic Evidence가 실제로 존재합니다."
            );
        }

        if (
                !palaceMatches.isEmpty()
        ) {

            reasons.add(
                    "가까운 관계와 밀접 상호작용에서 중요하게 참조되는 위치 Context가 함께 존재합니다."
            );

            reasons.add(
                    "해당 위치 Context는 친밀한 관계가 작동하는 배경 근거로 사용하며 배우자 자체 또는 특정 연애 사건으로 해석하지 않습니다."
            );
        }

        if (
                !peachBlossomMatches.isEmpty()
        ) {

            reasons.add(
                    "사람과의 접점·주목·상호작용이 두드러지는 Auxiliary Context가 함께 존재합니다."
            );

            reasons.add(
                    "Auxiliary Context는 친밀한 관계의 접점에 대한 보조 근거이며 LOVE_STYLE의 방향 Status를 변경하지 않습니다."
            );
        }

        appendStatusReason(
                reasons,
                directionalMatches
        );

        reasons.add(
                "Match의 총 개수나 서로 다른 Source Family의 Match 수를 친밀감 또는 연애 강도의 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "기존 Match의 summary 또는 reasons 문자열을 검색하여 감정 표현 방식이나 연애 행동을 새로 추론하지 않았습니다."
        );

        reasons.add(
                "이 결과는 관계가 가까워졌을 때 나타나는 구조적 반응을 설명하며 연애 시작·고백·결혼·이별 같은 실제 사건을 확정하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory
                                .LOVE_STYLE,

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
                    "LOVE_STYLE Natal Match는 null일 수 없습니다."
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
     * Status 계산
     * =================================================
     *
     * Match 개수는 사용하지 않는다.
     *
     * 공식 LifeDomainSignalStatus가
     * 실제 존재하는지만 사용한다.
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
         * Upstream에서 이미 MIXED로
         * 결정된 구조를 삭제하지 않는다.
         */

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        /*
         * 친밀한 관계의 반응이라는
         * 동일한 Category 질문 안에서
         * 지지 방향과 부담 방향이
         * 실제 함께 존재하면 MIXED.
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
                    "가까운 관계망 또는 관계 안의 현실적 요구·역할 구조에 기존 MIXED 판단이 존재하므로 친밀도가 높아질 때 한 방향으로 단순화하기 어려운 복합 반응 구조로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            reasons.add(
                    "친밀한 관계와 관련된 공식 Source에서 SUPPORTIVE와 PRESSURE 방향이 함께 존재하므로 가까워질수록 지지 요소와 조정이 필요한 요소가 함께 작동하는 MIXED 구조로 판단합니다."
            );

            return;
        }

        if (
                pressureExists
        ) {

            reasons.add(
                    "친밀한 관계와 관련된 공식 Source에 PRESSURE가 존재하므로 관계가 가까워지고 요구가 커질수록 거리 조절이나 부담 관리가 중요해지는 방향으로 판단합니다."
            );

            return;
        }

        if (
                supportiveExists
        ) {

            reasons.add(
                    "친밀한 관계와 관련된 공식 Source에 SUPPORTIVE가 존재하고 PRESSURE 또는 MIXED 방향이 확인되지 않아 관계가 가까워질 때의 반응 구조를 지지 방향으로 판단합니다."
            );

            return;
        }

        reasons.add(
                "가까운 관계와 관련된 구조는 실제 존재하지만 SUPPORTIVE·PRESSURE·MIXED 중 하나의 방향으로 확정할 근거가 없어 CONTEXTUAL로 유지합니다."
        );
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     *
     * 감정 표현 방식이나 구체적인 연애 행동을
     * 새로 만들어내지 않는다.
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
                            "관계가 가까워지고 친밀도가 높아질 때 상호작용과 관계 안의 요구를 비교적 안정적으로 받아들이는 구조적 근거가 존재합니다.";

                    case PRESSURE ->
                            "관계가 가까워질수록 상대의 기대·역할·현실적 요구에 대한 부담과 거리 조절 필요성이 커질 수 있는 구조가 존재합니다.";

                    case MIXED ->
                            "관계가 가까워질 때 연결과 친밀감을 받아들이는 요소와 부담·거리 조절이 필요한 요소가 함께 작동하는 복합적인 반응 구조입니다.";

                    case CONTEXTUAL ->
                            "관계가 가까워질 때의 반응 구조가 원국에 관여하지만 감정이나 연애 행동을 한 방향으로 단정할 수준은 아닙니다.";
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