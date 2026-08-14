package com.dajeom.type;

/**
 * 정밀화 6차-2.
 *
 * 월령이 가진 기본 한난·조습 환경을
 * 원국 전체 오행 세력이
 * 어떤 방향으로 보정하고 있는지를 나타낸다.
 *
 * MITIGATING
 * 계절 불균형을 실제로 완화하는 방향.
 *
 * NEUTRAL
 * 유의미한 완화 또는 악화가 없음.
 *
 * AGGRAVATING
 * 계절 불균형을 더 강하게 만드는 방향.
 *
 * OVER_CORRECTING
 * 원래의 불균형 방향을 넘어
 * 반대편 불균형으로 넘어간 상태.
 *
 * 예:
 *
 * 계절 한기 -3000
 * 원국 보정 +5000
 * 최종 +2000
 *
 * -> 단순 완화를 넘어
 *    반대 방향으로 넘어갔으므로
 *    OVER_CORRECTING.
 */
public enum ClimateMitigationEffect {

    MITIGATING(
            "완화"
    ),

    NEUTRAL(
            "중립"
    ),

    AGGRAVATING(
            "악화"
    ),

    OVER_CORRECTING(
            "과보정"
    );

    private final String koreanName;

    ClimateMitigationEffect(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean mitigating() {

        return this == MITIGATING;
    }

    public boolean neutral() {

        return this == NEUTRAL;
    }

    public boolean aggravating() {

        return this == AGGRAVATING;
    }

    public boolean overCorrecting() {

        return this == OVER_CORRECTING;
    }

    public boolean helpful() {

        return mitigating();
    }

    public boolean problematic() {

        return aggravating()
                || overCorrecting();
    }
}