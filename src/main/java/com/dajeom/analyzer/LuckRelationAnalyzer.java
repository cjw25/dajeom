package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.constant.StemRelationTable;
import com.dajeom.model.AnnualLuckAnalysis;
import com.dajeom.model.DecadeLuckPeriod;
import com.dajeom.model.FourPillars;
import com.dajeom.model.LuckBranchInteraction;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.LuckStemInteraction;
import com.dajeom.model.Pillar;
import com.dajeom.model.SexagenaryPillar;
import com.dajeom.rule.LuckInteractionRule;
import com.dajeom.type.BranchRelationType;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StemRelationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 / 대운 / 세운 사이의
 * 천간 및 지지 관계를 분석한다.
 *
 * =================================================
 *
 * 역할
 *
 * 원국끼리의 기존 관계는
 * 원국 RelationAnalysis 단계에서 이미 처리한다.
 *
 * 이 Analyzer에서는 적어도 하나의
 * 운 Source가 포함된 관계만 생성한다.
 *
 * =================================================
 *
 * 지원 흐름
 *
 * 13차:
 *
 * 원국
 * +
 * 대운
 *
 * 14차 이후:
 *
 * 원국
 * +
 * 대운
 * +
 * 세운
 *
 * =================================================
 *
 * 현재 13차 / 14차 운 간지는
 * SexagenaryPillar를 사용한다.
 *
 * 반면 LuckPillarSource는
 * 원국과 운을 하나의 관계 분석 소스로 통합하기 위해
 * Pillar를 사용한다.
 *
 * 따라서 이 Analyzer의 Source 생성 경계에서만
 * SexagenaryPillar -> Pillar로 변환한다.
 *
 * DecadeLuckPeriod / AnnualLuckAnalysis의
 * SexagenaryPillar 구조 자체를 되돌리지 않는다.
 *
 * =================================================
 *
 * 중요
 *
 * 이 클래스는:
 *
 * - 관계 Table을 새로 만들지 않는다.
 * - 합화 성립 여부를 판단하지 않는다.
 * - 합 = 길, 충 = 흉 같은 평가를 하지 않는다.
 *
 * 실제 합화는 별도 Transformation 단계의 책임이다.
 */
@Component
public class LuckRelationAnalyzer {

    /*
     * =================================================
     * 13차
     *
     * 원국 × 대운 천간 관계
     * =================================================
     */

    public List<LuckStemInteraction>
    analyzeStemInteractions(

            FourPillars fourPillars,

            DecadeLuckPeriod decadeLuckPeriod

    ) {

        Objects.requireNonNull(
                decadeLuckPeriod,
                "13차 대운 관계 분석에는 DecadeLuckPeriod가 필요합니다."
        );

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        null
                );

        return analyzeStemInteractions(
                sources
        );
    }

    /*
     * =================================================
     * 13차
     *
     * 원국 × 대운 지지 관계
     * =================================================
     */

    public List<LuckBranchInteraction>
    analyzeBranchInteractions(

            FourPillars fourPillars,

            DecadeLuckPeriod decadeLuckPeriod

    ) {

        Objects.requireNonNull(
                decadeLuckPeriod,
                "13차 대운 관계 분석에는 DecadeLuckPeriod가 필요합니다."
        );

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        null
                );

        return analyzeBranchInteractions(
                sources
        );
    }

    /*
     * =================================================
     * 기존 통합 API
     *
     * 원국 × 대운 × 세운
     * =================================================
     */

    public List<LuckStemInteraction>
    analyzeStemInteractions(

            FourPillars fourPillars,

            DecadeLuckPeriod decadeLuckPeriod,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "통합 운 관계 분석에는 세운 분석 결과가 필요합니다."
        );

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        annualLuckAnalysis
                );

        return analyzeStemInteractions(
                sources
        );
    }

    public List<LuckBranchInteraction>
    analyzeBranchInteractions(

            FourPillars fourPillars,

            DecadeLuckPeriod decadeLuckPeriod,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "통합 운 관계 분석에는 세운 분석 결과가 필요합니다."
        );

        List<LuckPillarSource> sources =
                createSources(
                        fourPillars,
                        decadeLuckPeriod,
                        annualLuckAnalysis
                );

        return analyzeBranchInteractions(
                sources
        );
    }

    /*
     * =================================================
     * 천간 공통 분석
     * =================================================
     */

    private List<LuckStemInteraction>
    analyzeStemInteractions(
            List<LuckPillarSource> sources
    ) {

        Objects.requireNonNull(
                sources,
                "운 관계 Source 목록이 필요합니다."
        );

        List<LuckStemInteraction> result =
                new ArrayList<>();

        for (
                int firstIndex = 0;
                firstIndex < sources.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size();
                    secondIndex++
            ) {

                LuckPillarSource first =
                        sources.get(
                                firstIndex
                        );

                LuckPillarSource second =
                        sources.get(
                                secondIndex
                        );

                /*
                 * 원국 × 원국 관계는
                 * 기존 8차 RelationAnalysis 책임.
                 *
                 * 적어도 하나의 운 Source가
                 * 포함된 Pair만 처리한다.
                 */
                if (
                        !LuckInteractionRule
                                .isRelevantPair(
                                        first,
                                        second
                                )
                ) {

                    continue;
                }

                /*
                 * =================================================
                 * 천간 오합
                 * =================================================
                 *
                 * 합 관계 존재 여부와
                 * 전통적 목표 오행 후보만 가져온다.
                 *
                 * 실제 합화 여부는 여기서 판단하지 않는다.
                 */

                Optional<FiveElement>
                        combinationElement =
                        StemRelationTable
                                .findCombinationElement(
                                        first
                                                .pillar()
                                                .stem(),
                                        second
                                                .pillar()
                                                .stem()
                                );

                combinationElement.ifPresent(
                        element ->
                                result.add(
                                        new LuckStemInteraction(
                                                StemRelationType
                                                        .COMBINATION,
                                                first,
                                                second,
                                                element
                                        )
                                )
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * 지지 공통 분석
     * =================================================
     */

    private List<LuckBranchInteraction>
    analyzeBranchInteractions(
            List<LuckPillarSource> sources
    ) {

        Objects.requireNonNull(
                sources,
                "운 관계 Source 목록이 필요합니다."
        );

        List<LuckBranchInteraction> result =
                new ArrayList<>();

        /*
         * 2 Source 관계
         */
        analyzePairs(
                sources,
                result
        );

        /*
         * 3 Source 관계
         *
         * 원국 지지 2개
         * +
         * 대운 또는 세운 1개
         *
         * 조합도 보존한다.
         */
        analyzeTriples(
                sources,
                result
        );

        return List.copyOf(
                result
        );
    }

    /*
     * =================================================
     * Pair
     * =================================================
     */

    private void analyzePairs(

            List<LuckPillarSource> sources,

            List<LuckBranchInteraction> result

    ) {

        for (
                int firstIndex = 0;
                firstIndex < sources.size();
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size();
                    secondIndex++
            ) {

                LuckPillarSource first =
                        sources.get(
                                firstIndex
                        );

                LuckPillarSource second =
                        sources.get(
                                secondIndex
                        );

                if (
                        !LuckInteractionRule
                                .isRelevantPair(
                                        first,
                                        second
                                )
                ) {

                    continue;
                }

                EarthlyBranch firstBranch =
                        first
                                .pillar()
                                .branch();

                EarthlyBranch secondBranch =
                        second
                                .pillar()
                                .branch();

                /*
                 * =================================================
                 * 육합
                 * =================================================
                 */

                Optional<FiveElement>
                        sixCombination =
                        BranchRelationTable
                                .findSixCombinationElement(
                                        firstBranch,
                                        secondBranch
                                );

                sixCombination.ifPresent(
                        element ->
                                result.add(
                                        createPair(
                                                BranchRelationType
                                                        .SIX_COMBINATION,
                                                first,
                                                second,
                                                element
                                        )
                                )
                );

                /*
                 * =================================================
                 * 충
                 * =================================================
                 */

                if (
                        BranchRelationTable
                                .isClash(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.CLASH,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                /*
                 * =================================================
                 * Pair 형
                 * =================================================
                 */

                if (
                        BranchRelationTable
                                .isPairPunishment(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.PUNISHMENT,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                /*
                 * =================================================
                 * 자형
                 * =================================================
                 */

                if (
                        firstBranch == secondBranch

                                && BranchRelationTable
                                .isSelfPunishmentBranch(
                                        firstBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType
                                            .SELF_PUNISHMENT,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                /*
                 * =================================================
                 * 해
                 * =================================================
                 */

                if (
                        BranchRelationTable
                                .isHarm(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.HARM,
                                    first,
                                    second,
                                    null
                            )
                    );
                }

                /*
                 * =================================================
                 * 파
                 * =================================================
                 */

                if (
                        BranchRelationTable
                                .isBreak(
                                        firstBranch,
                                        secondBranch
                                )
                ) {

                    result.add(
                            createPair(
                                    BranchRelationType.BREAK,
                                    first,
                                    second,
                                    null
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * Triple
     * =================================================
     */

    private void analyzeTriples(

            List<LuckPillarSource> sources,

            List<LuckBranchInteraction> result

    ) {

        if (
                sources.size() < 3
        ) {

            return;
        }

        for (
                int firstIndex = 0;
                firstIndex < sources.size() - 2;
                firstIndex++
        ) {

            for (
                    int secondIndex =
                    firstIndex + 1;
                    secondIndex < sources.size() - 1;
                    secondIndex++
            ) {

                for (
                        int thirdIndex =
                        secondIndex + 1;
                        thirdIndex < sources.size();
                        thirdIndex++
                ) {

                    LuckPillarSource first =
                            sources.get(
                                    firstIndex
                            );

                    LuckPillarSource second =
                            sources.get(
                                    secondIndex
                            );

                    LuckPillarSource third =
                            sources.get(
                                    thirdIndex
                            );

                    List<LuckPillarSource> group =
                            List.of(
                                    first,
                                    second,
                                    third
                            );

                    /*
                     * 세 Source가 전부 원국이면
                     * 기존 8차에서 이미 분석한다.
                     *
                     * 최소 하나의 운 Source가
                     * 포함된 그룹만 처리한다.
                     */
                    if (
                            !LuckInteractionRule
                                    .isRelevantGroup(
                                            group
                                    )
                    ) {

                        continue;
                    }

                    EarthlyBranch firstBranch =
                            first
                                    .pillar()
                                    .branch();

                    EarthlyBranch secondBranch =
                            second
                                    .pillar()
                                    .branch();

                    EarthlyBranch thirdBranch =
                            third
                                    .pillar()
                                    .branch();

                    /*
                     * =================================================
                     * 삼합
                     * =================================================
                     */

                    Optional<FiveElement>
                            threeHarmony =
                            BranchRelationTable
                                    .findThreeHarmonyElement(
                                            firstBranch,
                                            secondBranch,
                                            thirdBranch
                                    );

                    threeHarmony.ifPresent(
                            element ->
                                    result.add(
                                            new LuckBranchInteraction(
                                                    BranchRelationType
                                                            .THREE_HARMONY,
                                                    group,
                                                    element
                                            )
                                    )
                    );

                    /*
                     * =================================================
                     * 완성 삼형
                     * =================================================
                     */

                    if (
                            BranchRelationTable
                                    .isThreePunishment(
                                            firstBranch,
                                            secondBranch,
                                            thirdBranch
                                    )
                    ) {

                        result.add(
                                new LuckBranchInteraction(
                                        BranchRelationType
                                                .PUNISHMENT,
                                        group,
                                        null
                                )
                        );
                    }
                }
            }
        }
    }

    private LuckBranchInteraction createPair(

            BranchRelationType type,

            LuckPillarSource first,

            LuckPillarSource second,

            FiveElement combinationElement

    ) {

        return new LuckBranchInteraction(
                type,
                List.of(
                        first,
                        second
                ),
                combinationElement
        );
    }

    /*
     * =================================================
     * Source 생성
     * =================================================
     */

    private List<LuckPillarSource>
    createSources(

            FourPillars fourPillars,

            DecadeLuckPeriod decadeLuckPeriod,

            AnnualLuckAnalysis annualLuckAnalysis

    ) {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        if (
                decadeLuckPeriod == null
                        && annualLuckAnalysis == null
        ) {

            throw new IllegalArgumentException(
                    "운 관계 분석에는 대운 또는 세운 중 하나 이상이 필요합니다."
            );
        }

        List<LuckPillarSource> result =
                new ArrayList<>();

        /*
         * =================================================
         * 원국 YEAR
         * =================================================
         */

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.YEAR,
                        null,
                        fourPillars.yearPillar()
                )
        );

        /*
         * =================================================
         * 원국 MONTH
         * =================================================
         */

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.MONTH,
                        null,
                        fourPillars.monthPillar()
                )
        );

        /*
         * =================================================
         * 원국 DAY
         * =================================================
         */

        result.add(
                new LuckPillarSource(
                        LuckSourceType.NATAL,
                        PillarPosition.DAY,
                        null,
                        fourPillars.dayPillar()
                )
        );

        /*
         * =================================================
         * 원국 HOUR
         * =================================================
         *
         * 출생시간 미상에서는
         * 시주를 임의 생성하지 않는다.
         */

        if (
                fourPillars.hasHourPillar()
        ) {

            result.add(
                    new LuckPillarSource(
                            LuckSourceType.NATAL,
                            PillarPosition.HOUR,
                            null,
                            fourPillars.hourPillar()
                    )
            );
        }

        /*
         * =================================================
         * 대운
         * =================================================
         */

        if (
                decadeLuckPeriod != null
        ) {

            result.add(
                    new LuckPillarSource(
                            LuckSourceType.DECADE_LUCK,
                            null,
                            decadeLuckPeriod
                                    .sequenceNumber(),
                            toPillar(
                                    decadeLuckPeriod
                                            .pillar()
                            )
                    )
            );
        }

        /*
         * =================================================
         * 세운
         * =================================================
         */

        if (
                annualLuckAnalysis != null
        ) {

            result.add(
                    new LuckPillarSource(
                            LuckSourceType.ANNUAL_LUCK,
                            null,
                            annualLuckAnalysis
                                    .targetYear(),
                            toPillar(
                                    annualLuckAnalysis
                                            .annualPillar()
                            )
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 13차 / 14차 운 전용 간지를
     * 기존 LuckPillarSource의 Pillar 형태로
     * 경계에서만 변환한다.
     *
     * cycleIndex / sequenceNumber / targetYear 같은
     * 운의 메타데이터를 Pillar에 넣지 않는다.
     */
    private Pillar toPillar(
            SexagenaryPillar sexagenaryPillar
    ) {

        Objects.requireNonNull(
                sexagenaryPillar,
                "운 간지가 필요합니다."
        );

        return new Pillar(
                sexagenaryPillar.stem(),
                sexagenaryPillar.branch()
        );
    }
}