package com.dajeom.calculation;

import com.dajeom.domain.FiveElement;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.TenGod;
import com.dajeom.domain.YinYang;
import com.dajeom.rule.SajuRuleBook;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 십성 계산기.
 *
 * 기준은 항상 일간(dayStem)이다.
 *
 * targetStem이 일간과 어떤 관계인지 계산한다.
 */
@Component
public class TenGodCalculator {

    public TenGod calculate(
            HeavenlyStem dayStem,
            HeavenlyStem targetStem
    ) {

        Objects.requireNonNull(
                dayStem,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                targetStem,
                "대상 천간이 필요합니다."
        );

        FiveElement dayElement =
                dayStem.element();

        FiveElement targetElement =
                targetStem.element();

        YinYang dayYinYang =
                dayStem.yinYang();

        YinYang targetYinYang =
                targetStem.yinYang();

        boolean samePolarity =
                dayYinYang == targetYinYang;

        /*
         * 1. 같은 오행
         *
         * 같은 음양 → 비견
         * 다른 음양 → 겁재
         */
        if (dayElement == targetElement) {

            return samePolarity
                    ? TenGod.BI_JIAN
                    : TenGod.JIE_CAI;
        }

        /*
         * 2. 내가 생함
         *
         * 같은 음양 → 식신
         * 다른 음양 → 상관
         */
        if (SajuRuleBook.generates(
                dayElement,
                targetElement
        )) {

            return samePolarity
                    ? TenGod.SHI_SHEN
                    : TenGod.SHANG_GUAN;
        }

        /*
         * 3. 내가 극함
         *
         * 같은 음양 → 편재
         * 다른 음양 → 정재
         */
        if (SajuRuleBook.controls(
                dayElement,
                targetElement
        )) {

            return samePolarity
                    ? TenGod.PIAN_CAI
                    : TenGod.ZHENG_CAI;
        }

        /*
         * 4. 나를 생함
         *
         * 같은 음양 → 편인
         * 다른 음양 → 정인
         */
        if (SajuRuleBook.generates(
                targetElement,
                dayElement
        )) {

            return samePolarity
                    ? TenGod.PIAN_YIN
                    : TenGod.ZHENG_YIN;
        }

        /*
         * 5. 나를 극함
         *
         * 같은 음양 → 편관
         * 다른 음양 → 정관
         */
        if (SajuRuleBook.controls(
                targetElement,
                dayElement
        )) {

            return samePolarity
                    ? TenGod.PIAN_GUAN
                    : TenGod.ZHENG_GUAN;
        }

        /*
         * 오행은 반드시 위 다섯 관계 중 하나에 들어간다.
         *
         * 여기까지 왔다면 데이터 오류다.
         */
        throw new IllegalStateException(
                "십성 관계를 결정할 수 없습니다. "
                        + "dayStem="
                        + dayStem.hanja()
                        + ", targetStem="
                        + targetStem.hanja()
        );
    }
}