package com.dajeom.engine;

import com.dajeom.analyzer.MonthCommandAnalyzer;
import com.dajeom.analyzer.RootAnalyzer;
import com.dajeom.analyzer.StemExposureAnalyzer;
import com.dajeom.calculator.HiddenStemCalculator;
import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.MonthCommandAnalysis;
import com.dajeom.model.Pillar;
import com.dajeom.model.PillarHiddenStemAnalysis;
import com.dajeom.model.RootAnalysis;
import com.dajeom.model.StemExposureAnalysis;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StemExposurePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 지장간 / 통근 / 투간 / 월령을
 * 하나의 원국 기초세력 분석으로 통합한다.
 *
 * 처리 순서:
 *
 * FourPillars
 *      ↓
 * 지지별 지장간
 *      ↓
 * 월령
 *      ↓
 * 일간 통근
 *      ↓
 * 원국 천간 통근
 *      ↓
 * 지장간 투간
 *      ↓
 * 월지 지장간 투간
 *      ↓
 * BasePowerAnalysis
 *
 * 이후 3차 오행 세력 정밀화의
 * 입력 데이터로 사용한다.
 */
@Component
public class BasePowerAnalysisEngine {

    private final HiddenStemCalculator
            hiddenStemCalculator;

    private final RootAnalyzer
            rootAnalyzer;

    private final StemExposureAnalyzer
            stemExposureAnalyzer;

    private final MonthCommandAnalyzer
            monthCommandAnalyzer;

    public BasePowerAnalysisEngine(

            HiddenStemCalculator hiddenStemCalculator,

            RootAnalyzer rootAnalyzer,

            StemExposureAnalyzer stemExposureAnalyzer,

            MonthCommandAnalyzer monthCommandAnalyzer

    ) {

        this.hiddenStemCalculator =
                Objects.requireNonNull(
                        hiddenStemCalculator,
                        "HiddenStemCalculator가 필요합니다."
                );

        this.rootAnalyzer =
                Objects.requireNonNull(
                        rootAnalyzer,
                        "RootAnalyzer가 필요합니다."
                );

        this.stemExposureAnalyzer =
                Objects.requireNonNull(
                        stemExposureAnalyzer,
                        "StemExposureAnalyzer가 필요합니다."
                );

        this.monthCommandAnalyzer =
                Objects.requireNonNull(
                        monthCommandAnalyzer,
                        "MonthCommandAnalyzer가 필요합니다."
                );
    }

    /**
     * 원국의 기초 세력 분석.
     */
    public BasePowerAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        HeavenlyStem dayMaster =
                fourPillars
                        .dayPillar()
                        .stem();

        /*
         * 년지 / 월지 / 일지 / 시지의
         * 지장간을 위치별로 보존.
         */
        List<PillarHiddenStemAnalysis>
                pillarHiddenStemAnalyses =
                analyzeHiddenStemsByPosition(
                        fourPillars
                );

        /*
         * 월령.
         */
        MonthCommandAnalysis
                monthCommandAnalysis =
                monthCommandAnalyzer.analyze(
                        fourPillars
                );

        /*
         * 일간의 통근.
         */
        RootAnalysis dayMasterRootAnalysis =
                rootAnalyzer.analyzeDayMaster(
                        fourPillars
                );

        /*
         * 겉으로 드러난 모든 천간의 통근.
         */
        Map<HeavenlyStem, RootAnalysis>
                visibleStemRootAnalyses =
                rootAnalyzer.analyzeVisibleStems(
                        fourPillars
                );

        /*
         * 원국 전체 지장간의 투출 상태.
         */
        Map<HeavenlyStem, StemExposureAnalysis>
                hiddenStemExposureAnalyses =
                stemExposureAnalyzer
                        .analyzeAllHiddenStems(
                                fourPillars
                        );

        /*
         * 월지 지장간만 별도 분석.
         *
         * 이후 격국 판단에서 직접 사용.
         */
        Map<HeavenlyStem, StemExposureAnalysis>
                monthBranchExposureAnalyses =
                stemExposureAnalyzer
                        .analyzeMonthBranch(
                                fourPillars
                        );

        boolean dayMasterRooted =
                dayMasterRootAnalysis
                        .rooted();

        boolean dayMasterMonthRooted =
                dayMasterRootAnalysis
                        .monthRooted();

        boolean dayMasterMainQiRooted =
                dayMasterRootAnalysis
                        .mainHiddenStemRooted();

        boolean monthMainQiExposed =
                stemExposureAnalyzer
                        .isMonthMainQiExposed(
                                fourPillars
                        );

        return new BasePowerAnalysis(
                fourPillars,
                dayMaster,
                monthCommandAnalysis,
                pillarHiddenStemAnalyses,
                dayMasterRootAnalysis,
                visibleStemRootAnalyses,
                hiddenStemExposureAnalyses,
                monthBranchExposureAnalyses,
                dayMasterRooted,
                dayMasterMonthRooted,
                dayMasterMainQiRooted,
                monthMainQiExposed,
                dayMasterRootAnalysis
                        .totalRootWeight(),
                fourPillars
                        .knownPillarCount()
        );
    }

    /**
     * 원국의 각 지지를 위치별로 분석한다.
     *
     * Map<Pillar, ...>를 사용하지 않는 이유:
     *
     * 동일한 간지가 원국에서 두 번 등장할 경우
     * Pillar를 key로 사용하면 위치 정보가
     * 덮어써질 수 있기 때문이다.
     *
     * 따라서:
     *
     * 년
     * 월
     * 일
     * 시
     *
     * 순서를 가진 List로 보존한다.
     */
    private List<PillarHiddenStemAnalysis>
    analyzeHiddenStemsByPosition(
            FourPillars fourPillars
    ) {

        List<PillarHiddenStemAnalysis> result =
                new ArrayList<>();

        result.add(
                analyzePillarHiddenStem(
                        fourPillars.yearPillar(),
                        StemExposurePosition.YEAR
                )
        );

        result.add(
                analyzePillarHiddenStem(
                        fourPillars.monthPillar(),
                        StemExposurePosition.MONTH
                )
        );

        result.add(
                analyzePillarHiddenStem(
                        fourPillars.dayPillar(),
                        StemExposurePosition.DAY
                )
        );

        fourPillars
                .hourPillarOptional()
                .ifPresent(
                        hourPillar ->
                                result.add(
                                        analyzePillarHiddenStem(
                                                hourPillar,
                                                StemExposurePosition.HOUR
                                        )
                                )
                );

        return List.copyOf(
                result
        );
    }

    private PillarHiddenStemAnalysis
    analyzePillarHiddenStem(

            Pillar pillar,

            StemExposurePosition position

    ) {

        List<HiddenStem> hiddenStems =
                hiddenStemCalculator.calculate(
                        pillar
                );

        HiddenStem mainHiddenStem =
                hiddenStemCalculator
                        .getMainHiddenStem(
                                pillar.branch()
                        );

        int totalHiddenStemWeight =
                hiddenStems
                        .stream()
                        .mapToInt(
                                HiddenStem::weight
                        )
                        .sum();

        return new PillarHiddenStemAnalysis(
                position,
                pillar.branch(),
                mainHiddenStem.stem(),
                mainHiddenStem.weight(),
                totalHiddenStemWeight,
                hiddenStems
        );
    }
}