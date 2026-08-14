package com.dajeom.rule;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.LuckPillarSource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 원국 / 대운 / 세운 상호작용 V1 Rule.
 *
 * =================================================
 *
 * 현재 정밀화 구조에서는
 * 대운 시작시점이 하나의 확정 시점일 수도 있고,
 *
 * earliestStartDateTime
 * ~
 * latestStartDateTime
 *
 * 범위일 수도 있다.
 *
 * 따라서 boolean 기반 startsInsideAnnualLuck()에서는
 * 가능한 시작 범위 전체가 해당 세운 안에 있을 때만
 * true를 반환한다.
 *
 * 일부 가능한 경우에만 해당 세운 안에 들어오는 상태를
 * 확정 true로 축약하지 않는다.
 */
public final class LuckInteractionRule {

    public static final String RULE_VERSION =
            "DAJEOM_LUCK_INTERACTION_V1";

    private LuckInteractionRule() {

        throw new IllegalStateException(
                "LuckInteractionRule은 생성할 수 없습니다."
        );
    }

    /**
     * 원국끼리의 관계는 이미
     * Relation 분석 단계에서 처리하므로,
     *
     * 여기에서는 적어도 하나의
     * 대운 또는 세운이 포함된 관계만 분석한다.
     */
    public static boolean isRelevantPair(
            LuckPillarSource first,
            LuckPillarSource second
    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 출처는 필수입니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 출처는 필수입니다."
        );

        return first.isExternalLuck()
                || second.isExternalLuck();
    }

    /**
     * 복수 출처 관계에서
     * 대운 또는 세운이 하나 이상 포함되어 있는지 확인한다.
     */
    public static boolean isRelevantGroup(
            List<LuckPillarSource> sources
    ) {

        Objects.requireNonNull(
                sources,
                "출처 목록은 필수입니다."
        );

        return sources
                .stream()
                .anyMatch(
                        LuckPillarSource::isExternalLuck
                );
    }

    /**
     * 대운 시작 시점이
     * 해당 세운 기간 안에 들어오는지 확인한다.
     *
     * =================================================
     *
     * 세운 기간:
     *
     * [startDateTimeInclusive,
     *  endDateTimeExclusive)
     *
     * =================================================
     *
     * 대운 시작시점이 정확한 경우:
     *
     * earliest == latest
     *
     * 이므로 일반적인 단일시점 판정과 같다.
     *
     * =================================================
     *
     * 대운 시작시점이 불확실한 경우:
     *
     * earliest ~ latest 전체가
     * 동일한 세운 안에 있어야 true다.
     *
     * 예:
     *
     * earliest = 2028년 세운
     * latest   = 2029년 세운
     *
     * 이라면 어느 한 세운에
     * 확정적으로 속한다고 볼 수 없으므로 false다.
     */
    public static boolean startsInsideAnnualLuck(
            DecadeLuckPeriod decadeLuck,
            AnnualLuckPeriod annualLuck
    ) {

        Objects.requireNonNull(
                decadeLuck,
                "대운 구간은 필수입니다."
        );

        Objects.requireNonNull(
                annualLuck,
                "세운 구간은 필수입니다."
        );

        ZonedDateTime annualStart =
                annualLuck
                        .startDateTimeInclusive();

        ZonedDateTime annualEnd =
                annualLuck
                        .endDateTimeExclusive();

        ZonedDateTime earliestDecadeStart =
                decadeLuck
                        .earliestStartDateTime()
                        .withZoneSameInstant(
                                annualLuck.zoneId()
                        );

        ZonedDateTime latestDecadeStart =
                decadeLuck
                        .latestStartDateTime()
                        .withZoneSameInstant(
                                annualLuck.zoneId()
                        );

        /*
         * 세운 구간은:
         *
         * [start, end)
         *
         * 이므로 시작점과 정확히 같으면 포함하고,
         * 종료점과 정확히 같으면 포함하지 않는다.
         */
        boolean earliestInside =
                !earliestDecadeStart
                        .isBefore(
                                annualStart
                        )
                        && earliestDecadeStart
                        .isBefore(
                                annualEnd
                        );

        boolean latestInside =
                !latestDecadeStart
                        .isBefore(
                                annualStart
                        )
                        && latestDecadeStart
                        .isBefore(
                                annualEnd
                        );

        return earliestInside
                && latestInside;
    }
}