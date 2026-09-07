package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.TwelveShinsal;
import com.dajeom.domain.TwelveShinsalAnalysis;
import com.dajeom.domain.TwelveShinsalGroup;
import com.dajeom.rule.TwelveShinsalRule;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TwelveShinsalCalculator {

    public List<TwelveShinsalAnalysis> calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        /*
         * =====================================================
         * 세 가지 기준
         * =====================================================
         */

        EarthlyBranch yearBranchReference =
                fourPillars
                        .year()
                        .branch();

        EarthlyBranch dayBranchReference =
                fourPillars
                        .day()
                        .branch();

        HeavenlyStem dayStemReference =
                fourPillars.dayStem();

        TwelveShinsalGroup yearBranchGroup =
                TwelveShinsalRule.groupOf(
                        yearBranchReference
                );

        TwelveShinsalGroup dayBranchGroup =
                TwelveShinsalRule.groupOf(
                        dayBranchReference
                );

        TwelveShinsalGroup dayStemGroup =
                TwelveShinsalRule.groupOf(
                        dayStemReference
                );

        List<TwelveShinsalAnalysis> result =
                new ArrayList<>();

        /*
         * 년주
         */
        result.add(
                analyze(
                        PillarPosition.YEAR,
                        fourPillars.year(),

                        yearBranchReference,
                        yearBranchGroup,

                        dayBranchReference,
                        dayBranchGroup,

                        dayStemReference,
                        dayStemGroup
                )
        );

        /*
         * 월주
         */
        result.add(
                analyze(
                        PillarPosition.MONTH,
                        fourPillars.month(),

                        yearBranchReference,
                        yearBranchGroup,

                        dayBranchReference,
                        dayBranchGroup,

                        dayStemReference,
                        dayStemGroup
                )
        );

        /*
         * 일주
         */
        result.add(
                analyze(
                        PillarPosition.DAY,
                        fourPillars.day(),

                        yearBranchReference,
                        yearBranchGroup,

                        dayBranchReference,
                        dayBranchGroup,

                        dayStemReference,
                        dayStemGroup
                )
        );

        /*
         * 시주
         */
        if (fourPillars.hasKnownHour()) {

            result.add(
                    analyze(
                            PillarPosition.HOUR,
                            fourPillars.hour(),

                            yearBranchReference,
                            yearBranchGroup,

                            dayBranchReference,
                            dayBranchGroup,

                            dayStemReference,
                            dayStemGroup
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private TwelveShinsalAnalysis analyze(
            PillarPosition position,
            Pillar pillar,

            EarthlyBranch yearBranchReference,
            TwelveShinsalGroup yearBranchGroup,

            EarthlyBranch dayBranchReference,
            TwelveShinsalGroup dayBranchGroup,

            HeavenlyStem dayStemReference,
            TwelveShinsalGroup dayStemGroup
    ) {

        Objects.requireNonNull(
                position
        );

        Objects.requireNonNull(
                pillar
        );

        EarthlyBranch targetBranch =
                pillar.branch();

        TwelveShinsal yearBranchShinsal =
                TwelveShinsalRule.calculate(
                        yearBranchGroup,
                        targetBranch
                );

        TwelveShinsal dayBranchShinsal =
                TwelveShinsalRule.calculate(
                        dayBranchGroup,
                        targetBranch
                );

        TwelveShinsal dayStemShinsal =
                TwelveShinsalRule.calculate(
                        dayStemGroup,
                        targetBranch
                );

        return new TwelveShinsalAnalysis(
                position,
                targetBranch,

                yearBranchReference,
                yearBranchGroup,
                yearBranchShinsal,

                dayBranchReference,
                dayBranchGroup,
                dayBranchShinsal,

                dayStemReference,
                dayStemGroup,
                dayStemShinsal
        );
    }
}