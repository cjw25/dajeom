package com.dajeom.rule;

import com.dajeom.model.ConsultationCategoryDeterministicMatch;
import com.dajeom.model.ConsultationCategoryEvidenceView;
import com.dajeom.model.ConsultationCategorySegmentEvidence;
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
 * PARTNER_LUCK 현재 월 deterministic Rule.
 *
 * 공식 Source Family:
 *
 * RELATIONSHIP_MONTHLY_CONTEXT
 *
 * 하나만 사용한다.
 *
 * 세운과 월운을 이 Rule 안에서 결합하지 않는다.
 */
@Component
public class PartnerLuckCurrentMonthlyActivationRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "PARTNER_LUCK_CURRENT_MONTHLY_ACTIVATION";

    private static final String
            MONTHLY_CONTEXT_RULE_CODE =
            "RELATIONSHIP_MONTHLY_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.PARTNER_LUCK;
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
                "PartnerLuckCurrentMonthlyActivationRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.PARTNER_LUCK
        ) {

            throw new IllegalArgumentException(
                    "PartnerLuckCurrentMonthlyActivationRule은 PARTNER_LUCK View만 평가할 수 있습니다."
            );
        }

        if (
                view.unavailable()
        ) {

            return List.of();
        }

        if (
                !view.hasSegmentEvidences()
        ) {

            return List.of();
        }

        List<ConsultationCategoryDeterministicMatch> result =
                new ArrayList<>();

        for (
                ConsultationCategorySegmentEvidence segment
                : view.segmentEvidences()
        ) {

            ConsultationCategorySegmentEvidence normalizedSegment =
                    Objects.requireNonNull(
                            segment,
                            "PARTNER_LUCK Current Timeline Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> monthlyMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            MONTHLY_CONTEXT_RULE_CODE
                    );

            if (
                    monthlyMatches.isEmpty()
            ) {

                continue;
            }

            ConsultationCategoryDeterministicStatus monthlyStatus =
                    resolveStatus(
                            monthlyMatches
                    );

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "현재 월 PARTNER_LUCK 판단은 RELATIONSHIP_MONTHLY_CONTEXT Source Family만 사용했습니다."
            );

            reasons.add(
                    statusReason(
                            monthlyMatches,
                            monthlyStatus
                    )
            );

            if (
                    normalizedSegment
                            .timelineUncertaintyExists()
            ) {

                reasons.add(
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 PARTNER_LUCK 월운 경로로 확정하지 않습니다."
                );
            }

            if (
                    !normalizedSegment
                            .fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment가 완전히 확정되지 않았으므로 동일 명리연도·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
                );
            }

            reasons.add(
                    "RELATIONSHIP_ANNUAL_CONTEXT를 포함한 다른 시간층 Source를 이 월운 Conclusion에 결합하지 않았습니다."
            );

            reasons.add(
                    "다른 시간층과의 강화·약화·상쇄·전환·충돌·완화·인과·순서를 생성하지 않았습니다."
            );

            reasons.add(
                    "Monthly Source Match 반복 개수를 배우자운·연애 가능성·결혼 가능성·관계 지속성의 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            reasons.add(
                    "monthIndex는 기존 Timeline 계산값을 그대로 보존하며 0도 유효한 값으로 취급합니다."
            );

            reasons.add(
                    "이 결과는 현재 월의 관계영역 deterministic 상태만 보존하며 배우자 등장·연애 시작·결혼·이별·재회 같은 사건을 생성하지 않습니다."
            );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.PARTNER_LUCK,
                            ConsultationCategoryDeterministicLayer
                                    .CURRENT_TIMELINE,
                            RULE_CODE,
                            monthlyStatus,
                            normalizedSegment
                                    .sajuYear(),
                            normalizedSegment
                                    .monthIndex(),
                            List.of(),
                            summaryOf(
                                    monthlyStatus
                            ),
                            List.copyOf(
                                    monthlyMatches
                            ),
                            List.copyOf(
                                    reasons
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }


    private List<LifeDomainRuleMatch>
    findSegmentMatches(

            ConsultationCategorySegmentEvidence segment,

            String sourceRuleCode

    ) {

        Objects.requireNonNull(
                segment,
                "ConsultationCategorySegmentEvidence가 필요합니다."
        );

        String normalizedRuleCode =
                requireText(
                        sourceRuleCode,
                        "PARTNER_LUCK Timeline Source ruleCode가 필요합니다."
                );

        List<LifeDomainRuleMatch> result =
                new ArrayList<>();

        for (
                LifeDomainRuleMatch match
                : segment.matches()
        ) {

            LifeDomainRuleMatch normalized =
                    Objects.requireNonNull(
                            match,
                            "PARTNER_LUCK Segment Match는 null일 수 없습니다."
                    );

            if (
                    normalized.domain()
                            != LifeDomain.RELATIONSHIP
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


    private ConsultationCategoryDeterministicStatus
    resolveStatus(
            List<LifeDomainRuleMatch> matches
    ) {

        Objects.requireNonNull(
                matches,
                "PARTNER_LUCK Monthly Match 목록이 필요합니다."
        );

        if (
                matches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "PARTNER_LUCK Monthly Status 계산에는 최소 하나의 Match가 필요합니다."
            );
        }

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


    private boolean hasStatus(

            List<LifeDomainRuleMatch> matches,

            LifeDomainSignalStatus status

    ) {

        Objects.requireNonNull(
                matches,
                "PARTNER_LUCK Match 목록이 필요합니다."
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


    private String statusReason(

            List<LifeDomainRuleMatch> matches,

            ConsultationCategoryDeterministicStatus status

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

            return "RELATIONSHIP_MONTHLY_CONTEXT Source Family의 MIXED 상태를 현재 월 PARTNER_LUCK 상태로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_MONTHLY_CONTEXT Source Family 안에 SUPPORTIVE와 PRESSURE 상태가 모두 존재하여 MIXED로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return "RELATIONSHIP_MONTHLY_CONTEXT Source Family의 공식 방향을 PRESSURE로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return "RELATIONSHIP_MONTHLY_CONTEXT Source Family의 공식 방향을 SUPPORTIVE로 보존했습니다.";
        }

        return "RELATIONSHIP_MONTHLY_CONTEXT Source Family는 존재하지만 방향은 CONTEXTUAL로 유지했습니다.";
    }


    private String summaryOf(
            ConsultationCategoryDeterministicStatus monthlyStatus
    ) {

        Objects.requireNonNull(
                monthlyStatus,
                "PARTNER_LUCK Monthly Status가 필요합니다."
        );

        return switch (
                monthlyStatus
                ) {

            case SUPPORTIVE ->
                    "현재 월의 PARTNER_LUCK 관계영역 Context에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "현재 월의 PARTNER_LUCK 관계영역 Context에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "현재 월의 PARTNER_LUCK 관계영역 Context에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "현재 월의 PARTNER_LUCK 관계영역 Context가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
        };
    }


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