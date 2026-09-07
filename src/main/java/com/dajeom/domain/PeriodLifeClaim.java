package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 특정 대운 / 세운 / 월운에 해당하는
 * 지나온 흐름용 LifeClaim.
 *
 * =================================================
 *
 * code
 *
 * 그 기간의 삶의 의미.
 *
 * =================================================
 *
 * bases
 *
 * 어떤 PeriodLifeSignal 때문에
 * 이 Claim이 만들어졌는가.
 *
 * =================================================
 *
 * 이 객체 역시 최종 사용자에게
 * 내부 코드 그대로 노출하지 않는다.
 */
public record PeriodLifeClaim(

        LifePeriodKey period,

        PeriodLifeClaimCode code,

        Set<PeriodLifeSignalCode> bases

) {

    public PeriodLifeClaim {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );

        Objects.requireNonNull(
                code,
                "PeriodLifeClaimCode는 필수입니다."
        );

        bases =
                bases == null
                        ? Set.of()
                        : Set.copyOf(bases);

        if (bases.isEmpty()) {

            throw new IllegalArgumentException(
                    "PeriodLifeClaim에는 최소 하나의 구조적 근거가 필요합니다."
            );
        }

        for (
                PeriodLifeSignalCode basis
                : bases
        ) {

            Objects.requireNonNull(
                    basis,
                    "PeriodLifeClaim basis에 null이 포함될 수 없습니다."
            );
        }
    }

    public boolean hasBasis(
            PeriodLifeSignalCode basis
    ) {

        Objects.requireNonNull(
                basis,
                "PeriodLifeSignalCode는 필수입니다."
        );

        return bases.contains(
                basis
        );
    }
}