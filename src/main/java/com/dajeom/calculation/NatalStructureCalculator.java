package com.dajeom.calculation;

import com.dajeom.domain.BranchGroupRelation;
import com.dajeom.domain.BranchRelation;
import com.dajeom.domain.DayMasterForceFormationAnalysis;
import com.dajeom.domain.DayMasterGroundAnalysis;
import com.dajeom.domain.DayMasterRelationAnalysis;
import com.dajeom.domain.DayMasterSeasonAnalysis;
import com.dajeom.domain.DayMasterSupportStructure;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemExposureAnalysis;
import com.dajeom.domain.MonthCommandAnalysis;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import com.dajeom.domain.NatalRootAnalysis;
import com.dajeom.domain.NatalStructureAnalysis;
import com.dajeom.domain.StemGroundAnalysis;
import com.dajeom.domain.StemRelation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class NatalStructureCalculator {

    private final RootingCalculator
            rootingCalculator;

    private final HiddenStemExposureCalculator
            hiddenStemExposureCalculator;

    private final MonthCommandCalculator
            monthCommandCalculator;

    private final MonthCommandDayMasterCalculator
            monthCommandDayMasterCalculator;

    private final DayMasterSeasonCalculator
            dayMasterSeasonCalculator;

    private final DayMasterRelationCalculator
            dayMasterRelationCalculator;

    private final DayMasterSupportStructureCalculator
            dayMasterSupportStructureCalculator;

    private final DayMasterGroundCalculator
            dayMasterGroundCalculator;

    private final DayMasterForceFormationCalculator
            dayMasterForceFormationCalculator;

    public NatalStructureCalculator(

            RootingCalculator rootingCalculator,

            HiddenStemExposureCalculator
                    hiddenStemExposureCalculator,

            MonthCommandCalculator
                    monthCommandCalculator,

            MonthCommandDayMasterCalculator
                    monthCommandDayMasterCalculator,

            DayMasterSeasonCalculator
                    dayMasterSeasonCalculator,

            DayMasterRelationCalculator
                    dayMasterRelationCalculator,

            DayMasterSupportStructureCalculator
                    dayMasterSupportStructureCalculator,

            DayMasterGroundCalculator
                    dayMasterGroundCalculator,

            DayMasterForceFormationCalculator
                    dayMasterForceFormationCalculator

    ) {

        this.rootingCalculator =
                Objects.requireNonNull(
                        rootingCalculator,
                        "RootingCalculator는 필수입니다."
                );

        this.hiddenStemExposureCalculator =
                Objects.requireNonNull(
                        hiddenStemExposureCalculator,
                        "HiddenStemExposureCalculator는 필수입니다."
                );

        this.monthCommandCalculator =
                Objects.requireNonNull(
                        monthCommandCalculator,
                        "MonthCommandCalculator는 필수입니다."
                );

        this.monthCommandDayMasterCalculator =
                Objects.requireNonNull(
                        monthCommandDayMasterCalculator,
                        "MonthCommandDayMasterCalculator는 필수입니다."
                );

        this.dayMasterSeasonCalculator =
                Objects.requireNonNull(
                        dayMasterSeasonCalculator,
                        "DayMasterSeasonCalculator는 필수입니다."
                );

        this.dayMasterRelationCalculator =
                Objects.requireNonNull(
                        dayMasterRelationCalculator,
                        "DayMasterRelationCalculator는 필수입니다."
                );

        this.dayMasterSupportStructureCalculator =
                Objects.requireNonNull(
                        dayMasterSupportStructureCalculator,
                        "DayMasterSupportStructureCalculator는 필수입니다."
                );

        this.dayMasterGroundCalculator =
                Objects.requireNonNull(
                        dayMasterGroundCalculator,
                        "DayMasterGroundCalculator는 필수입니다."
                );

        this.dayMasterForceFormationCalculator =
                Objects.requireNonNull(
                        dayMasterForceFormationCalculator,
                        "DayMasterForceFormationCalculator는 필수입니다."
                );
    }

    public NatalStructureAnalysis analyze(

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            HeavenlyStem dayStem,

            Optional<HeavenlyStem> hourStem,

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch,

            List<StemRelation> stemRelations,

            List<BranchRelation> branchRelations,

            List<BranchGroupRelation> branchGroupRelations

    ) {

        Objects.requireNonNull(
                yearStem,
                "년간은 필수입니다."
        );

        Objects.requireNonNull(
                monthStem,
                "월간은 필수입니다."
        );

        Objects.requireNonNull(
                dayStem,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                hourStem,
                "시간 Optional은 필수입니다."
        );

        Objects.requireNonNull(
                yearBranch,
                "년지는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지는 필수입니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지는 필수입니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지 Optional은 필수입니다."
        );

        validateHourConsistency(
                hourStem,
                hourBranch
        );

        StemGroundAnalysis yearRooting =
                rootingCalculator.analyze(
                        yearStem,
                        yearBranch,
                        monthBranch,
                        dayBranch,
                        hourBranch
                );

        StemGroundAnalysis monthRooting =
                rootingCalculator.analyze(
                        monthStem,
                        yearBranch,
                        monthBranch,
                        dayBranch,
                        hourBranch
                );

        StemGroundAnalysis dayRooting =
                rootingCalculator.analyze(
                        dayStem,
                        yearBranch,
                        monthBranch,
                        dayBranch,
                        hourBranch
                );

        Optional<StemGroundAnalysis> hourRooting =
                hourStem.map(
                        stem ->
                                rootingCalculator.analyze(
                                        stem,
                                        yearBranch,
                                        monthBranch,
                                        dayBranch,
                                        hourBranch
                                )
                );

        NatalRootAnalysis rooting =
                new NatalRootAnalysis(
                        yearRooting,
                        monthRooting,
                        dayRooting,
                        hourRooting
                );

        HiddenStemExposureAnalysis exposure =
                hiddenStemExposureCalculator.analyze(
                        yearStem,
                        monthStem,
                        dayStem,
                        hourStem,
                        yearBranch,
                        monthBranch,
                        dayBranch,
                        hourBranch
                );

        MonthCommandAnalysis monthCommand =
                monthCommandCalculator.analyze(
                        monthBranch,
                        exposure
                );

        MonthCommandDayMasterAnalysis
                monthCommandDayMaster =
                monthCommandDayMasterCalculator.analyze(
                        dayStem,
                        monthCommand
                );

        DayMasterSeasonAnalysis
                dayMasterSeason =
                dayMasterSeasonCalculator.analyze(
                        dayStem,
                        monthBranch
                );

        DayMasterRelationAnalysis
                dayMasterRelations =
                dayMasterRelationCalculator.analyze(
                        dayStem,
                        yearStem,
                        monthStem,
                        hourStem,
                        yearBranch,
                        monthBranch,
                        dayBranch,
                        hourBranch
                );

        DayMasterSupportStructure
                dayMasterSupport =
                dayMasterSupportStructureCalculator.analyze(
                        dayStem,
                        dayRooting,
                        monthCommandDayMaster,
                        dayMasterRelations
                );

        DayMasterGroundAnalysis
                dayMasterGround =
                dayMasterGroundCalculator.analyze(
                        dayStem,
                        dayRooting
                );

        DayMasterForceFormationAnalysis
                dayMasterForceFormation =
                dayMasterForceFormationCalculator.analyze(
                        dayStem,
                        dayMasterRelations
                );

        return new NatalStructureAnalysis(
                dayStem,
                rooting,
                exposure,
                monthCommand,
                monthCommandDayMaster,
                dayMasterSeason,
                dayMasterRelations,
                dayMasterSupport,
                dayMasterGround,
                dayMasterForceFormation,
                stemRelations,
                branchRelations,
                branchGroupRelations
        );
    }

    private void validateHourConsistency(

            Optional<HeavenlyStem> hourStem,

            Optional<EarthlyBranch> hourBranch

    ) {

        if (
                hourStem.isPresent()
                        != hourBranch.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "시간과 시지는 함께 존재하거나 함께 없어야 합니다."
            );
        }
    }
}