package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.SpecialStarHit;
import com.dajeom.domain.SpecialStarReferenceType;
import com.dajeom.domain.SpecialStarType;
import com.dajeom.rule.SpecialStarRule;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class SpecialStarCalculator {

    public List<SpecialStarHit> calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        HeavenlyStem dayStem =
                fourPillars.dayStem();

        EarthlyBranch monthBranch =
                fourPillars
                        .month()
                        .branch();

        List<PositionedPillar> pillars =
                collectPillars(
                        fourPillars
                );

        List<SpecialStarHit> result =
                new ArrayList<>();

        for (
                SpecialStarType type
                : SpecialStarType.values()
        ) {

            SpecialStarReferenceType referenceType =
                    SpecialStarRule.referenceType(
                            type
                    );

            switch (referenceType) {

                case DAY_STEM_BRANCH ->
                        calculateDayStemBranch(
                                result,
                                type,
                                dayStem,
                                pillars
                        );

                case PILLAR_GANJI ->
                        calculatePillarGanji(
                                result,
                                type,
                                pillars
                        );

                case PILLAR_CHARACTER ->
                        calculatePillarCharacter(
                                result,
                                type,
                                pillars
                        );

                case MONTH_BRANCH_CHARACTER ->
                        calculateMonthBranchCharacter(
                                result,
                                type,
                                monthBranch,
                                pillars
                        );
            }
        }

        return List.copyOf(
                result
        );
    }

    // =========================================================
    // 일간 → 지지
    // =========================================================

    private void calculateDayStemBranch(
            List<SpecialStarHit> result,
            SpecialStarType type,
            HeavenlyStem dayStem,
            List<PositionedPillar> pillars
    ) {

        List<EarthlyBranch> expectedBranches =
                SpecialStarRule.targetBranches(
                        type,
                        dayStem
                );

        for (
                EarthlyBranch expectedBranch
                : expectedBranches
        ) {

            for (
                    PositionedPillar value
                    : pillars
            ) {

                if (value.pillar().branch()
                        != expectedBranch) {

                    continue;
                }

                result.add(
                        SpecialStarHit.dayStemBranch(
                                type,
                                dayStem,
                                expectedBranch,
                                value.position(),
                                value.pillar()
                        )
                );
            }
        }
    }

    // =========================================================
    // 기둥 간지 자체
    // =========================================================

    private void calculatePillarGanji(
            List<SpecialStarHit> result,
            SpecialStarType type,
            List<PositionedPillar> pillars
    ) {

        if (type
                != SpecialStarType.BAEKHO) {

            throw new IllegalArgumentException(
                    "현재 PILLAR_GANJI 방식은 백호살만 지원합니다. type="
                            + type
            );
        }

        for (
                PositionedPillar value
                : pillars
        ) {

            if (!SpecialStarRule.isBaekho(
                    value.pillar().ganji()
            )) {

                continue;
            }

            result.add(
                    SpecialStarHit.pillarGanji(
                            type,
                            value.pillar().ganji(),
                            value.position(),
                            value.pillar()
                    )
            );
        }
    }

    // =========================================================
    // 기둥 글자 자체
    // =========================================================

    private void calculatePillarCharacter(
            List<SpecialStarHit> result,
            SpecialStarType type,
            List<PositionedPillar> pillars
    ) {

        if (type
                != SpecialStarType.HYEONCHIM) {

            throw new IllegalArgumentException(
                    "현재 PILLAR_CHARACTER 방식은 현침살만 지원합니다. type="
                            + type
            );
        }

        for (
                PositionedPillar value
                : pillars
        ) {

            Pillar pillar =
                    value.pillar();

            if (!SpecialStarRule.isHyeonchim(
                    pillar
            )) {

                continue;
            }

            result.add(
                    SpecialStarHit.pillarCharacter(
                            type,
                            value.position(),
                            pillar,
                            SpecialStarRule.hyeonchimEvidence(
                                    pillar
                            )
                    )
            );
        }
    }

    // =========================================================
    // 월지 → 특정 천간/지지
    // =========================================================

    private void calculateMonthBranchCharacter(
            List<SpecialStarHit> result,
            SpecialStarType type,
            EarthlyBranch monthBranch,
            List<PositionedPillar> pillars
    ) {

        SpecialStarRule.MonthBranchCharacterTarget target =
                SpecialStarRule.monthBranchTarget(
                        type,
                        monthBranch
                );

        for (
                PositionedPillar value
                : pillars
        ) {

            Pillar pillar =
                    value.pillar();

            if (!target.matches(
                    pillar
            )) {

                continue;
            }

            if (target.isStemTarget()) {

                result.add(
                        SpecialStarHit.monthBranchStem(
                                type,
                                monthBranch,
                                target.stem(),
                                value.position(),
                                pillar
                        )
                );

                continue;
            }

            result.add(
                    SpecialStarHit.monthBranchBranch(
                            type,
                            monthBranch,
                            target.branch(),
                            value.position(),
                            pillar
                    )
            );
        }
    }

    private List<PositionedPillar> collectPillars(
            FourPillars fourPillars
    ) {

        List<PositionedPillar> result =
                new ArrayList<>();

        result.add(
                new PositionedPillar(
                        PillarPosition.YEAR,
                        fourPillars.year()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.MONTH,
                        fourPillars.month()
                )
        );

        result.add(
                new PositionedPillar(
                        PillarPosition.DAY,
                        fourPillars.day()
                )
        );

        if (fourPillars.hasKnownHour()) {

            result.add(
                    new PositionedPillar(
                            PillarPosition.HOUR,
                            fourPillars.hour()
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    private record PositionedPillar(
            PillarPosition position,
            Pillar pillar
    ) {
    }
}