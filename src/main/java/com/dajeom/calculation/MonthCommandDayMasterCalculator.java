package com.dajeom.calculation;

import com.dajeom.domain.DayMasterRelationType;
import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.HiddenStemComponent;
import com.dajeom.domain.HiddenStemExposureHit;
import com.dajeom.domain.MonthCommandAnalysis;
import com.dajeom.domain.MonthCommandDayMasterAnalysis;
import com.dajeom.domain.MonthCommandStemRelation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 월령 지장간과 일간의 관계 분석기.
 *
 * 이 클래스가 하는 일:
 *
 * 월령 EARLY / MIDDLE / MAIN
 *            ↓
 * 일간과 오행 생극관계 판정
 *            ↓
 * 해당 지장간의 천간 투출 상태 연결
 *
 *
 * 하지 않는 일:
 *
 * - 득령/실령 판정
 * - 신강/신약 판정
 * - 격국 판정
 * - 점수 계산
 * - 가중치 계산
 */
@Component
public class MonthCommandDayMasterCalculator {

    private final DayMasterRelationCalculator
            dayMasterRelationCalculator;

    public MonthCommandDayMasterCalculator(
            DayMasterRelationCalculator
                    dayMasterRelationCalculator
    ) {

        this.dayMasterRelationCalculator =
                Objects.requireNonNull(
                        dayMasterRelationCalculator,
                        "DayMasterRelationCalculator는 필수입니다."
                );
    }

    public MonthCommandDayMasterAnalysis analyze(

            HeavenlyStem dayMaster,

            MonthCommandAnalysis monthCommand

    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                monthCommand,
                "월령 분석 결과는 필수입니다."
        );

        List<MonthCommandStemRelation> relations =
                new ArrayList<>();

        for (
                HiddenStemComponent hidden
                : monthCommand.hiddenStems()
        ) {

            Objects.requireNonNull(
                    hidden,
                    "월령 지장간 목록에 null이 포함될 수 없습니다."
            );

            HeavenlyStem hiddenStem =
                    hidden.stem();

            DayMasterRelationType relationType =
                    dayMasterRelationCalculator.relationOf(
                            dayMaster,
                            hiddenStem
                    );

            List<HiddenStemExposureHit> exposureHits =
                    monthCommand.exposureHits()
                            .stream()
                            .filter(
                                    hit ->
                                            hit.hiddenStem()
                                                    == hiddenStem
                            )
                            .filter(
                                    hit ->
                                            hit.hiddenStemPhase()
                                                    == hidden.phase()
                            )
                            .toList();

            relations.add(
                    new MonthCommandStemRelation(
                            dayMaster,
                            hiddenStem,
                            hidden.phase(),
                            relationType,
                            exposureHits
                    )
            );
        }

        return new MonthCommandDayMasterAnalysis(
                dayMaster,
                monthCommand.monthBranch(),
                relations
        );
    }
}