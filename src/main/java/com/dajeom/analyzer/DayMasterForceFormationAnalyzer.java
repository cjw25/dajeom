package com.dajeom.analyzer;

import com.dajeom.model.DayMasterElementRelation;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DayMasterForceEvidence;
import com.dajeom.model.DayMasterForceFormationAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.HiddenElementPowerEvidence;
import com.dajeom.model.StemExposureAnalysis;
import com.dajeom.model.VisibleStemPower;
import com.dajeom.rule.DayMasterForceFormationRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterForceFormationLevel;
import com.dajeom.type.DayMasterForcePosition;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StemExposurePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 4차-4.
 *
 * 득세 구조를 분석한다.
 *
 * 여기서는 실제 지원 세력의 총량을
 * 최종 계산하지 않는다.
 *
 * 확인하는 것은:
 *
 * - 천간의 비겁·인성 분포
 * - 지장간의 비겁·인성 분포
 * - 어느 위치에 존재하는가
 * - 정기로 자리 잡았는가
 * - 지장간이 투출까지 했는가
 *
 * 이다.
 *
 * 실제 인성/비겁 세력량은
 * 4-5에서 계산한다.
 */
@Component
public class DayMasterForceFormationAnalyzer {

    private final DayMasterElementRelationAnalyzer
            dayMasterElementRelationAnalyzer;

    private final DayMasterForceFormationRule
            dayMasterForceFormationRule;

    public DayMasterForceFormationAnalyzer(

            DayMasterElementRelationAnalyzer
                    dayMasterElementRelationAnalyzer,

            DayMasterForceFormationRule
                    dayMasterForceFormationRule

    ) {

        this.dayMasterElementRelationAnalyzer =
                Objects.requireNonNull(
                        dayMasterElementRelationAnalyzer,
                        "DayMasterElementRelationAnalyzer가 필요합니다."
                );

        this.dayMasterForceFormationRule =
                Objects.requireNonNull(
                        dayMasterForceFormationRule,
                        "DayMasterForceFormationRule이 필요합니다."
                );
    }

    /**
     * 4-1 결과를 내부에서 생성.
     */
    public DayMasterForceFormationAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        DayMasterElementRelationAnalysis relations =
                dayMasterElementRelationAnalyzer
                        .analyze(
                                elementStrength
                        );

        return analyze(
                elementStrength,
                relations
        );
    }

    /**
     * 이미 계산한 4-1 결과를 재사용.
     */
    public DayMasterForceFormationAnalysis analyze(

            ElementStrengthAnalysis elementStrength,

            DayMasterElementRelationAnalysis relations

    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                relations,
                "일간 기준 오행 관계 분석이 필요합니다."
        );

        if (
                elementStrength.dayMaster()
                        != relations.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "오행 세력 분석과 일간 관계 분석의 일간이 일치하지 않습니다."
            );
        }

        List<DayMasterForceEvidence> evidences =
                new ArrayList<>();

        /*
         * ------------------------------------------------
         * 1. 표면 천간
         * ------------------------------------------------
         *
         * 일간 자신은 제외한다.
         *
         * 년간
         * 월간
         * 시간
         *
         * 만 득세 주변 환경으로 본다.
         */
        for (
                VisibleStemPower visibleStem
                : elementStrength
                .visibleStemPowerAnalysis()
                .visibleStems()
        ) {

            if (
                    visibleStem.dayMaster()
            ) {

                continue;
            }

            DayMasterElementRelation relation =
                    requireRelation(
                            relations,
                            visibleStem.element()
                    );

            DayMasterForcePosition position =
                    DayMasterForcePosition
                            .fromVisibleStemPosition(
                                    visibleStem.position()
                            );

            evidences.add(
                    new DayMasterForceEvidence(
                            position,
                            relation.role(),
                            visibleStem.element(),
                            visibleStem.stem(),
                            true,
                            null,
                            null,
                            visibleStem.baseWeight(),
                            true
                    )
            );
        }

        /*
         * ------------------------------------------------
         * 2. 지장간
         * ------------------------------------------------
         *
         * 3-2에서 이미 생성된
         * HiddenElementPowerEvidence를 그대로 활용한다.
         *
         * 다시 지장간을 계산하지 않는다.
         */
        for (
                HiddenElementPowerEvidence hiddenEvidence
                : elementStrength
                .hiddenElementPowerAnalysis()
                .evidences()
        ) {

            DayMasterElementRelation relation =
                    requireRelation(
                            relations,
                            hiddenEvidence.element()
                    );

            DayMasterForcePosition position =
                    DayMasterForcePosition
                            .fromBranchPosition(
                                    hiddenEvidence
                                            .sourcePosition()
                            );

            boolean exposed =
                    isHiddenStemExposed(
                            elementStrength,
                            hiddenEvidence.hiddenStem()
                    );

            evidences.add(
                    new DayMasterForceEvidence(
                            position,
                            relation.role(),
                            hiddenEvidence.element(),
                            hiddenEvidence.hiddenStem(),
                            false,
                            hiddenEvidence.sourceBranch(),
                            hiddenEvidence.hiddenStemRole(),
                            hiddenEvidence.baseWeight(),
                            exposed
                    )
            );
        }

        int supportivePositionCount =
                countPositions(
                        evidences,
                        true,
                        false
                );

        int opposingPositionCount =
                countPositions(
                        evidences,
                        false,
                        false
                );

        int strongSupportPositionCount =
                countPositions(
                        evidences,
                        true,
                        true
                );

        int supportiveVisibleStemCount =
                (int) evidences
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::visibleStemSource
                        )
                        .filter(
                                DayMasterForceEvidence
                                        ::supportive
                        )
                        .count();

        int opposingVisibleStemCount =
                (int) evidences
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::visibleStemSource
                        )
                        .filter(
                                DayMasterForceEvidence
                                        ::opposing
                        )
                        .count();

        int supportiveHiddenSourceCount =
                (int) evidences
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::hiddenStemSource
                        )
                        .filter(
                                DayMasterForceEvidence
                                        ::supportive
                        )
                        .count();

        int opposingHiddenSourceCount =
                (int) evidences
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::hiddenStemSource
                        )
                        .filter(
                                DayMasterForceEvidence
                                        ::opposing
                        )
                        .count();

        int supportiveExposedHiddenSourceCount =
                (int) evidences
                        .stream()
                        .filter(
                                DayMasterForceEvidence
                                        ::supportive
                        )
                        .filter(
                                DayMasterForceEvidence
                                        ::exposedHiddenStem
                        )
                        .count();

        DayMasterForceFormationLevel level =
                dayMasterForceFormationRule
                        .determineLevel(
                                supportivePositionCount,
                                strongSupportPositionCount,
                                supportiveVisibleStemCount,
                                supportiveHiddenSourceCount
                        );

        /*
         * 시주 미상:
         *
         * 년간
         * 월간
         * 년지
         * 월지
         * 일지
         *
         * = 5개 주변 위치
         *
         * 시주 확정:
         *
         * 위 5개
         * +
         * 시간
         * 시지
         *
         * = 7개
         */
        int knownSurroundingPositionCount =
                elementStrength.completeChart()
                        ? 7
                        : 5;

        return new DayMasterForceFormationAnalysis(
                elementStrength.dayMaster(),
                elementStrength.dayMasterElement(),
                knownSurroundingPositionCount,
                supportivePositionCount,
                opposingPositionCount,
                strongSupportPositionCount,
                supportiveVisibleStemCount,
                opposingVisibleStemCount,
                supportiveHiddenSourceCount,
                opposingHiddenSourceCount,
                supportiveExposedHiddenSourceCount,
                level,
                evidences
        );
    }

    private DayMasterElementRelation requireRelation(

            DayMasterElementRelationAnalysis relations,

            FiveElement element

    ) {

        return relations
                .relationOf(
                        element
                )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "일간 기준 오행 관계를 찾을 수 없습니다: "
                                                + element
                                )
                );
    }

    /**
     * 해당 지장간 천간이
     * 실제 표면 천간으로 투출했는지 확인한다.
     *
     * 2차에서 이미 계산한
     * StemExposureAnalysis를 재사용한다.
     */
    private boolean isHiddenStemExposed(

            ElementStrengthAnalysis elementStrength,

            com.dajeom.type.HeavenlyStem stem

    ) {

        StemExposureAnalysis exposure =
                elementStrength
                        .basePowerAnalysis()
                        .hiddenStemExposureAnalyses()
                        .get(
                                stem
                        );

        return exposure != null
                && exposure.exposed();
    }

    private int countPositions(

            List<DayMasterForceEvidence> evidences,

            boolean supportive,

            boolean strongOnly

    ) {

        EnumSet<DayMasterForcePosition> positions =
                EnumSet.noneOf(
                        DayMasterForcePosition.class
                );

        for (
                DayMasterForceEvidence evidence
                : evidences
        ) {

            if (
                    evidence.supportive()
                            != supportive
            ) {

                continue;
            }

            if (
                    strongOnly
                            && !evidence
                            .structurallyStrongSource()
            ) {

                continue;
            }

            positions.add(
                    evidence.position()
            );
        }

        return positions.size();
    }
}