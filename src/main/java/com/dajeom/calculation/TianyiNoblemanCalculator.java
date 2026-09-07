package com.dajeom.calculation;

import com.dajeom.domain.EarthlyBranch;
import com.dajeom.domain.FourPillars;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.Pillar;
import com.dajeom.domain.PillarPosition;
import com.dajeom.domain.TianyiNobleman;
import com.dajeom.rule.TianyiNoblemanRule;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TianyiNoblemanCalculator {

    public TianyiNobleman calculate(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 네 기둥이 필요합니다."
        );

        HeavenlyStem dayStem =
                fourPillars.dayStem();

        List<EarthlyBranch> noblemanBranches =
                TianyiNoblemanRule.branches(
                        dayStem
                );

        EarthlyBranch first =
                noblemanBranches.get(0);

        EarthlyBranch second =
                noblemanBranches.get(1);

        List<TianyiNobleman.Hit> hits =
                new ArrayList<>();

        /*
         * 년지
         */
        addIfMatched(
                hits,
                PillarPosition.YEAR,
                fourPillars.year(),
                first,
                second
        );

        /*
         * 월지
         */
        addIfMatched(
                hits,
                PillarPosition.MONTH,
                fourPillars.month(),
                first,
                second
        );

        /*
         * 일지
         */
        addIfMatched(
                hits,
                PillarPosition.DAY,
                fourPillars.day(),
                first,
                second
        );

        /*
         * 시지
         *
         * 출생시간 미상일 경우
         * 시주는 존재하지 않으므로 판정하지 않는다.
         */
        if (fourPillars.hasKnownHour()) {

            addIfMatched(
                    hits,
                    PillarPosition.HOUR,
                    fourPillars.hour(),
                    first,
                    second
            );
        }

        return new TianyiNobleman(
                dayStem,
                first,
                second,
                hits
        );
    }

    private void addIfMatched(
            List<TianyiNobleman.Hit> hits,
            PillarPosition position,
            Pillar pillar,
            EarthlyBranch first,
            EarthlyBranch second
    ) {

        Objects.requireNonNull(
                hits,
                "천을귀인 적중 목록이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "기둥 위치가 필요합니다."
        );

        Objects.requireNonNull(
                pillar,
                "기둥이 필요합니다."
        );

        EarthlyBranch branch =
                pillar.branch();

        if (branch != first
                && branch != second) {

            return;
        }

        hits.add(
                new TianyiNobleman.Hit(
                        position,
                        branch
                )
        );
    }
}