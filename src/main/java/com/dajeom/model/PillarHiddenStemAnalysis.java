package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StemExposurePosition;

import java.util.List;
import java.util.Objects;

/**
 * 원국의 특정 지지 하나에 대한
 * 지장간 분석 결과.
 *
 * 예:
 *
 * 월지 申
 *
 * 庚 정기 60
 * 壬 중기 25
 * 戊 여기 15
 *
 * 총합 100
 *
 * 년지 / 월지 / 일지 / 시지를
 * 서로 구분해서 보존한다.
 */
public record PillarHiddenStemAnalysis(

        StemExposurePosition position,

        EarthlyBranch branch,

        HeavenlyStem mainHiddenStem,

        int mainHiddenStemWeight,

        int totalHiddenStemWeight,

        List<HiddenStem> hiddenStems

) {

    public PillarHiddenStemAnalysis {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        Objects.requireNonNull(
                mainHiddenStem,
                "정기 천간이 필요합니다."
        );

        hiddenStems =
                hiddenStems == null
                        ? List.of()
                        : List.copyOf(
                        hiddenStems
                );

        if (
                hiddenStems.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "지장간 목록은 비어 있을 수 없습니다."
            );
        }

        if (
                mainHiddenStemWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "정기 가중치는 0보다 커야 합니다."
            );
        }

        int calculatedTotal =
                hiddenStems
                        .stream()
                        .mapToInt(
                                HiddenStem::weight
                        )
                        .sum();

        if (
                calculatedTotal
                        != totalHiddenStemWeight
        ) {

            throw new IllegalArgumentException(
                    "지장간 전체 가중치가 실제 합계와 일치하지 않습니다."
            );
        }

        boolean mainExists =
                hiddenStems
                        .stream()
                        .anyMatch(
                                hiddenStem ->
                                        hiddenStem.main()
                                                && hiddenStem.stem()
                                                == mainHiddenStem
                        );

        if (
                !mainExists
        ) {

            throw new IllegalArgumentException(
                    "지장간 목록에서 정기를 찾을 수 없습니다."
            );
        }
    }

    public boolean yearBranch() {

        return position.isYear();
    }

    public boolean monthBranch() {

        return position.isMonth();
    }

    public boolean dayBranch() {

        return position.isDay();
    }

    public boolean hourBranch() {

        return position.isHour();
    }

    /**
     * 해당 지지에 특정 천간이
     * 숨겨져 있는지 확인.
     */
    public boolean contains(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return false;
        }

        return hiddenStems
                .stream()
                .anyMatch(
                        hiddenStem ->
                                hiddenStem.stem()
                                        == stem
                );
    }

    /**
     * 해당 지지에서 특정 천간이
     * 확보한 지장간 세력.
     */
    public int weightOf(
            HeavenlyStem stem
    ) {

        if (
                stem == null
        ) {

            return 0;
        }

        return hiddenStems
                .stream()
                .filter(
                        hiddenStem ->
                                hiddenStem.stem()
                                        == stem
                )
                .mapToInt(
                        HiddenStem::weight
                )
                .sum();
    }
}