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
 * SOCIAL_RELATIONSHIP
 * 현재 명리연도 deterministic Rule.
 *
 * =================================================
 * 공식 Source Family
 * =================================================
 *
 * RELATIONSHIP_ANNUAL_CONTEXT
 *
 * 하나만 사용한다.
 *
 * =================================================
 * 시간층 독립성
 * =================================================
 *
 * Decade Context는
 * 이 Annual Conclusion에 결합하지 않는다.
 *
 * 대운과 세운은 각각 독립 결과다.
 *
 * =================================================
 * 하지 않는 것
 * =================================================
 *
 * - Decade + Annual 합성
 * - 다른 시간층 Status 결합
 * - 강화/약화 계산
 * - 사건 생성
 * - 인과 생성
 * - 순서 생성
 * - Match 개수 점수화
 * - summary 문자열 파싱
 * - reasons 문자열 파싱
 */
@Component
public class SocialRelationshipCurrentStructuralFlowRule
        implements ConsultationCategoryDeterministicRule {

    private static final String RULE_CODE =
            "SOCIAL_RELATIONSHIP_CURRENT_STRUCTURAL_FLOW";

    private static final String
            ANNUAL_CONTEXT_RULE_CODE =
            "RELATIONSHIP_ANNUAL_CONTEXT";


    @Override
    public ConsultationCategory category() {

        return ConsultationCategory.SOCIAL_RELATIONSHIP;
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
                "SocialRelationshipCurrentStructuralFlowRule에는 ConsultationCategoryEvidenceView가 필요합니다."
        );

        if (
                view.category()
                        != ConsultationCategory.SOCIAL_RELATIONSHIP
        ) {

            throw new IllegalArgumentException(
                    "SocialRelationshipCurrentStructuralFlowRule은 SOCIAL_RELATIONSHIP View만 평가할 수 있습니다."
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
                            "SOCIAL_RELATIONSHIP Current Timeline Segment는 null일 수 없습니다."
                    );

            List<LifeDomainRuleMatch> annualMatches =
                    findSegmentMatches(
                            normalizedSegment,
                            ANNUAL_CONTEXT_RULE_CODE
                    );

            if (
                    annualMatches.isEmpty()
            ) {

                continue;
            }

            ConsultationCategoryDeterministicStatus annualStatus =
                    resolveStatus(
                            annualMatches
                    );

            List<String> reasons =
                    new ArrayList<>();

            reasons.add(
                    "현재 SOCIAL_RELATIONSHIP 명리연도 판단은 RELATIONSHIP_ANNUAL_CONTEXT Source Family만 사용했습니다."
            );

            reasons.add(
                    statusReason(
                            annualMatches,
                            annualStatus
                    )
            );

            if (
                    normalizedSegment
                            .timelineUncertaintyExists()
            ) {

                reasons.add(
                        "현재 Timeline에 불확실성이 존재하므로 이 Segment를 유일한 현재운 경로로 확정하지 않습니다."
                );
            }

            if (
                    !normalizedSegment
                            .fullyDefinite()
            ) {

                reasons.add(
                        "현재 Segment가 완전히 확정되지 않았으므로 동일 연·월 좌표의 다른 가능한 Segment가 있으면 각각 별도 결과로 보존합니다."
                );
            }

            reasons.add(
                    "대운을 포함한 다른 시간층 Source를 이 명리연도 Conclusion에 결합하지 않았습니다."
            );

            reasons.add(
                    "Annual Source Match의 반복 개수는 인간관계 강도·사회성·인기 또는 사건 가능성 점수로 사용하지 않았습니다."
            );

            reasons.add(
                    "Source의 summary 또는 reasons 문자열을 판단 조건으로 사용하지 않았습니다."
            );

            result.add(
                    new ConsultationCategoryDeterministicMatch(
                            ConsultationCategory.SOCIAL_RELATIONSHIP,
                            ConsultationCategoryDeterministicLayer
                                    .CURRENT_TIMELINE,
                            RULE_CODE,
                            annualStatus,
                            normalizedSegment
                                    .sajuYear(),
                            normalizedSegment
                                    .monthIndex(),
                            List.of(),
                            summaryOf(
                                    annualStatus
                            ),
                            annualMatches,
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
                        "SOCIAL_RELATIONSHIP Timeline Source ruleCode가 필요합니다."
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
                            "SOCIAL_RELATIONSHIP Segment Match는 null일 수 없습니다."
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
                "SOCIAL_RELATIONSHIP Annual Match 목록이 필요합니다."
        );

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
                "SOCIAL_RELATIONSHIP Match 목록이 필요합니다."
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

            return "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 MIXED 상태를 현재 명리연도 상태로 보존했습니다.";
        }

        if (
                supportiveExists
                        && pressureExists
        ) {

            return "RELATIONSHIP_ANNUAL_CONTEXT Source Family 안에 SUPPORTIVE와 PRESSURE 상태가 모두 존재하여 MIXED로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.PRESSURE
        ) {

            return "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 공식 방향을 PRESSURE로 보존했습니다.";
        }

        if (
                status
                        == ConsultationCategoryDeterministicStatus.SUPPORTIVE
        ) {

            return "RELATIONSHIP_ANNUAL_CONTEXT Source Family의 공식 방향을 SUPPORTIVE로 보존했습니다.";
        }

        return "RELATIONSHIP_ANNUAL_CONTEXT Source Family는 존재하지만 방향은 CONTEXTUAL로 유지했습니다.";
    }


    private String summaryOf(

            ConsultationCategoryDeterministicStatus annualStatus

    ) {

        Objects.requireNonNull(
                annualStatus,
                "SOCIAL_RELATIONSHIP Annual Status가 필요합니다."
        );

        return switch (
                annualStatus
                ) {

            case SUPPORTIVE ->
                    "현재 명리연도의 대인관계 영역에 SUPPORTIVE 상태가 확인됩니다.";

            case PRESSURE ->
                    "현재 명리연도의 대인관계 영역에 PRESSURE 상태가 확인됩니다.";

            case MIXED ->
                    "현재 명리연도의 대인관계 영역에 MIXED 상태가 확인됩니다.";

            case CONTEXTUAL ->
                    "현재 명리연도의 대인관계 Context가 존재하며 방향은 CONTEXTUAL로 유지됩니다.";
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