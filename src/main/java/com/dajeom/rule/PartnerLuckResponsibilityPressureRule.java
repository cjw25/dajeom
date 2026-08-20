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
 * 장기 관계 역할·책임 수용 Rule.
 *
 * =================================================
 *
 * 목적
 * =================================================
 *
 * 장기적인 가까운 관계에서는
 * 단순한 친밀감뿐 아니라:
 *
 * - 반복되는 역할
 * - 책임 분담
 * - 현실적인 주고받음
 * - 생활적인 기대
 * - 관계 안의 기준
 * - 지속적으로 감당해야 하는 요구
 *
 * 가 함께 작동한다.
 *
 * 이 Rule은:
 *
 * "장기 관계에서 역할과 책임 구조가
 * 어떻게 형성되어 있고,
 * 그 요구를 실제로 받아들이는 과정에서
 * 어떤 부담 구조가 존재하는가?"
 *
 * 를 deterministic하게 판단한다.
 *
 * =================================================
 *
 * 핵심 Source 1
 * 역할·책임 구조축
 * =================================================
 *
 * RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 *
 * 원국 중심 구조에서:
 *
 * - 현실적 주고받음
 * - 생활적인 기대
 * - 약속
 * - 책임
 * - 역할
 * - 관계 안의 기준
 *
 * 을 다루는 기능의
 * 성립·부담·손상·혼잡 상태를 사용한다.
 *
 * 이 Source는:
 *
 * "장기 관계에서 역할 구조 자체가
 * 어떤 상태인가?"
 *
 * 를 담당한다.
 *
 * =================================================
 *
 * 핵심 Source 2
 * 수용 부담축
 * =================================================
 *
 * RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * 실제 복합구조에서:
 *
 * - 현실적인 요구
 * - 자원 부담
 * - 역할·책임
 * - 통제 요구
 * - 수용 부담
 *
 * 에 관한 deterministic 결과를 사용한다.
 *
 * 이 Source는:
 *
 * "그 역할과 요구를 실제로 감당하는 과정에
 * 어떤 부담 Context가 존재하는가?"
 *
 * 를 담당한다.
 *
 * =================================================
 *
 * SOCIAL_RELATIONSHIP과의 차이
 * =================================================
 *
 * SOCIAL_RELATIONSHIP의
 * EXPECTATION_PRESSURE Rule은:
 *
 * 일반적인 사람 관계에서
 * 기대와 책임이 커질 때
 * 전체 방향을 판단한다.
 *
 * -------------------------------------------------
 *
 * 이 PARTNER_LUCK Rule은:
 *
 * 장기 관계의 역할 구조축
 * +
 * 실제 수용 부담축
 *
 * 을 서로 다른 축으로 먼저 판정한 뒤
 * 두 축의 조합으로 결과를 계산한다.
 *
 * 따라서 동일 Source를 사용하더라도
 * 계산 목적과 상태 결합 방식이 다르다.
 *
 * =================================================
 *
 * 최소 조건
 * =================================================
 *
 * 반드시 다음 두 Source Family가
 * 모두 존재해야 한다.
 *
 * - RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE
 * - RELATIONSHIP_TEN_GOD_CONTEXT
 *
 * 하나라도 없으면
 * 이 Rule은 Match를 생성하지 않는다.
 *
 * =================================================
 *
 * Source Family 내부 상태 정규화
 * =================================================
 *
 * 각 Family 안에서는
 * Match 개수를 사용하지 않는다.
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
 * 두 축의 최종 조합
 * =================================================
 *
 * 역할 구조 자체가 MIXED
 * 또는
 * 수용 구조 자체가 MIXED
 * -> MIXED
 *
 * -------------------------------------------------
 *
 * 역할 구조 SUPPORTIVE
 * +
 * 수용 부담 PRESSURE
 * -> MIXED
 *
 * 역할 구조는 성립하지만
 * 실제 감당 과정에는 부담이 존재하는
 * 복합 구조다.
 *
 * -------------------------------------------------
 *
 * 역할 구조 PRESSURE
 * +
 * 수용 부담 PRESSURE
 * -> PRESSURE
 *
 * 두 축이 모두 같은 부담 방향이므로
 * 단순히 반대 방향 혼재로 보지 않는다.
 *
 * -------------------------------------------------
 *
 * 역할 구조 CONTEXTUAL
 * +
 * 수용 부담 PRESSURE
 * -> PRESSURE
 *
 * 역할 구조의 방향은 확정되지 않았지만
 * 실제 수용 부담은 확인된 상태다.
 *
 * -------------------------------------------------
 *
 * 역할 구조 SUPPORTIVE
 * +
 * 수용 구조 CONTEXTUAL
 * -> SUPPORTIVE
 *
 * 역할 구조는 비교적 성립되어 있고
 * 별도의 부담 방향이 확인되지 않은 상태다.
 *
 * -------------------------------------------------
 *
 * 둘 다 CONTEXTUAL
 * -> CONTEXTUAL
 *
 * =================================================
 *
 * 금지
 * =================================================
 *
 * - Match 개수 비교
 * - Match 총량 점수화
 * - summary 문자열 검색
 * - reasons 문자열 검색
 * - SUPPORTIVE = 결혼 성공
 * - PRESSURE = 이별
 * - MIXED = 관계 불안정 확정
 * - 배우자 존재 여부 판단
 * - 결혼 시기 예측
 * - 실제 관계 사건 생성
 */
@Component
public class PartnerLuckResponsibilityPressureRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_RESPONSIBILITY_PRESSURE";

    private static final String
            PRIMARY_WEALTH_OFFICER_STRUCTURE_RULE_CODE =
            "RELATIONSHIP_PRIMARY_WEALTH_OFFICER_STRUCTURE";

    private static final String
            TEN_GOD_CONTEXT_RULE_CODE =
            "RELATIONSHIP_TEN_GOD_CONTEXT";


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
                "PartnerLuckResponsibilityPressureRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckResponsibilityPressureRule은 PARTNER_LUCK View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * 역할·책임 구조축
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
         * 실제 수용 부담축
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
         * 두 Source Family 모두 필수
         * =================================================
         *
         * 장기 역할 구조만 있거나
         * 수용 부담 Context만 있는 경우에는
         * 이 Rule의 질문을 완성할 수 없다.
         */

        if (
                structureMatches.isEmpty()
                        || tenGodMatches.isEmpty()
        ) {

            return List.of();
        }

        /*
         * =================================================
         * Source Family별 상태 계산
         * =================================================
         *
         * 두 Family를 먼저 합쳐버리지 않는다.
         *
         * 각각 어떤 상태인지
         * 독립적으로 계산한 뒤
         * 최종 조합한다.
         */

        ConsultationCategoryDeterministicStatus
                structureStatus =
                resolveFamilyStatus(
                        structureMatches
                );

        ConsultationCategoryDeterministicStatus
                acceptanceStatus =
                resolveFamilyStatus(
                        tenGodMatches
                );

        /*
         * =================================================
         * PARTNER_LUCK 전용 상태 조합
         * =================================================
         */

        ConsultationCategoryDeterministicStatus
                finalStatus =
                resolvePartnerStatus(
                        structureStatus,
                        acceptanceStatus
                );

        /*
         * =================================================
         * Provenance
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
         * Reasons
         * =================================================
         */

        List<String> reasons =
                new ArrayList<>();

        reasons.add(
                "장기적인 가까운 관계에서 역할·책임 구조 자체와 그 요구를 실제로 수용하는 부담 구조를 서로 다른 축으로 평가했습니다."
        );

        reasons.add(
                "현실적 주고받음·약속·책임·역할을 다루는 원국 중심 구조 Source Family가 실제 존재합니다."
        );

        reasons.add(
                "관계 안의 현실적 요구·역할·책임과 그 수용 부담을 다루는 복합구조 Source Family가 실제 존재합니다."
        );

        reasons.add(
                "역할·책임 구조축의 deterministic 상태는 "
                        + structureStatus.name()
                        + "입니다."
        );

        reasons.add(
                "역할·책임 요구의 수용축 deterministic 상태는 "
                        + acceptanceStatus.name()
                        + "입니다."
        );

        appendCombinationReason(
                reasons,
                structureStatus,
                acceptanceStatus,
                finalStatus
        );

        reasons.add(
                "두 Source Family의 Match 개수를 비교하지 않았으며 Match 총량을 장기 관계의 강도나 안정성 점수로 사용하지 않았습니다."
        );

        reasons.add(
                "기존 Match의 summary 또는 reasons 문자열을 검색하여 세부 관계 사실을 새로 만들지 않았습니다."
        );

        reasons.add(
                "이 결과는 장기 관계에서 역할·책임이 반복될 때 구조 자체와 실제 수용 부담이 어떻게 함께 작동하는지를 설명합니다."
        );

        reasons.add(
                "이 결과를 결혼 성사·배우자 존재·이별·재결합 같은 실제 관계 사건으로 확정하지 않습니다."
        );

        return List.of(
                new ConsultationCategoryDeterministicMatch(
                        ConsultationCategory
                                .PARTNER_LUCK,

                        ConsultationCategoryDeterministicLayer
                                .NATAL,

                        RULE_CODE,

                        finalStatus,

                        null,

                        null,

                        List.of(),

                        summaryOf(
                                structureStatus,
                                acceptanceStatus,
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
     * 공식 구조 필드만 사용한다.
     *
     * - domain
     * - ruleCode
     *
     * summary / reasons 문자열은
     * Source 선택 조건으로 사용하지 않는다.
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
     * Source Family 내부 Status
     * =================================================
     *
     * Match 개수를 사용하지 않는다.
     *
     * 해당 공식 상태가
     * 실제 하나라도 존재하는지만 사용한다.
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

        if (
                mixedExists
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

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
     * PARTNER_LUCK 전용 두 축 조합
     * =================================================
     *
     * 일반적인 상태 Union이 아니다.
     *
     * 첫 번째 축:
     *
     * 장기 관계의 역할·책임 구조 자체.
     *
     * 두 번째 축:
     *
     * 실제 요구를 받아들이는 수용 부담.
     *
     * 이 두 의미의 관계를 기준으로
     * PARTNER_LUCK 결과를 계산한다.
     */

    private ConsultationCategoryDeterministicStatus
    resolvePartnerStatus(

            ConsultationCategoryDeterministicStatus
                    structureStatus,

            ConsultationCategoryDeterministicStatus
                    acceptanceStatus

    ) {

        /*
         * 어느 한 축 자체가 이미
         * 복합 구조로 확정되어 있다면
         * 복합성을 삭제하지 않는다.
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.MIXED

                        || acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            return ConsultationCategoryDeterministicStatus
                    .MIXED;
        }

        /*
         * =================================================
         * 역할 구조가 SUPPORTIVE
         * =================================================
         *
         * 구조는 비교적 성립하지만
         * 실제 수용 부담이 존재하면
         * 장기적으로 두 방향이 함께 작동한다.
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            if (
                    acceptanceStatus
                            == ConsultationCategoryDeterministicStatus.PRESSURE
            ) {

                return ConsultationCategoryDeterministicStatus
                        .MIXED;
            }

            return ConsultationCategoryDeterministicStatus
                    .SUPPORTIVE;
        }

        /*
         * =================================================
         * 역할 구조가 PRESSURE
         * =================================================
         */

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            /*
             * 역할 구조 자체는 부담인데
             * 수용축에는 지지 방향이 있다면
             * 두 방향이 함께 존재한다.
             */

            if (
                    acceptanceStatus
                            == ConsultationCategoryDeterministicStatus.SUPPORTIVE
            ) {

                return ConsultationCategoryDeterministicStatus
                        .MIXED;
            }

            /*
             * 역할 구조와 수용축이
             * 둘 다 부담 방향이거나
             * 수용축이 방향 미확정인 경우.
             */

            return ConsultationCategoryDeterministicStatus
                    .PRESSURE;
        }

        /*
         * =================================================
         * 역할 구조가 CONTEXTUAL
         * =================================================
         *
         * 역할 구조 자체의 방향은
         * 확정되지 않았다.
         *
         * 이때 실제 수용 부담이
         * 명확하게 존재하면
         * 부담 방향은 보존한다.
         */

        if (
                acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return ConsultationCategoryDeterministicStatus
                    .PRESSURE;
        }

        /*
         * 역할 구조 자체가 방향 미확정이면
         * 수용축 SUPPORTIVE 하나만으로
         * 장기 관계의 책임 구조 전체를
         * SUPPORTIVE로 승격하지 않는다.
         */

        return ConsultationCategoryDeterministicStatus
                .CONTEXTUAL;
    }


    /*
     * =================================================
     * 두 축 조합 Reason
     * =================================================
     */

    private void appendCombinationReason(

            List<String> reasons,

            ConsultationCategoryDeterministicStatus
                    structureStatus,

            ConsultationCategoryDeterministicStatus
                    acceptanceStatus,

            ConsultationCategoryDeterministicStatus
                    finalStatus

    ) {

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.MIXED

                        || acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.MIXED
        ) {

            reasons.add(
                    "역할·책임 구조축 또는 실제 수용축 자체에 MIXED 구조가 존재하므로 장기 관계의 역할·책임 반응도 복합 구조로 보존합니다."
            );

            return;
        }

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE

                        && acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "장기 관계의 역할·책임 구조 자체는 비교적 성립하지만 실제 요구를 감당하는 과정에는 부담 방향이 존재하므로 유지 능력과 부담이 함께 존재하는 MIXED로 판단합니다."
            );

            return;
        }

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE

                        && acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "장기 관계의 역할·책임 구조와 실제 요구의 수용축이 모두 부담 방향을 보이므로 반대 방향의 혼재로 처리하지 않고 PRESSURE 방향을 유지합니다."
            );

            return;
        }

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE

                        && acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "장기 관계의 역할 구조에는 부담 방향이 있지만 실제 요구를 받아들이는 축에는 지지 방향이 존재하므로 두 방향을 함께 보존해 MIXED로 판단합니다."
            );

            return;
        }

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "장기 관계의 역할·책임 구조 자체에 부담 방향이 확인되고 이를 반전시킬 별도의 지지 구조가 확정되지 않아 PRESSURE로 판단합니다."
            );

            return;
        }

        if (
                structureStatus
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            reasons.add(
                    "장기 관계의 역할·책임 구조가 비교적 성립되어 있고 실제 수용축에서 별도의 부담 방향이 확인되지 않아 SUPPORTIVE로 판단합니다."
            );

            return;
        }

        if (
                acceptanceStatus
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            reasons.add(
                    "장기 관계의 역할 구조 자체는 한 방향으로 확정되지 않았지만 실제 역할·책임 요구를 수용하는 과정의 부담은 확인되므로 PRESSURE 방향을 보존합니다."
            );

            return;
        }

        reasons.add(
                "장기 관계의 역할 구조와 실제 수용 구조는 모두 존재하지만 어느 한 방향으로 확정할 충분한 deterministic 방향 근거가 없어 CONTEXTUAL로 유지합니다."
        );

        if (
                finalStatus
                        != ConsultationCategoryDeterministicStatus.CONTEXTUAL
        ) {

            throw new IllegalStateException(
                    "PARTNER_LUCK 역할·책임 상태 조합과 Reason이 일치하지 않습니다."
            );
        }
    }


    /*
     * =================================================
     * Internal Summary
     * =================================================
     *
     * 최종 사용자 storyText가 아니다.
     *
     * 장기적인 역할·책임 구조만 설명하며
     * 실제 결혼 사건을 생성하지 않는다.
     */

    private String summaryOf(

            ConsultationCategoryDeterministicStatus
                    structureStatus,

            ConsultationCategoryDeterministicStatus
                    acceptanceStatus,

            ConsultationCategoryDeterministicStatus
                    finalStatus

    ) {

        return switch (
                finalStatus
                ) {

            case SUPPORTIVE ->
                    "장기적인 가까운 관계에서 역할·책임 구조가 비교적 성립되어 있고 반복되는 현실적 요구를 감당하는 과정에서도 뚜렷한 부담 방향이 확인되지 않습니다.";

            case PRESSURE ->
                    "장기적인 가까운 관계에서 역할·책임이 반복될수록 현실적인 요구를 감당하고 조정하는 과정에 부담이 커질 수 있는 구조가 존재합니다.";

            case MIXED -> {

                if (
                        structureStatus
                                == ConsultationCategoryDeterministicStatus.SUPPORTIVE

                                && acceptanceStatus
                                == ConsultationCategoryDeterministicStatus.PRESSURE
                ) {

                    yield "장기 관계에서 역할과 책임을 유지할 구조는 존재하지만 반복되는 현실적 요구를 실제로 감당하는 과정에는 부담이 함께 나타나는 복합 구조입니다.";
                }

                yield "장기 관계의 역할·책임 구조에서 유지에 도움이 되는 요소와 부담 또는 조정이 필요한 요소가 함께 존재하는 복합 구조입니다.";
            }

            case CONTEXTUAL ->
                    "장기 관계에서 역할·책임과 현실적인 요구를 다루는 구조는 존재하지만 지속적인 관계 유지 방향을 한쪽으로 단정할 수준은 아닙니다.";
        };
    }
}