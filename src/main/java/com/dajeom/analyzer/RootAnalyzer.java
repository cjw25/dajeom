package com.dajeom.analyzer;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.model.RootAnalysis;
import com.dajeom.model.RootEvidence;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RootRelationType;
import com.dajeom.type.RootSourcePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 원국의 통근(通根)을 분석한다.
 *
 * 계산 기준:
 *
 * 1. 천간과 지장간이 동일한 경우
 *    -> 직접 통근
 *
 * 2. 천간과 지장간의 오행이 같지만
 *    음양 천간이 다른 경우
 *    -> 동기 보조근
 *
 * 3. 정기/중기/여기의 세력 차이는
 *    HiddenStem.weight를 사용한다.
 *
 * 4. 년지/월지/일지/시지 위치 차이를
 *    RootSourcePosition 가중치로 반영한다.
 *
 * 5. 월령의 실제 계절 세력은
 *    이 클래스에서 계산하지 않는다.
 *
 * 통근은 이후:
 *
 * 오행 세력
 * 신강·신약
 * 격국
 * 용신
 *
 * 판정의 근거 데이터로 사용한다.
 */
@Component
public class RootAnalyzer {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    public RootAnalyzer(
            HiddenStemCalculator hiddenStemCalculator
    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );
    }

    /**
     * 일간의 통근을 분석한다.
     *
     * 신강/신약 계산에서 가장 직접적으로
     * 사용하게 될 메서드.
     */
    public RootAnalysis analyzeDayMaster(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        return analyze(
                fourPillars,
                fourPillars
                        .dayPillar()
                        .stem()
        );
    }

    /**
     * 특정 천간의 원국 전체 통근 분석.
     */
    public RootAnalysis analyze(
            FourPillars fourPillars,
            HeavenlyStem targetStem
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Objects.requireNonNull(
                targetStem,
                "통근 대상 천간이 필요합니다."
        );

        List<RootEvidence> evidences =
                new ArrayList<>();

        /*
         * 년지.
         */
        collectRootEvidence(
                targetStem,
                fourPillars.yearPillar(),
                RootSourcePosition.YEAR,
                evidences
        );

        /*
         * 월지.
         */
        collectRootEvidence(
                targetStem,
                fourPillars.monthPillar(),
                RootSourcePosition.MONTH,
                evidences
        );

        /*
         * 일지.
         */
        collectRootEvidence(
                targetStem,
                fourPillars.dayPillar(),
                RootSourcePosition.DAY,
                evidences
        );

        /*
         * 시지는 출생시간을 알고 있는 경우에만.
         */
        fourPillars
                .hourPillarOptional()
                .ifPresent(
                        hourPillar ->
                                collectRootEvidence(
                                        targetStem,
                                        hourPillar,
                                        RootSourcePosition.HOUR,
                                        evidences
                                )
                );

        int directRootWeight =
                evidences
                        .stream()
                        .filter(
                                RootEvidence::directRoot
                        )
                        .mapToInt(
                                RootEvidence::effectiveWeight
                        )
                        .sum();

        int supportingRootWeight =
                evidences
                        .stream()
                        .filter(
                                RootEvidence::supportingRoot
                        )
                        .mapToInt(
                                RootEvidence::effectiveWeight
                        )
                        .sum();

        int totalRootWeight =
                directRootWeight
                        + supportingRootWeight;

        boolean rooted =
                !evidences.isEmpty();

        boolean directRooted =
                directRootWeight > 0;

        boolean monthRooted =
                evidences
                        .stream()
                        .anyMatch(
                                RootEvidence::monthRoot
                        );

        boolean mainHiddenStemRooted =
                evidences
                        .stream()
                        .anyMatch(
                                RootEvidence::mainHiddenStemRoot
                        );

        return new RootAnalysis(
                targetStem,
                rooted,
                directRooted,
                monthRooted,
                mainHiddenStemRooted,
                directRootWeight,
                supportingRootWeight,
                totalRootWeight,
                evidences.size(),
                evidences
        );
    }

    /**
     * 원국 천간 전체의 통근 상태를 계산한다.
     *
     * 동일 천간이 여러 위치에 나타나더라도
     * 같은 천간의 원국 전체 뿌리 구조는 동일하므로
     * 천간별로 한 번만 분석한다.
     */
    public Map<HeavenlyStem, RootAnalysis>
    analyzeVisibleStems(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Map<HeavenlyStem, RootAnalysis> result =
                new LinkedHashMap<>();

        for (
                Pillar pillar
                : fourPillars.knownPillars()
        ) {

            HeavenlyStem stem =
                    pillar.stem();

            result.computeIfAbsent(
                    stem,
                    key ->
                            analyze(
                                    fourPillars,
                                    key
                            )
            );
        }

        return Map.copyOf(
                result
        );
    }

    /**
     * 모든 10천간의 통근 상태를 계산.
     *
     * 다음 단계에서 오행 전체의 잠재 세력을
     * 분석할 때 사용할 수 있다.
     */
    public Map<HeavenlyStem, RootAnalysis>
    analyzeAllHeavenlyStems(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Map<HeavenlyStem, RootAnalysis> result =
                new LinkedHashMap<>();

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            HeavenlyStem stem =
                    HeavenlyStem.fromIndex(
                            index
                    );

            result.put(
                    stem,
                    analyze(
                            fourPillars,
                            stem
                    )
            );
        }

        return Map.copyOf(
                result
        );
    }

    private void collectRootEvidence(

            HeavenlyStem targetStem,

            Pillar sourcePillar,

            RootSourcePosition sourcePosition,

            List<RootEvidence> result

    ) {

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator.calculate(
                        sourcePillar
                );

        for (
                HiddenStem hiddenStem
                : hiddenStems
        ) {

            RootRelationType relationType =
                    determineRelationType(
                            targetStem,
                            hiddenStem.stem()
                    );

            /*
             * 오행 자체가 다른 경우
             * 통근 근거가 아니다.
             */
            if (
                    relationType == null
            ) {
                continue;
            }

            int effectiveWeight =
                    calculateEffectiveWeight(
                            hiddenStem,
                            sourcePosition,
                            relationType
                    );

            result.add(
                    new RootEvidence(
                            targetStem,
                            sourcePosition,
                            sourcePillar.branch(),
                            hiddenStem.stem(),
                            hiddenStem.role(),
                            relationType,
                            hiddenStem.weight(),
                            sourcePosition.weightRate(),
                            relationType.weightRate(),
                            effectiveWeight
                    )
            );
        }
    }

    /**
     * 대상 천간과 지장간 관계 판정.
     */
    private RootRelationType determineRelationType(

            HeavenlyStem targetStem,

            HeavenlyStem hiddenStem

    ) {

        if (
                targetStem
                        == hiddenStem
        ) {

            return RootRelationType.SAME_STEM;
        }

        if (
                sameElement(
                        targetStem,
                        hiddenStem
                )
        ) {

            return RootRelationType.SAME_ELEMENT;
        }

        return null;
    }

    /**
     * 지장간 기본 세력
     * × 위치 가중치
     * × 통근 관계 가중치.
     *
     * 예:
     *
     * 정기 60
     * 월지 115%
     * 동일 천간 100%
     *
     * 60 × 1.15 × 1.00
     * = 69
     *
     * 숫자는 상대 세력값이며
     * 확률이 아니다.
     */
    private int calculateEffectiveWeight(

            HiddenStem hiddenStem,

            RootSourcePosition sourcePosition,

            RootRelationType relationType

    ) {

        long weighted =
                (long) hiddenStem.weight()
                        * sourcePosition.weightRate()
                        * relationType.weightRate();

        return Math.max(
                1,
                (int) Math.round(
                        weighted
                                / 10_000.0
                )
        );
    }

    /**
     * 두 천간이 같은 오행인지 판단한다.
     *
     * HeavenlyStem enum의 내부 필드명에
     * 의존하지 않도록 10천간 표준 순서를 이용한다.
     *
     * 甲乙 = 木
     * 丙丁 = 火
     * 戊己 = 土
     * 庚辛 = 金
     * 壬癸 = 水
     */
    private boolean sameElement(

            HeavenlyStem first,

            HeavenlyStem second

    ) {

        int firstIndex =
                findStemIndex(
                        first
                );

        int secondIndex =
                findStemIndex(
                        second
                );

        return firstIndex / 2
                == secondIndex / 2;
    }

    /**
     * HeavenlyStem.fromIndex()와 역으로 대응되는
     * 표준 천간 index를 찾는다.
     *
     * enum ordinal에 직접 의존하지 않는다.
     */
    private int findStemIndex(
            HeavenlyStem stem
    ) {

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    )
                            == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }
}