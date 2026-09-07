package com.dajeom.domain;

import java.util.Objects;

/**
 * 특정 대운 / 세운 / 월운에서 확인된
 * 하나의 정규화된 명리 구조 영향.
 *
 * =================================================
 *
 * 이 객체는 기존 운 계산 결과와
 * LifeClaim 계층 사이의 Adapter DTO 역할을 한다.
 *
 * =================================================
 *
 * 예:
 *
 * 2030년 세운에서 財 계열 활성
 *
 * PeriodMyeongriInfluence(
 *     period,
 *     WEALTH_ACTIVATED
 * )
 *
 * =================================================
 *
 * 여기에는:
 *
 * - 돈을 벌었다
 * - 취업했다
 * - 결혼했다
 *
 * 같은 실제 사건 정보를 넣지 않는다.
 */
public record PeriodMyeongriInfluence(

        LifePeriodKey period,

        PeriodMyeongriInfluenceCode code

) {

    public PeriodMyeongriInfluence {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );

        Objects.requireNonNull(
                code,
                "PeriodMyeongriInfluenceCode는 필수입니다."
        );
    }
}