package com.dajeom.analyzer;

import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.Pillar;
import com.dajeom.model.StemExposureAnalysis;
import com.dajeom.model.StemExposureEvidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RootSourcePosition;
import com.dajeom.type.StemExposurePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 원국의 투간(透干)을 분석한다.
 *
 * 다점 투간 정책:
 *
 * 1. 지장간과 천간이 정확히 같은 글자여야
 *    투간으로 인정한다.
 *
 * 2. 같은 오행이지만 다른 음양 천간이면
 *    투간으로 인정하지 않는다.
 *
 * 예:
 *
 * 庚 지장간 + 庚 천간
 * -> 투간
 *
 * 庚 지장간 + 辛 천간
 * -> 투간 아님
 *
 * 3. 정기/중기/여기를 구분한다.
 *
 * 4. 년지/월지/일지/시지 근원을 구분한다.
 *
 * 5. 년간/월간/일간/시간 투출 위치를 구분한다.
 *
 * 6. 동일 천간이 두 개 이상 보이면
 *    중복 투출 상태를 별도로 보존한다.
 *
 * 7. 월지 지장간의 투출은
 *    이후 격국 판단에서 별도 활용한다.
 */
@Component
public class StemExposureAnalyzer {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    public StemExposureAnalyzer(
            HiddenStemCalculator hiddenStemCalculator
    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );
    }

    /**
     * 특정 천간의 투간 상태를 분석한다.
     */
    public StemExposureAnalysis analyze(

            FourPillars fourPillars,

            HeavenlyStem targetStem

    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Objects.requireNonNull(
                targetStem,
                "투간 분석 대상 천간이 필요합니다."
        );

        List<VisibleStem> visibleStems =
                collectVisibleStems(
                        fourPillars,
                        targetStem
                );

        List<HiddenSource> hiddenSources =
                collectHiddenSources(
                        fourPillars,
                        targetStem
                );

        List<StemExposureEvidence> evidences =
                new ArrayList<>();

        /*
         * 같은 지장간 천간이
         * 실제 천간에 존재하는 경우에만
         * 투간 Evidence를 만든다.
         */
        for (
                HiddenSource source
                : hiddenSources
        ) {

            for (
                    VisibleStem visibleStem
                    : visibleStems
            ) {

                evidences.add(
                        createEvidence(
                                source,
                                visibleStem
                        )
                );
            }
        }

        boolean exposed =
                !evidences.isEmpty();

        boolean monthBranchExposed =
                evidences
                        .stream()
                        .anyMatch(
                                StemExposureEvidence
                                        ::fromMonthBranch
                        );

        boolean mainQiExposed =
                evidences
                        .stream()
                        .anyMatch(
                                StemExposureEvidence
                                        ::mainQiExposure
                        );

        boolean monthMainQiExposed =
                evidences
                        .stream()
                        .anyMatch(
                                StemExposureEvidence
                                        ::monthMainQiExposure
                        );

        boolean samePillarExposed =
                evidences
                        .stream()
                        .anyMatch(
                                StemExposureEvidence
                                        ::samePillarExposure
                        );

        boolean repeatedExposure =
                visibleStems.size()
                        >= 2;

        int totalSourceWeight =
                hiddenSources
                        .stream()
                        .mapToInt(
                                HiddenSource
                                        ::effectiveSourceWeight
                        )
                        .sum();

        return new StemExposureAnalysis(
                targetStem,
                exposed,
                monthBranchExposed,
                mainQiExposed,
                monthMainQiExposed,
                samePillarExposed,
                repeatedExposure,
                visibleStems.size(),
                hiddenSources.size(),
                evidences.size(),
                totalSourceWeight,
                evidences
        );
    }

    /**
     * 원국에 존재하는 모든 지장간을 기준으로
     * 투간 여부를 분석한다.
     *
     * 동일 천간은 한 번만 분석한다.
     */
    public Map<HeavenlyStem, StemExposureAnalysis>
    analyzeAllHiddenStems(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        Set<HeavenlyStem> hiddenStemSet =
                new LinkedHashSet<>();

        for (
                Pillar pillar
                : fourPillars.knownPillars()
        ) {

            for (
                    HiddenStem hiddenStem
                    : hiddenStemCalculator.calculate(
                    pillar
            )
            ) {

                hiddenStemSet.add(
                        hiddenStem.stem()
                );
            }
        }

        Map<HeavenlyStem, StemExposureAnalysis> result =
                new LinkedHashMap<>();

        for (
                HeavenlyStem stem
                : hiddenStemSet
        ) {

            result.put(
                    stem,
                    analyze(
                            fourPillars,
                            stem
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    /**
     * 월지 지장간만 대상으로
     * 투간 여부를 분석한다.
     *
     * 격국 판단에서 직접 사용하게 될 메서드.
     */
    public Map<HeavenlyStem, StemExposureAnalysis>
    analyzeMonthBranch(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        List<HiddenStem> monthHiddenStems =
                hiddenStemCalculator.calculate(
                        fourPillars.monthPillar()
                );

        Map<HeavenlyStem, StemExposureAnalysis> result =
                new LinkedHashMap<>();

        for (
                HiddenStem hiddenStem
                : monthHiddenStems
        ) {

            result.put(
                    hiddenStem.stem(),
                    analyze(
                            fourPillars,
                            hiddenStem.stem()
                    )
            );
        }

        return Collections.unmodifiableMap(
                result
        );
    }

    /**
     * 특정 천간이 실제로 투간되었는지.
     */
    public boolean isExposed(

            FourPillars fourPillars,

            HeavenlyStem stem

    ) {

        return analyze(
                fourPillars,
                stem
        ).exposed();
    }

    /**
     * 월지 정기가 실제 천간에
     * 투출했는지 확인.
     */
    public boolean isMonthMainQiExposed(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        HiddenStem mainHiddenStem =
                hiddenStemCalculator
                        .getMainHiddenStem(
                                fourPillars
                                        .monthPillar()
                                        .branch()
                        );

        StemExposureAnalysis analysis =
                analyze(
                        fourPillars,
                        mainHiddenStem.stem()
                );

        return analysis
                .evidences()
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.fromMonthBranch()
                                        && evidence.mainQiExposure()
                );
    }

    /**
     * 특정 천간이 몇 군데 천간에
     * 드러나 있는지 계산한다.
     */
    public int countVisibleStem(
            FourPillars fourPillars,
            HeavenlyStem stem
    ) {

        return collectVisibleStems(
                fourPillars,
                stem
        ).size();
    }

    private List<VisibleStem> collectVisibleStems(

            FourPillars fourPillars,

            HeavenlyStem targetStem

    ) {

        List<VisibleStem> result =
                new ArrayList<>();

        addVisibleStem(
                result,
                fourPillars.yearPillar(),
                StemExposurePosition.YEAR,
                targetStem
        );

        addVisibleStem(
                result,
                fourPillars.monthPillar(),
                StemExposurePosition.MONTH,
                targetStem
        );

        addVisibleStem(
                result,
                fourPillars.dayPillar(),
                StemExposurePosition.DAY,
                targetStem
        );

        fourPillars
                .hourPillarOptional()
                .ifPresent(
                        hourPillar ->
                                addVisibleStem(
                                        result,
                                        hourPillar,
                                        StemExposurePosition.HOUR,
                                        targetStem
                                )
                );

        return List.copyOf(
                result
        );
    }

    private void addVisibleStem(

            List<VisibleStem> result,

            Pillar pillar,

            StemExposurePosition position,

            HeavenlyStem targetStem

    ) {

        if (
                pillar.stem()
                        == targetStem
        ) {

            result.add(
                    new VisibleStem(
                            position,
                            pillar.stem()
                    )
            );
        }
    }

    private List<HiddenSource> collectHiddenSources(

            FourPillars fourPillars,

            HeavenlyStem targetStem

    ) {

        List<HiddenSource> result =
                new ArrayList<>();

        collectHiddenSourcesFromPillar(
                result,
                fourPillars.yearPillar(),
                StemExposurePosition.YEAR,
                targetStem
        );

        collectHiddenSourcesFromPillar(
                result,
                fourPillars.monthPillar(),
                StemExposurePosition.MONTH,
                targetStem
        );

        collectHiddenSourcesFromPillar(
                result,
                fourPillars.dayPillar(),
                StemExposurePosition.DAY,
                targetStem
        );

        fourPillars
                .hourPillarOptional()
                .ifPresent(
                        hourPillar ->
                                collectHiddenSourcesFromPillar(
                                        result,
                                        hourPillar,
                                        StemExposurePosition.HOUR,
                                        targetStem
                                )
                );

        return List.copyOf(
                result
        );
    }

    private void collectHiddenSourcesFromPillar(

            List<HiddenSource> result,

            Pillar pillar,

            StemExposurePosition position,

            HeavenlyStem targetStem

    ) {

        for (
                HiddenStem hiddenStem
                : hiddenStemCalculator.calculate(
                pillar
        )
        ) {

            /*
             * 투간은 같은 오행이 아니라
             * 정확히 같은 천간만 인정.
             */
            if (
                    hiddenStem.stem()
                            != targetStem
            ) {

                continue;
            }

            int positionWeightRate =
                    determineSourcePositionWeightRate(
                            position
                    );

            int effectiveSourceWeight =
                    calculateEffectiveSourceWeight(
                            hiddenStem,
                            positionWeightRate
                    );

            result.add(
                    new HiddenSource(
                            position,
                            pillar.branch(),
                            hiddenStem,
                            positionWeightRate,
                            effectiveSourceWeight
                    )
            );
        }
    }

    private StemExposureEvidence createEvidence(

            HiddenSource source,

            VisibleStem target

    ) {

        return new StemExposureEvidence(
                source.hiddenStem().stem(),
                source.position(),
                source.branch(),
                source.hiddenStem().role(),
                source.hiddenStem().weight(),
                source.positionWeightRate(),
                source.effectiveSourceWeight(),
                target.position()
        );
    }

    /**
     * 2차-2 통근에서 사용한
     * 지지 위치 기준과 동일하게 맞춘다.
     *
     * 월령의 계절 세력과는 별개의 값.
     */
    private int determineSourcePositionWeightRate(
            StemExposurePosition position
    ) {

        return switch (
                position
                ) {

            case YEAR ->
                    RootSourcePosition
                            .YEAR
                            .weightRate();

            case MONTH ->
                    RootSourcePosition
                            .MONTH
                            .weightRate();

            case DAY ->
                    RootSourcePosition
                            .DAY
                            .weightRate();

            case HOUR ->
                    RootSourcePosition
                            .HOUR
                            .weightRate();
        };
    }

    /**
     * 지장간 세력 × 지지 위치 가중치.
     *
     * 천간 노출 횟수 자체를 곱하지 않는다.
     *
     * 예를 들어 같은 庚이
     * 년간과 월간에 모두 나타난다고 해서
     * 지장간 申 속 庚 자체의 근원 세력이
     * 두 배가 되는 것은 아니기 때문이다.
     */
    private int calculateEffectiveSourceWeight(

            HiddenStem hiddenStem,

            int positionWeightRate

    ) {

        long weighted =
                (long) hiddenStem.weight()
                        * positionWeightRate;

        return Math.max(
                1,
                (int) Math.round(
                        weighted
                                / 100.0
                )
        );
    }

    /**
     * 겉으로 드러난 천간.
     */
    private record VisibleStem(

            StemExposurePosition position,

            HeavenlyStem stem

    ) {

        private VisibleStem {

            Objects.requireNonNull(
                    position
            );

            Objects.requireNonNull(
                    stem
            );
        }
    }

    /**
     * 투간의 근원이 되는 지장간.
     */
    private record HiddenSource(

            StemExposurePosition position,

            EarthlyBranch branch,

            HiddenStem hiddenStem,

            int positionWeightRate,

            int effectiveSourceWeight

    ) {

        private HiddenSource {

            Objects.requireNonNull(
                    position
            );

            Objects.requireNonNull(
                    branch
            );

            Objects.requireNonNull(
                    hiddenStem
            );
        }
    }
}