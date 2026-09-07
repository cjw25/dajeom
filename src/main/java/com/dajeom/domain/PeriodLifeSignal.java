package com.dajeom.domain;

import java.util.Objects;

/**
 * 특정 운 기간에서 확인된 하나의 구조적 Signal.
 *
 * =================================================
 *
 * 아직 LifeClaim이 아니다.
 *
 * 즉:
 *
 * "관성이 강해졌다"
 * "재성이 들어왔다"
 *
 * 같은 계산 결과를
 *
 * 곧바로 삶의 사건으로 해석하지 않고,
 * 중간 구조 신호로 보관한다.
 */
public record PeriodLifeSignal(

        LifePeriodKey period,

        PeriodLifeSignalCode code

) {

    public PeriodLifeSignal {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );

        Objects.requireNonNull(
                code,
                "PeriodLifeSignalCode는 필수입니다."
        );
    }
}