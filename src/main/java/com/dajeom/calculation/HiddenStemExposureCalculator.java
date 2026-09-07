package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.HiddenStemExposureAnalysis;
import com.dajeom.domain.HiddenStemExposureHit;
import com.dajeom.domain.PillarPosition;
import com.dajeom.rule.HiddenStemSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class HiddenStemExposureCalculator {

    private final HiddenStemSource hiddenStemSource;

    public HiddenStemExposureCalculator(
            HiddenStemSource hiddenStemSource
    ) {

        this.hiddenStemSource =
                Objects.requireNonNull(
                        hiddenStemSource,
                        "HiddenStemSource는 필수입니다."
                );
    }

    public HiddenStemExposureAnalysis analyze(

            HeavenlyStem yearStem,

            HeavenlyStem monthStem,

            HeavenlyStem dayStem,

            Optional<HeavenlyStem> hourStem,

            EarthlyBranch yearBranch,

            EarthlyBranch monthBranch,

            EarthlyBranch dayBranch,

            Optional<EarthlyBranch> hourBranch

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

        List<StemPositionValue> stems =
                new ArrayList<>();

        stems.add(
                new StemPositionValue(
                        PillarPosition.YEAR,
                        yearStem
                )
        );

        stems.add(
                new StemPositionValue(
                        PillarPosition.MONTH,
                        monthStem
                )
        );

        stems.add(
                new StemPositionValue(
                        PillarPosition.DAY,
                        dayStem
                )
        );

        hourStem.ifPresent(
                stem ->
                        stems.add(
                                new StemPositionValue(
                                        PillarPosition.HOUR,
                                        stem
                                )
                        )
        );

        List<HiddenStemExposureHit> result =
                new ArrayList<>();

        appendBranchExposure(
                result,
                PillarPosition.YEAR,
                yearBranch,
                stems
        );

        appendBranchExposure(
                result,
                PillarPosition.MONTH,
                monthBranch,
                stems
        );

        appendBranchExposure(
                result,
                PillarPosition.DAY,
                dayBranch,
                stems
        );

        hourBranch.ifPresent(
                branch ->
                        appendBranchExposure(
                                result,
                                PillarPosition.HOUR,
                                branch,
                                stems
                        )
        );

        return new HiddenStemExposureAnalysis(
                result
        );
    }

    private void appendBranchExposure(

            List<HiddenStemExposureHit> result,

            PillarPosition sourceBranchPosition,

            EarthlyBranch sourceBranch,

            List<StemPositionValue> visibleStems

    ) {

        List<HiddenStemComponent> hiddenStems =
                hiddenStemSource.hiddenStemsOf(
                        sourceBranch
                );

        if (
                hiddenStems == null
                        || hiddenStems.isEmpty()
        ) {

            return;
        }

        for (
                HiddenStemComponent hidden
                : hiddenStems
        ) {

            Objects.requireNonNull(
                    hidden,
                    "지장간 목록에 null이 포함될 수 없습니다."
            );

            for (
                    StemPositionValue visible
                    : visibleStems
            ) {

                if (
                        hidden.stem()
                                != visible.stem()
                ) {

                    continue;
                }

                result.add(
                        new HiddenStemExposureHit(
                                sourceBranchPosition,
                                sourceBranch,
                                hidden.stem(),
                                hidden.phase(),
                                visible.position()
                        )
                );
            }
        }
    }

    private record StemPositionValue(

            PillarPosition position,

            HeavenlyStem stem

    ) {

        private StemPositionValue {

            Objects.requireNonNull(
                    position
            );

            Objects.requireNonNull(
                    stem
            );
        }
    }
}