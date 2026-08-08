package com.dajeom.rule;

import com.dajeom.model.AnnualLuckPeriod;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.LuckPillarSource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 원국/대운/세운 상호작용 V1 Rule.
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
     * RelationAnalyzer에서 분석했으므로
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
     * 대운 시작 시점이 해당 세운 기간 안에
     * 들어오는지 확인한다.
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

        ZonedDateTime start =
                decadeLuck
                        .startDateTime()
                        .withZoneSameInstant(
                                annualLuck
                                        .startDateTime()
                                        .getZone()
                        );

        return start.isAfter(
                annualLuck.startDateTime()
        )
                && start.isBefore(
                annualLuck.endDateTime()
        );
    }
}