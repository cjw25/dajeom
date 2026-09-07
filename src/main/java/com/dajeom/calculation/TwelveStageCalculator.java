package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.TwelveStage;
import com.dajeom.rule.SajuRuleBook;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 12운성 계산기.
 *
 * 기준 천간 + 대상 지지로 12운성을 계산한다.
 *
 * 예:
 *
 * 丁 + 酉 = 장생
 * 丁 + 申 = 목욕
 * 丁 + 丑 = 묘
 * 丁 + 辰 = 쇠
 */
@Component
public class TwelveStageCalculator {

    public TwelveStage calculate(
            HeavenlyStem referenceStem,
            EarthlyBranch targetBranch
    ) {

        Objects.requireNonNull(
                referenceStem,
                "12운성 기준 천간이 필요합니다."
        );

        Objects.requireNonNull(
                targetBranch,
                "12운성 대상 지지가 필요합니다."
        );

        EarthlyBranch startBranch =
                SajuRuleBook.twelveStageStartBranch(
                        referenceStem
                );

        boolean forward =
                SajuRuleBook.isTwelveStageForward(
                        referenceStem
                );

        int distance;

        if (forward) {

            distance =
                    Math.floorMod(
                            targetBranch.index()
                                    - startBranch.index(),
                            12
                    );

        } else {

            distance =
                    Math.floorMod(
                            startBranch.index()
                                    - targetBranch.index(),
                            12
                    );
        }

        return TwelveStage.ofIndex(
                distance
        );
    }
}