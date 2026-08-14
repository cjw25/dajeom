package com.dajeom.analyzer;

import com.dajeom.calculator.TwelveLifeStageCalculator;
import com.dajeom.model.FourPillars;
import com.dajeom.model.TwelveLifeStageEvidence;
import com.dajeom.model.TwelveLifeStageEvidenceAnalysis;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 11차-3.
 *
 * 원국 년·월·일·시 지지에
 * 12운성을 적용하는 Analyzer.
 *
 * =================================================
 *
 * 기준 천간:
 *
 * 반드시 일간.
 *
 * 년간 기준 X
 * 월간 기준 X
 * 시간 기준 X
 *
 * =================================================
 *
 * 알려진 시간:
 *
 * YEAR
 * MONTH
 * DAY
 * HOUR
 *
 * 4개 Evidence.
 *
 * =================================================
 *
 * 시간 미상:
 *
 * YEAR
 * MONTH
 * DAY
 *
 * 3개 Evidence.
 *
 * HOUR Evidence를 만들지 않는다.
 *
 * =================================================
 *
 * 이 단계에서는 위치별 의미 해석을 하지 않는다.
 *
 * 예:
 *
 * 년지 장생이 무엇을 뜻하는가
 * 일지 묘가 무엇을 뜻하는가
 *
 * 같은 Context 해석은 11-4에서 수행한다.
 */
@Component
public class TwelveLifeStageEvidenceAnalyzer {

    private final TwelveLifeStageCalculator
            twelveLifeStageCalculator;

    public TwelveLifeStageEvidenceAnalyzer(
            TwelveLifeStageCalculator twelveLifeStageCalculator
    ) {

        this.twelveLifeStageCalculator =
                Objects.requireNonNull(
                        twelveLifeStageCalculator,
                        "TwelveLifeStageCalculator가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 11-3 API
     * =================================================
     */
    public TwelveLifeStageEvidenceAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "12운성 Evidence 분석에는 FourPillars가 필요합니다."
        );

        HeavenlyStem dayMaster =
                extractDayMaster(
                        fourPillars
                );

        List<TwelveLifeStageEvidence> evidences =
                new ArrayList<>();

        /*
         * =================================================
         * YEAR
         * =================================================
         */

        addEvidence(
                evidences,
                dayMaster,
                EarthlyBranchPosition.YEAR,
                extractYearBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * MONTH
         * =================================================
         */

        addEvidence(
                evidences,
                dayMaster,
                EarthlyBranchPosition.MONTH,
                extractMonthBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * DAY
         * =================================================
         */

        addEvidence(
                evidences,
                dayMaster,
                EarthlyBranchPosition.DAY,
                extractDayBranch(
                        fourPillars
                )
        );

        /*
         * =================================================
         * HOUR
         * =================================================
         *
         * 시간 미상일 경우
         * hourPillar 자체가 없으므로
         * Evidence를 생성하지 않는다.
         */

        if (
                fourPillars.hourPillar() != null
        ) {

            addEvidence(
                    evidences,
                    dayMaster,
                    EarthlyBranchPosition.HOUR,
                    extractHourBranch(
                            fourPillars
                    )
            );
        }

        boolean completeChart =
                fourPillars.hourPillar()
                        != null;

        int knownPillarCount =
                completeChart
                        ? 4
                        : 3;

        return new TwelveLifeStageEvidenceAnalysis(
                dayMaster,
                evidences,
                knownPillarCount,
                completeChart
        );
    }

    /*
     * =================================================
     * Evidence 생성
     * =================================================
     */

    private void addEvidence(

            List<TwelveLifeStageEvidence> evidences,

            HeavenlyStem dayMaster,

            EarthlyBranchPosition position,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                evidences,
                "12운성 Evidence 목록이 필요합니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "12운성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "12운성 Evidence에는 위치가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "12운성 Evidence에는 지지가 필요합니다."
        );

        TwelveLifeStage stage =
                twelveLifeStageCalculator
                        .calculate(
                                dayMaster,
                                branch
                        );

        evidences.add(
                new TwelveLifeStageEvidence(
                        dayMaster,
                        position,
                        branch,
                        stage
                )
        );
    }

    /*
     * =================================================
     * FourPillars 추출
     * =================================================
     *
     * 현재 11차에서는 원국의 실제 간지 객체를
     * 그대로 사용한다.
     *
     * FourPillars 내부 API가 최종 프로젝트에서
     * stem()/branch()가 아닌 다른 accessor를 사용한다면
     * 이 아래 추출부만 현재 API에 맞추면 된다.
     *
     * 11차 Model/Calculator에 호환 메서드를
     * 추가하지 않는다.
     */

    private HeavenlyStem extractDayMaster(
            FourPillars fourPillars
    ) {

        return Objects.requireNonNull(
                fourPillars
                        .dayPillar()
                        .stem(),
                "일간을 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractYearBranch(
            FourPillars fourPillars
    ) {

        return Objects.requireNonNull(
                fourPillars
                        .yearPillar()
                        .branch(),
                "년지를 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractMonthBranch(
            FourPillars fourPillars
    ) {

        return Objects.requireNonNull(
                fourPillars
                        .monthPillar()
                        .branch(),
                "월지를 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractDayBranch(
            FourPillars fourPillars
    ) {

        return Objects.requireNonNull(
                fourPillars
                        .dayPillar()
                        .branch(),
                "일지를 찾을 수 없습니다."
        );
    }

    private EarthlyBranch extractHourBranch(
            FourPillars fourPillars
    ) {

        if (
                fourPillars.hourPillar() == null
        ) {

            throw new IllegalStateException(
                    "시간 미상 원국에서는 시지 12운성을 계산할 수 없습니다."
            );
        }

        return Objects.requireNonNull(
                fourPillars
                        .hourPillar()
                        .branch(),
                "시지를 찾을 수 없습니다."
        );
    }
}