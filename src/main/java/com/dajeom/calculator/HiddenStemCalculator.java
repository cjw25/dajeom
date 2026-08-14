package com.dajeom.calculator;

import com.dajeom.constant.HiddenStemTable;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국의 지장간 계산을 담당한다.
 *
 * 역할:
 *
 * 1. 지지별 지장간 추출
 * 2. 정기 추출
 * 3. 특정 천간의 통근 가능 여부 확인
 * 4. 원국 전체 지장간 수집
 * 5. 천간별 내부 세력 weight 집계
 *
 * 이 클래스에서는 아직
 *
 * 통근 강도
 * 투간
 * 득령
 * 신강/신약
 *
 * 을 최종 판정하지 않는다.
 *
 * 그 부분은 다음 Analyzer 단계에서 처리한다.
 */
@Component
public class HiddenStemCalculator {

    /**
     * 하나의 지지에 대한 지장간.
     */
    public List<HiddenStem> calculate(
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return HiddenStemTable.get(
                branch
        );
    }

    /**
     * 하나의 Pillar에 대한 지장간.
     */
    public List<HiddenStem> calculate(
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        return calculate(
                pillar.branch()
        );
    }

    /**
     * 기존 코드에서 의미가 더 잘 드러나는 이름으로
     * 사용할 수 있는 alias.
     */
    public List<HiddenStem> getHiddenStems(
            EarthlyBranch branch
    ) {

        return calculate(
                branch
        );
    }

    /**
     * 지지의 정기.
     */
    public HiddenStem getMainHiddenStem(
            EarthlyBranch branch
    ) {

        return HiddenStemTable.main(
                branch
        );
    }

    /**
     * 특정 천간이 해당 지지에
     * 실제로 저장되어 있는지 확인.
     *
     * 다음 통근 분석의 기초가 된다.
     */
    public boolean hasRoot(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return HiddenStemTable.contains(
                branch,
                stem
        );
    }

    /**
     * 천간이 해당 지지에서 어떤 지장간 역할로
     * 존재하는지 확인.
     */
    public Optional<HiddenStem> findRoot(
            HeavenlyStem stem,
            EarthlyBranch branch
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "지지가 필요합니다."
        );

        return HiddenStemTable.find(
                branch,
                stem
        );
    }

    /**
     * 원국 전체의 지장간.
     *
     * 시주가 없는 경우
     * FourPillars.knownPillars()가 알아서 제외한다.
     */
    public List<HiddenStem> calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        List<HiddenStem> result =
                new ArrayList<>();

        for (
                Pillar pillar
                : fourPillars.knownPillars()
        ) {

            result.addAll(
                    calculate(
                            pillar
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 원국의 각 지지와 지장간을
     * 기둥 순서대로 반환.
     *
     * LinkedHashMap을 사용해
     * 년 → 월 → 일 → 시 순서를 유지한다.
     */
    public Map<Pillar, List<HiddenStem>>
    calculateByPillar(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Map<Pillar, List<HiddenStem>> result =
                new LinkedHashMap<>();

        for (
                Pillar pillar
                : fourPillars.knownPillars()
        ) {

            result.put(
                    pillar,
                    calculate(
                            pillar
                    )
            );
        }

        return Map.copyOf(
                result
        );
    }

    /**
     * 원국 전체 지장간에서
     * 각 천간이 확보한 내부 세력을 합산한다.
     *
     * 예:
     *
     * 申 속 庚 60
     * 酉 속 辛 100
     * 丑 속 辛 10
     *
     * 등을 별도로 집계한다.
     *
     * 아직 오행 단위로 합산하지 않고
     * 천간 단위 그대로 유지한다.
     *
     * 다음 단계 통근/투간에서 필요하다.
     */
    public Map<HeavenlyStem, Integer>
    calculateStemWeights(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Map<HeavenlyStem, Integer> result =
                new LinkedHashMap<>();

        /*
         * 10천간을 모두 0으로 초기화.
         */
        for (
                int index = 0;
                index < 10;
                index++
        ) {

            result.put(
                    HeavenlyStem.fromIndex(
                            index
                    ),
                    0
            );
        }

        for (
                Pillar pillar
                : fourPillars.knownPillars()
        ) {

            for (
                    HiddenStem hiddenStem
                    : calculate(
                    pillar
            )
            ) {

                result.compute(
                        hiddenStem.stem(),
                        (
                                stem,
                                currentWeight
                        ) ->
                                (
                                        currentWeight == null
                                                ? 0
                                                : currentWeight
                                )
                                        + hiddenStem.weight()
                );
            }
        }

        return Map.copyOf(
                result
        );
    }

    /**
     * 특정 천간이 원국 전체 지지에서
     * 확보한 지장간 세력 합계.
     */
    public int calculateRootWeight(
            FourPillars fourPillars,
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        return calculateStemWeights(
                fourPillars
        ).getOrDefault(
                stem,
                0
        );
    }
}