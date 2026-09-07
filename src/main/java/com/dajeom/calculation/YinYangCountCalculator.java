package com.dajeom.calculation;

import com.dajeom.domain.FourPillars;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.YinYang;
import com.dajeom.domain.YinYangCount;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 원국에 드러난 천간 + 지지의 음양 개수를 계산한다.
 *
 * 지장간은 포함하지 않는다.
 */
@Component
public class YinYangCountCalculator {

    public YinYangCount calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        MutableCount count =
                new MutableCount();

        addPillar(
                count,
                fourPillars.year()
        );

        addPillar(
                count,
                fourPillars.month()
        );

        addPillar(
                count,
                fourPillars.day()
        );

        if (fourPillars.hasKnownHour()) {

            addPillar(
                    count,
                    fourPillars.hour()
            );
        }

        return new YinYangCount(
                count.yang,
                count.yin
        );
    }

    private void addPillar(
            MutableCount count,
            Pillar pillar
    ) {

        Objects.requireNonNull(
                count,
                "음양 개수 저장소가 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        add(
                count,
                pillar.stem().yinYang()
        );

        add(
                count,
                pillar.branch().yinYang()
        );
    }

    private void add(
            MutableCount count,
            YinYang yinYang
    ) {

        if (yinYang == YinYang.YANG) {

            count.yang++;

        } else {

            count.yin++;
        }
    }

    /**
     * 계산 중에만 사용하는 내부 가변 객체.
     *
     * 최종 결과는 불변 record인 YinYangCount로 반환한다.
     */
    private static final class MutableCount {

        private int yang;
        private int yin;
    }
}