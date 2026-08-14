package com.dajeom.rule;

import org.springframework.stereotype.Component;

/**
 * 정밀화 4차-5.
 *
 * 일간을 돕는:
 *
 * - 비겁
 * - 인성
 *
 * 의 실제 생조 세력을 계산하기 위한 규칙.
 *
 * 중요:
 *
 * 이 수치는 자연과학적 절대값이나
 * 명리학 전체 유파의 공통 공식이 아니다.
 *
 * 다점 명리엔진 내부에서
 * 서로 다른 종류의 지원을 비교하기 위한
 * 명시적인 계산 정책이다.
 *
 * 최종 신강/신약 판정은
 * 이 Rule에서 하지 않는다.
 */
@Component
public class DayMasterSupportPowerRule {

    /**
     * 비겁은 일간과 같은 오행이므로
     * 외부 비겁으로 추정된 세력을
     * 직접 지원력으로 사용한다.
     */
    private static final int
            COMPANION_SUPPORT_RATE =
            100;

    /**
     * 인성은 일간과 같은 오행이 아니라
     * 일간을 생하는 공급원이다.
     *
     * 따라서 관계 capacity의 85%를
     * 신강 분석용 유효 생조력으로 사용한다.
     *
     * 이후 Git revision을 통해
     * 정책 조정이 가능하다.
     */
    private static final int
            RESOURCE_SUPPORT_RATE =
            85;

    /**
     * 같은 오행 전체 최종세력 가운데
     * 실제 일간 자신을 제외한
     * 외부 비겁의 세력을 추정한다.
     *
     * 예:
     *
     * 火 raw 전체 = 200
     *
     * 그중:
     *
     * 일간 丁 = 100
     * 외부 火 = 100
     *
     * 이라면 외부 비율은 50%.
     *
     * 火 최종세력이 180이라면:
     *
     * 외부 비겁 추정세력
     * = 180 × 50%
     * = 90
     *
     * 이렇게 하면 일간 자기 자신을
     * 비겁 지원으로 다시 더하지 않는다.
     */
    public int calculateExternalCompanionCapacity(

            int companionFinalPower,

            int companionRawBasePower,

            int externalCompanionRawPower

    ) {

        if (
                companionFinalPower <= 0
                        || companionRawBasePower <= 0
                        || externalCompanionRawPower <= 0
        ) {

            return 0;
        }

        int safeExternalRawPower =
                Math.min(
                        externalCompanionRawPower,
                        companionRawBasePower
                );

        double externalRatio =
                safeExternalRawPower
                        / (double) companionRawBasePower;

        return Math.max(
                0,
                (int) Math.round(
                        companionFinalPower
                                * externalRatio
                )
        );
    }

    /**
     * 비겁 유효 지원력.
     */
    public int calculateCompanionSupportPower(
            int companionCapacity
    ) {

        return applyRate(
                companionCapacity,
                COMPANION_SUPPORT_RATE
        );
    }

    /**
     * 인성이 실제로 일간 오행을
     * 생조할 수 있는 최대 관계량.
     *
     * 공급자인 인성만 강하고
     * 일간 오행이 극도로 작아도
     * 인성 전체가 그대로 전달된다고
     * 보지 않는다.
     *
     * 반대로 일간은 큰데 인성이 작으면
     * 인성 자체의 세력 이상을
     * 공급할 수도 없다.
     *
     * 따라서:
     *
     * min(인성 최종세력, 일간오행 최종세력)
     *
     * 을 관계 capacity로 사용한다.
     */
    public int calculateResourceSupportCapacity(

            int resourceFinalPower,

            int dayMasterElementFinalPower

    ) {

        if (
                resourceFinalPower <= 0
                        || dayMasterElementFinalPower <= 0
        ) {

            return 0;
        }

        return Math.min(
                resourceFinalPower,
                dayMasterElementFinalPower
        );
    }

    /**
     * 인성의 유효 생조력.
     */
    public int calculateResourceSupportPower(
            int resourceCapacity
    ) {

        return applyRate(
                resourceCapacity,
                RESOURCE_SUPPORT_RATE
        );
    }

    public int companionSupportRate() {

        return COMPANION_SUPPORT_RATE;
    }

    public int resourceSupportRate() {

        return RESOURCE_SUPPORT_RATE;
    }

    private int applyRate(

            int value,

            int rate

    ) {

        if (
                value <= 0
                        || rate <= 0
        ) {

            return 0;
        }

        return Math.max(
                0,
                (int) Math.round(
                        value
                                * (
                                rate / 100.0
                        )
                )
        );
    }
}