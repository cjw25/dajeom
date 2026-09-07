package com.dajeom.domain;

import java.util.Objects;

/**
 * 기존 대운 / 세운 / 월운 계산기가
 * LifeClaim 계층으로 넘기기 직전에 만드는
 * 기간별 정규화 사실 묶음.
 *
 * =================================================
 *
 * 기존 운 엔진
 *
 * 천간 / 지지
 * 십성
 * 합충형파해
 * 원국과의 관계
 * 격국 영향
 *
 * 등을 계산한 뒤,
 *
 * "그래서 이 기간에 어떤 구조가 실제 활성됐는가"
 *
 * 만 이 객체로 넘긴다.
 *
 * =================================================
 *
 * 이 객체는 실제 사건을 의미하지 않는다.
 *
 * 예:
 *
 * wealthActivated = true
 *
 * 는
 *
 * 돈을 벌었다 X
 * 돈을 잃었다 X
 *
 * 財 계열의 현실적 요구가
 * 해당 기간에 실제 활성되었다 O
 *
 * 라는 뜻이다.
 *
 * =================================================
 *
 * disruptionActivated와 disruptionResolved는
 * 동시에 true일 수 있다.
 *
 * 즉:
 *
 * 충돌이 활성됐지만
 * 동시에 해소 route도 존재할 수 있다.
 *
 * =================================================
 *
 * natalDirectionReinforced와
 * natalDirectionChallenged도
 * 복합 운에서는 동시에 존재할 수 있으므로
 * 여기서 강제로 상호배타 처리하지 않는다.
 */
public record PeriodMyeongriFacts(

        LifePeriodKey period,

        boolean resourceActivated,

        boolean resourceWeakened,

        boolean outputActivated,

        boolean wealthActivated,

        boolean authorityActivated,

        boolean companionActivated,

        boolean natalStructureDisruptionActivated,

        boolean natalStructureDisruptionResolved,

        boolean activeBindingEstablished,

        boolean natalDirectionReinforced,

        boolean natalDirectionChallenged

) {

    public PeriodMyeongriFacts {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );
    }

    /**
     * 기간 해석에 사용할 구조 사실이
     * 하나라도 존재하는가.
     */
    public boolean hasAnyInfluence() {

        return resourceActivated
                ||
                resourceWeakened
                ||
                outputActivated
                ||
                wealthActivated
                ||
                authorityActivated
                ||
                companionActivated
                ||
                natalStructureDisruptionActivated
                ||
                natalStructureDisruptionResolved
                ||
                activeBindingEstablished
                ||
                natalDirectionReinforced
                ||
                natalDirectionChallenged;
    }
}