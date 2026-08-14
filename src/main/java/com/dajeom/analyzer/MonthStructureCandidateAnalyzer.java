package com.dajeom.analyzer;

import com.dajeom.calculator.StructureTenGodCalculator;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.HiddenStem;
import com.dajeom.model.MonthStructureCandidate;
import com.dajeom.model.MonthStructureCandidateAnalysis;
import com.dajeom.model.PillarHiddenStemAnalysis;
import com.dajeom.model.StemExposureAnalysis;
import com.dajeom.model.StemExposureEvidence;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StemExposurePosition;
import com.dajeom.type.StructureTenGodType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 5차-1.
 *
 * 월지 정기·중기·여기를
 * 격 후보로 추출한다.
 *
 * 정확한 십성 계산은
 * StructureTenGodCalculator에 위임한다.
 */
@Component
public class MonthStructureCandidateAnalyzer {

    private final StructureTenGodCalculator
            structureTenGodCalculator;

    public MonthStructureCandidateAnalyzer(
            StructureTenGodCalculator
                    structureTenGodCalculator
    ) {

        this.structureTenGodCalculator =
                Objects.requireNonNull(
                        structureTenGodCalculator,
                        "StructureTenGodCalculator가 필요합니다."
                );
    }

    public MonthStructureCandidateAnalysis analyze(
            DayMasterStrengthAnalysis strength
    ) {

        Objects.requireNonNull(
                strength,
                "신강·신약 분석 결과가 필요합니다."
        );

        HeavenlyStem dayMaster =
                strength.dayMaster();

        var basePower =
                strength
                        .elementStrengthAnalysis()
                        .basePowerAnalysis();

        var monthCommand =
                basePower
                        .monthCommandAnalysis();

        /*
         * hiddenStemAnalysisOfMonthBranch()는
         * Optional<PillarHiddenStemAnalysis>를 반환한다.
         *
         * Optional 자체에 hiddenStems()를 호출하지 않고
         * 실제 분석 객체를 꺼낸 뒤 사용한다.
         */
        PillarHiddenStemAnalysis monthHiddenAnalysis =
                basePower
                        .hiddenStemAnalysisOfMonthBranch()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "월지 지장간 분석을 찾을 수 없습니다."
                                        )
                        );

        List<MonthStructureCandidate> candidates =
                new ArrayList<>();

        for (
                HiddenStem hiddenStem
                : monthHiddenAnalysis.hiddenStems()
        ) {

            HeavenlyStem candidateStem =
                    hiddenStem.stem();

            StructureTenGodType tenGod =
                    structureTenGodCalculator
                            .calculate(
                                    dayMaster,
                                    candidateStem
                            );

            StemExposureAnalysis exposure =
                    basePower
                            .hiddenStemExposureAnalyses()
                            .get(
                                    candidateStem
                            );

            boolean exposedAtYearStem =
                    isExposedAt(
                            exposure,
                            StemExposurePosition.YEAR
                    );

            boolean exposedAtMonthStem =
                    isExposedAt(
                            exposure,
                            StemExposurePosition.MONTH
                    );

            boolean exposedAtHourStem =
                    isExposedAt(
                            exposure,
                            StemExposurePosition.HOUR
                    );

            int visibleStemCount =
                    countStructureVisiblePositions(
                            exposure
                    );

            boolean repeatedExposure =
                    visibleStemCount >= 2;

            int exposureSourceWeight =
                    exposure == null
                            ? 0
                            : exposure.totalSourceWeight();

            candidates.add(
                    new MonthStructureCandidate(
                            dayMaster,
                            candidateStem,
                            hiddenStem.role(),
                            hiddenStem.weight(),
                            tenGod,
                            exposedAtYearStem
                                    || exposedAtMonthStem
                                    || exposedAtHourStem,
                            exposedAtYearStem,
                            exposedAtMonthStem,
                            exposedAtHourStem,
                            repeatedExposure,
                            visibleStemCount,
                            exposureSourceWeight
                    )
            );
        }

        return new MonthStructureCandidateAnalysis(
                dayMaster,
                monthCommand.monthBranch(),
                monthCommand.mainHiddenStem(),
                candidates
        );
    }

    private boolean isExposedAt(

            StemExposureAnalysis exposure,

            StemExposurePosition position

    ) {

        if (
                exposure == null
                        || !exposure.exposed()
        ) {

            return false;
        }

        return exposure
                .evidences()
                .stream()
                .anyMatch(
                        evidence ->
                                evidence.targetPosition()
                                        == position
                );
    }

    private int countStructureVisiblePositions(
            StemExposureAnalysis exposure
    ) {

        if (
                exposure == null
                        || !exposure.exposed()
        ) {

            return 0;
        }

        EnumSet<StemExposurePosition> positions =
                EnumSet.noneOf(
                        StemExposurePosition.class
                );

        for (
                StemExposureEvidence evidence
                : exposure.evidences()
        ) {

            StemExposurePosition position =
                    evidence.targetPosition();

            /*
             * 일간 자신은
             * 월령 격의 투출 위치로 세지 않는다.
             */
            if (
                    position
                            == StemExposurePosition.DAY
            ) {

                continue;
            }

            positions.add(
                    position
            );
        }

        return positions.size();
    }
}