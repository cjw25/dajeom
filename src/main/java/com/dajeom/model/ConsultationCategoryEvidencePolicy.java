package com.dajeom.model;

import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.LifeDomain;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 19차.
 *
 * 18차 LifeDomainAnalysis를
 * 11개 ConsultationCategory에 연결하기 위한
 * deterministic 입력 정책.
 *
 * =================================================
 *
 * 이 모델은:
 *
 * "어떤 상담 분야가
 * 어떤 LifeDomain Evidence를 소비할 수 있는가"
 *
 * 만 정의한다.
 *
 * 사용자 상담문을 생성하지 않는다.
 *
 * =================================================
 *
 * natalDomains
 *
 * 원국 LifeDomainRuleMatch 중
 * 해당 ConsultationCategory에 사용할 수 있는 Domain.
 *
 * =================================================
 *
 * segmentDomains
 *
 * 대운·세운·월운 Segment 변화분 중
 * 해당 ConsultationCategory에 사용할 수 있는 Domain.
 *
 * =================================================
 *
 * requiresPastTimelineRange
 *
 * 현재 단일 target Segment가 아니라
 * 실제 과거 Timeline 범위가 필요함.
 *
 * =================================================
 *
 * requiresFutureTimelineRange
 *
 * 현재 Segment 하나가 아니라
 * 실제 미래 Timeline 범위가 필요함.
 *
 * =================================================
 *
 * requiresDedicatedCrossDomainRule
 *
 * 단순 Domain 매핑만으로 만들면 안 되고,
 * 별도의 교차 Evidence Rule이 필요한 분야.
 *
 * 대표:
 *
 * LEGAL_RISK
 *
 * =================================================
 *
 * 중요:
 *
 * ConsultationCategory
 * !=
 * LifeDomain
 *
 * 둘은 서로 다른 축이다.
 */
public record ConsultationCategoryEvidencePolicy(

        ConsultationCategory category,

        Set<LifeDomain> natalDomains,

        Set<LifeDomain> segmentDomains,

        boolean requiresPastTimelineRange,

        boolean requiresFutureTimelineRange,

        boolean requiresDedicatedCrossDomainRule

) {

    public ConsultationCategoryEvidencePolicy {

        Objects.requireNonNull(
                category,
                "ConsultationCategoryEvidencePolicy에는 ConsultationCategory가 필요합니다."
        );

        natalDomains =
                immutableDomains(
                        natalDomains
                );

        segmentDomains =
                immutableDomains(
                        segmentDomains
                );

        /*
         * =================================================
         * 과거/미래 Range 동시 요구 금지
         * =================================================
         *
         * 현재 11개 Category 중
         * 하나의 View가 과거와 미래 전체 Range를
         * 동시에 요구하지 않는다.
         */

        if (
                requiresPastTimelineRange
                        && requiresFutureTimelineRange
        ) {

            throw new IllegalArgumentException(
                    "하나의 ConsultationCategory가 과거/미래 전체 Timeline Range를 동시에 요구할 수 없습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * Range 전용 View
         * =================================================
         *
         * PAST_LUCK / FUTURE_LUCK 같은 Range View에
         * 현재 Segment Domain을 잘못 연결하지 못하게 한다.
         */

        if (
                (
                        requiresPastTimelineRange
                                || requiresFutureTimelineRange
                )
                        && !segmentDomains.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Timeline Range가 필요한 ConsultationCategory에는 현재 단일 Segment Domain을 직접 연결하지 않습니다: "
                            + category
            );
        }

        /*
         * =================================================
         * 전용 교차 Rule View
         * =================================================
         *
         * LEGAL_RISK 같은 Category를
         * 단순 Domain 복사로 만들지 않는다.
         */

        if (
                requiresDedicatedCrossDomainRule
                        && (
                        !natalDomains.isEmpty()
                                || !segmentDomains.isEmpty()
                )
        ) {

            throw new IllegalArgumentException(
                    "전용 교차 Rule이 필요한 ConsultationCategory에는 단순 LifeDomain 매핑을 두지 않습니다: "
                            + category
            );
        }
    }

    public boolean acceptsNatalDomain(
            LifeDomain domain
    ) {

        return domain != null
                && natalDomains.contains(
                domain
        );
    }

    public boolean acceptsSegmentDomain(
            LifeDomain domain
    ) {

        return domain != null
                && segmentDomains.contains(
                domain
        );
    }

    public boolean directLifeDomainView() {

        return !natalDomains.isEmpty()
                || !segmentDomains.isEmpty();
    }

    public boolean timelineRangeView() {

        return requiresPastTimelineRange
                || requiresFutureTimelineRange;
    }

    private static Set<LifeDomain>
    immutableDomains(
            Set<LifeDomain> source
    ) {

        if (
                source == null
                        || source.isEmpty()
        ) {

            return Set.of();
        }

        EnumSet<LifeDomain> copied =
                EnumSet.noneOf(
                        LifeDomain.class
                );

        for (
                LifeDomain domain
                : source
        ) {

            copied.add(
                    Objects.requireNonNull(
                            domain,
                            "ConsultationCategoryEvidencePolicy의 LifeDomain은 null일 수 없습니다."
                    )
            );
        }

        return Collections.unmodifiableSet(
                copied
        );
    }
}