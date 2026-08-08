package com.dajeom.model;

import com.dajeom.type.LuckDirection;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 대운 전체 분석 결과.
 *
 * 출생시간을 알고 있는 경우:
 *
 * - 방향
 * - 시작 나이
 * - 시작 시점
 * - 대운 목록
 * - 현재 대운
 *
 * 을 계산한다.
 *
 * 출생시간을 모르는 경우:
 *
 * 방향은 계산 가능하지만
 * 대운 시작시점은 임의로 만들지 않는다.
 */
public record DecadeLuckAnalysis(

        LuckDirection direction,

        boolean calculationAvailable,

        LuckStart luckStart,

        List<DecadeLuckPeriod> periods,

        DecadeLuckPeriod currentPeriod,

        ZonedDateTime targetDateTime,

        String unavailableReason,

        String ruleVersion

) {

    public DecadeLuckAnalysis {

        Objects.requireNonNull(
                direction,
                "대운 방향은 필수입니다."
        );

        Objects.requireNonNull(
                periods,
                "대운 목록은 필수입니다."
        );

        Objects.requireNonNull(
                targetDateTime,
                "조회 기준 시각은 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        periods =
                List.copyOf(
                        periods
                );

        if (calculationAvailable) {

            if (luckStart == null) {

                throw new IllegalArgumentException(
                        "대운 계산 가능 상태에서는 대운 시작정보가 필요합니다."
                );
            }

            if (periods.isEmpty()) {

                throw new IllegalArgumentException(
                        "대운 계산 가능 상태에서는 대운 목록이 필요합니다."
                );
            }

        } else {

            if (luckStart != null) {

                throw new IllegalArgumentException(
                        "대운 계산 불가 상태에서는 대운 시작정보가 없어야 합니다."
                );
            }

            if (!periods.isEmpty()) {

                throw new IllegalArgumentException(
                        "대운 계산 불가 상태에서는 대운 목록이 없어야 합니다."
                );
            }

            if (currentPeriod != null) {

                throw new IllegalArgumentException(
                        "대운 계산 불가 상태에서는 현재 대운이 없어야 합니다."
                );
            }

            if (
                    unavailableReason == null
                            || unavailableReason.isBlank()
            ) {

                throw new IllegalArgumentException(
                        "대운 계산 불가 사유가 필요합니다."
                );
            }
        }
    }

    public Optional<LuckStart> optionalLuckStart() {

        return Optional.ofNullable(
                luckStart
        );
    }

    public Optional<DecadeLuckPeriod>
    optionalCurrentPeriod() {

        return Optional.ofNullable(
                currentPeriod
        );
    }

    public boolean hasCurrentPeriod() {

        return currentPeriod != null;
    }

    /**
     * 아직 첫 대운이 시작되기 전인지 확인한다.
     */
    public boolean isBeforeFirstDecadeLuck() {

        if (
                !calculationAvailable
                        || periods.isEmpty()
        ) {

            return false;
        }

        return targetDateTime.isBefore(
                periods
                        .get(0)
                        .startDateTime()
        );
    }
}