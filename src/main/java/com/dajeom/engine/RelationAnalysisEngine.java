package com.dajeom.engine;

import com.dajeom.analyzer.EarthlyBranchBreakAnalyzer;
import com.dajeom.analyzer.EarthlyBranchClashAnalyzer;
import com.dajeom.analyzer.EarthlyBranchDirectionalCombinationAnalyzer;
import com.dajeom.analyzer.EarthlyBranchHarmAnalyzer;
import com.dajeom.analyzer.EarthlyBranchPunishmentAnalyzer;
import com.dajeom.analyzer.EarthlyBranchSixCombinationAnalyzer;
import com.dajeom.analyzer.EarthlyBranchThreeCombinationAnalyzer;
import com.dajeom.analyzer.HeavenlyStemCombinationAnalyzer;
import com.dajeom.analyzer.HeavenlyStemControlAnalyzer;
import com.dajeom.model.EarthlyBranchBreakAnalysis;
import com.dajeom.model.EarthlyBranchClashAnalysis;
import com.dajeom.model.EarthlyBranchDirectionalCombinationAnalysis;
import com.dajeom.model.EarthlyBranchHarmAnalysis;
import com.dajeom.model.EarthlyBranchPairRelation;
import com.dajeom.model.EarthlyBranchPunishmentAnalysis;
import com.dajeom.model.EarthlyBranchSixCombinationAnalysis;
import com.dajeom.model.EarthlyBranchThreeCombinationAnalysis;
import com.dajeom.model.FourPillars;
import com.dajeom.model.HeavenlyStemCombinationAnalysis;
import com.dajeom.model.HeavenlyStemControlAnalysis;
import com.dajeom.model.HeavenlyStemControlEvidence;
import com.dajeom.model.HeavenlyStemPairRelation;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.EarthlyBranchRelationKind;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HeavenlyStemRelationKind;
import com.dajeom.type.VisibleStemPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 8차-10.
 *
 * 천간·지지 관계 분석 전체 Engine.
 *
 * =================================================
 *
 * 입력
 *
 * FourPillars
 *
 * =================================================
 *
 * 실행
 *
 * 8-1 천간 합
 * 8-2 천간 충·극
 * 8-3 지지 육합
 * 8-4 지지 삼합
 * 8-5 지지 방합
 * 8-6 지지 충
 * 8-7 지지 형
 * 8-8 지지 파
 * 8-9 지지 해
 *
 * ↓
 *
 * 천간 궁위 Pair 관계망
 *
 * ↓
 *
 * 지지 궁위 Pair 관계망
 *
 * ↓
 *
 * RelationAnalysis
 *
 * =================================================
 *
 * 이 Engine에서는:
 *
 * - 실제 합화
 * - 합충 우선순위
 * - 오행 치환
 * - 관계 점수 합산
 * - 길흉 판단
 *
 * 을 하지 않는다.
 *
 * 8차의 목적은 관계 Evidence를
 * 손실 없이 통합하는 것이다.
 */
@Component
public class RelationAnalysisEngine {

    private final HeavenlyStemCombinationAnalyzer
            heavenlyStemCombinationAnalyzer;

    private final HeavenlyStemControlAnalyzer
            heavenlyStemControlAnalyzer;

    private final EarthlyBranchSixCombinationAnalyzer
            earthlyBranchSixCombinationAnalyzer;

    private final EarthlyBranchThreeCombinationAnalyzer
            earthlyBranchThreeCombinationAnalyzer;

    private final EarthlyBranchDirectionalCombinationAnalyzer
            earthlyBranchDirectionalCombinationAnalyzer;

    private final EarthlyBranchClashAnalyzer
            earthlyBranchClashAnalyzer;

    private final EarthlyBranchPunishmentAnalyzer
            earthlyBranchPunishmentAnalyzer;

    private final EarthlyBranchBreakAnalyzer
            earthlyBranchBreakAnalyzer;

    private final EarthlyBranchHarmAnalyzer
            earthlyBranchHarmAnalyzer;

    public RelationAnalysisEngine(

            HeavenlyStemCombinationAnalyzer
                    heavenlyStemCombinationAnalyzer,

            HeavenlyStemControlAnalyzer
                    heavenlyStemControlAnalyzer,

            EarthlyBranchSixCombinationAnalyzer
                    earthlyBranchSixCombinationAnalyzer,

            EarthlyBranchThreeCombinationAnalyzer
                    earthlyBranchThreeCombinationAnalyzer,

            EarthlyBranchDirectionalCombinationAnalyzer
                    earthlyBranchDirectionalCombinationAnalyzer,

            EarthlyBranchClashAnalyzer
                    earthlyBranchClashAnalyzer,

            EarthlyBranchPunishmentAnalyzer
                    earthlyBranchPunishmentAnalyzer,

            EarthlyBranchBreakAnalyzer
                    earthlyBranchBreakAnalyzer,

            EarthlyBranchHarmAnalyzer
                    earthlyBranchHarmAnalyzer

    ) {

        this.heavenlyStemCombinationAnalyzer =
                Objects.requireNonNull(
                        heavenlyStemCombinationAnalyzer,
                        "HeavenlyStemCombinationAnalyzer가 필요합니다."
                );

        this.heavenlyStemControlAnalyzer =
                Objects.requireNonNull(
                        heavenlyStemControlAnalyzer,
                        "HeavenlyStemControlAnalyzer가 필요합니다."
                );

        this.earthlyBranchSixCombinationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchSixCombinationAnalyzer,
                        "EarthlyBranchSixCombinationAnalyzer가 필요합니다."
                );

        this.earthlyBranchThreeCombinationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchThreeCombinationAnalyzer,
                        "EarthlyBranchThreeCombinationAnalyzer가 필요합니다."
                );

        this.earthlyBranchDirectionalCombinationAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchDirectionalCombinationAnalyzer,
                        "EarthlyBranchDirectionalCombinationAnalyzer가 필요합니다."
                );

        this.earthlyBranchClashAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchClashAnalyzer,
                        "EarthlyBranchClashAnalyzer가 필요합니다."
                );

        this.earthlyBranchPunishmentAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchPunishmentAnalyzer,
                        "EarthlyBranchPunishmentAnalyzer가 필요합니다."
                );

        this.earthlyBranchBreakAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchBreakAnalyzer,
                        "EarthlyBranchBreakAnalyzer가 필요합니다."
                );

        this.earthlyBranchHarmAnalyzer =
                Objects.requireNonNull(
                        earthlyBranchHarmAnalyzer,
                        "EarthlyBranchHarmAnalyzer가 필요합니다."
                );
    }

    /**
     * 정밀화 8차의 정식 진입점.
     */
    public RelationAnalysis analyze(
            FourPillars fourPillars
    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국이 필요합니다."
        );

        /*
         * =================================================
         * 8-1
         * =================================================
         */
        HeavenlyStemCombinationAnalysis
                heavenlyStemCombinationAnalysis =
                heavenlyStemCombinationAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-2
         * =================================================
         */
        HeavenlyStemControlAnalysis
                heavenlyStemControlAnalysis =
                heavenlyStemControlAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-3
         * =================================================
         */
        EarthlyBranchSixCombinationAnalysis
                sixCombinationAnalysis =
                earthlyBranchSixCombinationAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-4
         * =================================================
         */
        EarthlyBranchThreeCombinationAnalysis
                threeCombinationAnalysis =
                earthlyBranchThreeCombinationAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-5
         * =================================================
         */
        EarthlyBranchDirectionalCombinationAnalysis
                directionalCombinationAnalysis =
                earthlyBranchDirectionalCombinationAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-6
         * =================================================
         */
        EarthlyBranchClashAnalysis
                clashAnalysis =
                earthlyBranchClashAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-7
         * =================================================
         */
        EarthlyBranchPunishmentAnalysis
                punishmentAnalysis =
                earthlyBranchPunishmentAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-8
         * =================================================
         */
        EarthlyBranchBreakAnalysis
                breakAnalysis =
                earthlyBranchBreakAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 8-9
         * =================================================
         */
        EarthlyBranchHarmAnalysis
                harmAnalysis =
                earthlyBranchHarmAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 천간 궁위 Pair 관계망
         * =================================================
         */
        List<HeavenlyStemPairRelation>
                heavenlyStemPairRelations =
                buildHeavenlyStemPairRelations(
                        fourPillars,
                        heavenlyStemCombinationAnalysis,
                        heavenlyStemControlAnalysis
                );

        /*
         * =================================================
         * 지지 궁위 Pair 관계망
         * =================================================
         */
        List<EarthlyBranchPairRelation>
                earthlyBranchPairRelations =
                buildEarthlyBranchPairRelations(
                        fourPillars,
                        sixCombinationAnalysis,
                        threeCombinationAnalysis,
                        directionalCombinationAnalysis,
                        clashAnalysis,
                        punishmentAnalysis,
                        breakAnalysis,
                        harmAnalysis
                );

        boolean completeChart =
                fourPillars.hourPillar() != null;

        return new RelationAnalysis(
                fourPillars
                        .dayPillar()
                        .heavenlyStem(),

                fourPillars
                        .monthPillar()
                        .earthlyBranch(),

                fourPillars
                        .dayPillar()
                        .earthlyBranch(),

                heavenlyStemCombinationAnalysis,
                heavenlyStemControlAnalysis,
                sixCombinationAnalysis,
                threeCombinationAnalysis,
                directionalCombinationAnalysis,
                clashAnalysis,
                punishmentAnalysis,
                breakAnalysis,
                harmAnalysis,
                heavenlyStemPairRelations,
                earthlyBranchPairRelations,
                completeChart
        );
    }

    /*
     * =================================================
     * 천간 Pair 관계망
     * =================================================
     */

    private List<HeavenlyStemPairRelation>
    buildHeavenlyStemPairRelations(

            FourPillars fourPillars,

            HeavenlyStemCombinationAnalysis
                    combinationAnalysis,

            HeavenlyStemControlAnalysis
                    controlAnalysis

    ) {

        List<VisibleStemPosition> positions =
                knownStemPositions(
                        fourPillars
                );

        List<HeavenlyStemPairRelation> results =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < positions.size();
                firstIndex++
        ) {

            VisibleStemPosition firstPosition =
                    positions.get(
                            firstIndex
                    );

            for (
                    int secondIndex =
                    firstIndex + 1;

                    secondIndex < positions.size();

                    secondIndex++
            ) {

                VisibleStemPosition secondPosition =
                        positions.get(
                                secondIndex
                        );

                EnumSet<HeavenlyStemRelationKind> kinds =
                        EnumSet.noneOf(
                                HeavenlyStemRelationKind.class
                        );

                /*
                 * 8-1 천간합
                 */
                if (
                        combinationAnalysis
                                .combinationBetween(
                                        firstPosition,
                                        secondPosition
                                )
                                .isPresent()
                ) {

                    kinds.add(
                            HeavenlyStemRelationKind
                                    .COMBINATION
                    );
                }

                /*
                 * 8-2 천간 충·극
                 */
                Optional<HeavenlyStemControlEvidence>
                        controlEvidence =
                        controlAnalysis
                                .relationBetween(
                                        firstPosition,
                                        secondPosition
                                );

                if (
                        controlEvidence.isPresent()
                ) {

                    if (
                            controlEvidence
                                    .get()
                                    .clash()
                    ) {

                        kinds.add(
                                HeavenlyStemRelationKind
                                        .CLASH
                        );

                    } else {

                        kinds.add(
                                HeavenlyStemRelationKind
                                        .CONTROL_ONLY
                        );
                    }
                }

                if (
                        kinds.isEmpty()
                ) {

                    continue;
                }

                results.add(
                        new HeavenlyStemPairRelation(
                                firstPosition,
                                heavenlyStemAt(
                                        fourPillars,
                                        firstPosition
                                ),
                                secondPosition,
                                heavenlyStemAt(
                                        fourPillars,
                                        secondPosition
                                ),
                                kinds
                        )
                );
            }
        }

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 지지 Pair 관계망
     * =================================================
     */

    private List<EarthlyBranchPairRelation>
    buildEarthlyBranchPairRelations(

            FourPillars fourPillars,

            EarthlyBranchSixCombinationAnalysis
                    sixCombinationAnalysis,

            EarthlyBranchThreeCombinationAnalysis
                    threeCombinationAnalysis,

            EarthlyBranchDirectionalCombinationAnalysis
                    directionalCombinationAnalysis,

            EarthlyBranchClashAnalysis
                    clashAnalysis,

            EarthlyBranchPunishmentAnalysis
                    punishmentAnalysis,

            EarthlyBranchBreakAnalysis
                    breakAnalysis,

            EarthlyBranchHarmAnalysis
                    harmAnalysis

    ) {

        List<EarthlyBranchPosition> positions =
                knownBranchPositions(
                        fourPillars
                );

        List<EarthlyBranchPairRelation> results =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < positions.size();
                firstIndex++
        ) {

            EarthlyBranchPosition firstPosition =
                    positions.get(
                            firstIndex
                    );

            for (
                    int secondIndex =
                    firstIndex + 1;

                    secondIndex < positions.size();

                    secondIndex++
            ) {

                EarthlyBranchPosition secondPosition =
                        positions.get(
                                secondIndex
                        );

                EnumSet<EarthlyBranchRelationKind> kinds =
                        EnumSet.noneOf(
                                EarthlyBranchRelationKind.class
                        );

                /*
                 * =================================================
                 * 8-3 六合
                 * =================================================
                 */
                if (
                        sixCombinationAnalysis
                                .combinationBetween(
                                        firstPosition,
                                        secondPosition
                                )
                                .isPresent()
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .SIX_COMBINATION
                    );
                }

                /*
                 * =================================================
                 * 8-4 三合계
                 * =================================================
                 *
                 * 완전 삼합 Evidence가 세 궁위를
                 * 포함하는 경우에도 각 관련 Pair에는
                 * THREE_COMBINATION 관계가 존재한다고
                 * 관계망에 표시한다.
                 *
                 * 완전/반합/부분조합 세부 형태는
                 * 원본 Analysis에서 확인한다.
                 */
                if (
                        threeCombinationAnalysis
                                .relationsInvolving(
                                        firstPosition
                                )
                                .stream()
                                .anyMatch(
                                        evidence ->
                                                evidence.involves(
                                                        secondPosition
                                                )
                                )
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .THREE_COMBINATION
                    );
                }

                /*
                 * =================================================
                 * 8-5 方合계
                 * =================================================
                 */
                if (
                        directionalCombinationAnalysis
                                .relationsInvolving(
                                        firstPosition
                                )
                                .stream()
                                .anyMatch(
                                        evidence ->
                                                evidence.involves(
                                                        secondPosition
                                                )
                                )
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .DIRECTIONAL_COMBINATION
                    );
                }

                /*
                 * =================================================
                 * 8-6 沖
                 * =================================================
                 */
                if (
                        clashAnalysis
                                .clashBetween(
                                        firstPosition,
                                        secondPosition
                                )
                                .isPresent()
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .CLASH
                    );
                }

                /*
                 * =================================================
                 * 8-7 刑
                 * =================================================
                 *
                 * 완전 삼형도 세 궁위 전체의 관계이므로
                 * 그 Evidence에 두 위치가 모두 포함되면
                 * Pair 관계망에 PUNISHMENT를 표시한다.
                 */
                if (
                        punishmentAnalysis
                                .punishmentsInvolving(
                                        firstPosition
                                )
                                .stream()
                                .anyMatch(
                                        evidence ->
                                                evidence.involves(
                                                        secondPosition
                                                )
                                )
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .PUNISHMENT
                    );
                }

                /*
                 * =================================================
                 * 8-8 破
                 * =================================================
                 */
                if (
                        breakAnalysis
                                .breakBetween(
                                        firstPosition,
                                        secondPosition
                                )
                                .isPresent()
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .BREAK
                    );
                }

                /*
                 * =================================================
                 * 8-9 害
                 * =================================================
                 */
                if (
                        harmAnalysis
                                .harmBetween(
                                        firstPosition,
                                        secondPosition
                                )
                                .isPresent()
                ) {

                    kinds.add(
                            EarthlyBranchRelationKind
                                    .HARM
                    );
                }

                if (
                        kinds.isEmpty()
                ) {

                    continue;
                }

                results.add(
                        new EarthlyBranchPairRelation(
                                firstPosition,
                                earthlyBranchAt(
                                        fourPillars,
                                        firstPosition
                                ),
                                secondPosition,
                                earthlyBranchAt(
                                        fourPillars,
                                        secondPosition
                                ),
                                kinds
                        )
                );
            }
        }

        return List.copyOf(
                results
        );
    }

    /*
     * =================================================
     * 알려진 천간 위치
     * =================================================
     */

    private List<VisibleStemPosition>
    knownStemPositions(
            FourPillars fourPillars
    ) {

        List<VisibleStemPosition> positions =
                new ArrayList<>();

        positions.add(
                VisibleStemPosition.YEAR
        );

        positions.add(
                VisibleStemPosition.MONTH
        );

        positions.add(
                VisibleStemPosition.DAY
        );

        if (
                fourPillars.hourPillar() != null
        ) {

            positions.add(
                    VisibleStemPosition.HOUR
            );
        }

        return List.copyOf(
                positions
        );
    }

    /*
     * =================================================
     * 알려진 지지 위치
     * =================================================
     */

    private List<EarthlyBranchPosition>
    knownBranchPositions(
            FourPillars fourPillars
    ) {

        List<EarthlyBranchPosition> positions =
                new ArrayList<>();

        positions.add(
                EarthlyBranchPosition.YEAR
        );

        positions.add(
                EarthlyBranchPosition.MONTH
        );

        positions.add(
                EarthlyBranchPosition.DAY
        );

        if (
                fourPillars.hourPillar() != null
        ) {

            positions.add(
                    EarthlyBranchPosition.HOUR
            );
        }

        return List.copyOf(
                positions
        );
    }

    /*
     * =================================================
     * 궁위 -> 천간
     * =================================================
     */

    private HeavenlyStem heavenlyStemAt(

            FourPillars fourPillars,

            VisibleStemPosition position

    ) {

        return switch (
                position
                ) {

            case YEAR ->
                    fourPillars
                            .yearPillar()
                            .heavenlyStem();

            case MONTH ->
                    fourPillars
                            .monthPillar()
                            .heavenlyStem();

            case DAY ->
                    fourPillars
                            .dayPillar()
                            .heavenlyStem();

            case HOUR -> {

                if (
                        fourPillars.hourPillar()
                                == null
                ) {

                    throw new IllegalStateException(
                            "출생시간 미상 원국에서 HOUR 천간을 조회할 수 없습니다."
                    );
                }

                yield fourPillars
                        .hourPillar()
                        .heavenlyStem();
            }
        };
    }

    /*
     * =================================================
     * 궁위 -> 지지
     * =================================================
     */

    private EarthlyBranch earthlyBranchAt(

            FourPillars fourPillars,

            EarthlyBranchPosition position

    ) {

        return switch (
                position
                ) {

            case YEAR ->
                    fourPillars
                            .yearPillar()
                            .earthlyBranch();

            case MONTH ->
                    fourPillars
                            .monthPillar()
                            .earthlyBranch();

            case DAY ->
                    fourPillars
                            .dayPillar()
                            .earthlyBranch();

            case HOUR -> {

                if (
                        fourPillars.hourPillar()
                                == null
                ) {

                    throw new IllegalStateException(
                            "출생시간 미상 원국에서 HOUR 지지를 조회할 수 없습니다."
                    );
                }

                yield fourPillars
                        .hourPillar()
                        .earthlyBranch();
            }
        };
    }
}