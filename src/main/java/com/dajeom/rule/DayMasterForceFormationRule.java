package com.dajeom.rule;

import com.dajeom.type.DayMasterForceFormationLevel;
import org.springframework.stereotype.Component;

/**
 * 정밀화 4차-4 득세 구조 판정 규칙.
 *
 * 이 Rule은 최종 신강/신약을 판정하지 않는다.
 *
 * 오직:
 *
 * "비겁·인성이 일간 주변에
 * 구조적으로 어느 정도 분포해 있는가"
 *
 * 를 단계화한다.
 *
 * 실제 세력량은 4-5에서 별도로 계산한다.
 */
@Component
public class DayMasterForceFormationRule {

    /**
     * 지원 source 자체가 하나도 없으면
     * 득세 기반 없음.
     */
    public DayMasterForceFormationLevel determineLevel(

            int supportivePositionCount,

            int strongSupportPositionCount,

            int supportiveVisibleStemCount,

            int supportiveHiddenSourceCount

    ) {

        if (
                supportivePositionCount <= 0
        ) {

            return DayMasterForceFormationLevel
                    .NONE;
        }

        /*
         * 천간과 지지 여러 곳에
         * 비교적 명확한 지원 source가
         * 넓게 퍼져 있는 상태.
         */
        if (
                supportivePositionCount >= 4
                        && strongSupportPositionCount >= 3
                        && supportiveVisibleStemCount >= 1
        ) {

            return DayMasterForceFormationLevel
                    .WIDESPREAD;
        }

        /*
         * 다음 중 하나면
         * 지원망이 구조적으로 형성된 것으로 본다.
         *
         * 1. 강한 지원 위치가 2곳 이상
         *
         * 2. 천간 지원 + 지지 지원이 함께 존재
         *
         * 3. 총 지원 위치가 3곳 이상
         */
        if (
                strongSupportPositionCount >= 2

                        || (
                        supportiveVisibleStemCount >= 1
                                && supportiveHiddenSourceCount >= 1
                )

                        || supportivePositionCount >= 3
        ) {

            return DayMasterForceFormationLevel
                    .ESTABLISHED;
        }

        /*
         * 지원 근거가 존재하지만
         * 분포가 제한된 상태.
         */
        return DayMasterForceFormationLevel
                .LIMITED;
    }
}