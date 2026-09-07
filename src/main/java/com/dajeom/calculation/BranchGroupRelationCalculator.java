package com.dajeom.calculation;

import com.dajeom.domain.BranchGroupMember;
import com.dajeom.domain.BranchGroupRelation;
import com.dajeom.domain.BranchGroupRelationType;
import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FiveElement;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class BranchGroupRelationCalculator {

    public List<BranchGroupRelation> calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        List<PositionedBranch> branches =
                collectBranches(
                        fourPillars
                );

        List<BranchGroupRelation> result =
                new ArrayList<>();

        /*
         * =====================================================
         * 완전 삼합
         * =====================================================
         *
         * 申 子 辰 → 水
         * 亥 卯 未 → 木
         * 寅 午 戌 → 火
         * 巳 酉 丑 → 金
         */

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.THREE_HARMONY,
                FiveElement.WATER,
                EarthlyBranch.SHEN,
                EarthlyBranch.ZI,
                EarthlyBranch.CHEN
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.THREE_HARMONY,
                FiveElement.WOOD,
                EarthlyBranch.HAI,
                EarthlyBranch.MAO,
                EarthlyBranch.WEI
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.THREE_HARMONY,
                FiveElement.FIRE,
                EarthlyBranch.YIN,
                EarthlyBranch.WU,
                EarthlyBranch.XU
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.THREE_HARMONY,
                FiveElement.METAL,
                EarthlyBranch.SI,
                EarthlyBranch.YOU,
                EarthlyBranch.CHOU
        );

        /*
         * =====================================================
         * 완전 방합
         * =====================================================
         *
         * 寅 卯 辰 → 木
         * 巳 午 未 → 火
         * 申 酉 戌 → 金
         * 亥 子 丑 → 水
         */

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.DIRECTIONAL_HARMONY,
                FiveElement.WOOD,
                EarthlyBranch.YIN,
                EarthlyBranch.MAO,
                EarthlyBranch.CHEN
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.DIRECTIONAL_HARMONY,
                FiveElement.FIRE,
                EarthlyBranch.SI,
                EarthlyBranch.WU,
                EarthlyBranch.WEI
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.DIRECTIONAL_HARMONY,
                FiveElement.METAL,
                EarthlyBranch.SHEN,
                EarthlyBranch.YOU,
                EarthlyBranch.XU
        );

        addFullGroupIfPresent(
                result,
                branches,
                BranchGroupRelationType.DIRECTIONAL_HARMONY,
                FiveElement.WATER,
                EarthlyBranch.HAI,
                EarthlyBranch.ZI,
                EarthlyBranch.CHOU
        );

        return List.copyOf(
                result
        );
    }

    private void addFullGroupIfPresent(
            List<BranchGroupRelation> result,
            List<PositionedBranch> branches,
            BranchGroupRelationType type,
            FiveElement resultElement,
            EarthlyBranch first,
            EarthlyBranch second,
            EarthlyBranch third
    ) {

        Optional<PositionedBranch> firstFound =
                findFirst(
                        branches,
                        first
                );

        Optional<PositionedBranch> secondFound =
                findFirst(
                        branches,
                        second
                );

        Optional<PositionedBranch> thirdFound =
                findFirst(
                        branches,
                        third
                );

        /*
         * 세 글자가 모두 있어야 FULL.
         */
        if (firstFound.isEmpty()
                || secondFound.isEmpty()
                || thirdFound.isEmpty()) {

            return;
        }

        BranchGroupRelation relation =
                new BranchGroupRelation(
                        type,
                        "FULL",
                        resultElement,
                        List.of(
                                toMember(
                                        firstFound.get()
                                ),
                                toMember(
                                        secondFound.get()
                                ),
                                toMember(
                                        thirdFound.get()
                                )
                        )
                );

        result.add(
                relation
        );
    }

    private Optional<PositionedBranch> findFirst(
            List<PositionedBranch> branches,
            EarthlyBranch target
    ) {

        return branches
                .stream()
                .filter(value ->
                        value.branch() == target
                )
                .findFirst();
    }

    private BranchGroupMember toMember(
            PositionedBranch value
    ) {

        return new BranchGroupMember(
                value.position(),
                value.branch()
        );
    }

    private List<PositionedBranch> collectBranches(
            FourPillars fourPillars
    ) {

        List<PositionedBranch> result =
                new ArrayList<>();

        add(
                result,
                PillarPosition.YEAR,
                fourPillars.year()
        );

        add(
                result,
                PillarPosition.MONTH,
                fourPillars.month()
        );

        add(
                result,
                PillarPosition.DAY,
                fourPillars.day()
        );

        if (fourPillars.hasKnownHour()) {

            add(
                    result,
                    PillarPosition.HOUR,
                    fourPillars.hour()
            );
        }

        return List.copyOf(
                result
        );
    }

    private void add(
            List<PositionedBranch> result,
            PillarPosition position,
            Pillar pillar
    ) {

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        result.add(
                new PositionedBranch(
                        position,
                        pillar.branch()
                )
        );
    }

    private record PositionedBranch(
            PillarPosition position,
            EarthlyBranch branch
    ) {
    }
}