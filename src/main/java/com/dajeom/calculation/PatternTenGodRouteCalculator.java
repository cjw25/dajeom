package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodLinkType;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternTenGodRouteAnalysis;
import com.dajeom.domain.PatternTenGodRouteOccurrence;
import com.dajeom.domain.PatternTenGodRouteType;
import com.dajeom.domain.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 격국에서 사용하는 주요 십성 route를
 * 동일 PatternTenGodNode 기준으로 계산한다.
 *
 * =================================================
 *
 * 단순:
 *
 * 財 있음
 * 官 있음
 * 印 있음
 *
 * 을 route로 인정하지 않는다.
 *
 * 실제:
 *
 * 財 A → 官 B
 * 官 B → 印 C
 *
 * 처럼 동일 중간 노드가 이어져야 한다.
 *
 * =================================================
 */
@Component
public class PatternTenGodRouteCalculator {

    public PatternTenGodRouteAnalysis analyze(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        Objects.requireNonNull(
                interaction,
                "PatternInteractionAnalysis는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInteractions,
                "PatternTenGodInteractionAnalysis는 필수입니다."
        );

        validateDayMaster(
                interaction,
                tenGodInteractions
        );

        List<PatternTenGodRouteOccurrence> routes =
                new ArrayList<>();

        /*
         * 공통 七殺 → 印 → 身
         */
        addVisibleKillingResourceRoutes(
                tenGodInteractions,
                routes
        );

        Optional<MonthCommandPatternCandidate> candidate =
                interaction
                        .provisionalStandardCandidate();

        if (candidate.isPresent()) {

            PatternTenGodNode selectedMonthNode =
                    findSelectedMonthNode(
                            candidate.get(),
                            tenGodInteractions
                    );

            switch (
                    candidate
                            .get()
                            .candidateType()
            ) {

                case DIRECT_RESOURCE,
                     INDIRECT_RESOURCE ->

                        addResourcePatternRoutes(
                                selectedMonthNode,
                                tenGodInteractions,
                                routes
                        );

                case EATING_GOD ->

                        addEatingGodPatternRoutes(
                                selectedMonthNode,
                                tenGodInteractions,
                                routes
                        );

                case HURTING_OFFICER ->

                        addHurtingOfficerPatternRoutes(
                                selectedMonthNode,
                                tenGodInteractions,
                                routes
                        );

                case SEVEN_KILLINGS ->

                        addSevenKillingsPatternRoutes(
                                selectedMonthNode,
                                tenGodInteractions,
                                routes
                        );

                default -> {
                    /*
                     * 현재 route 자동화 대상 아님.
                     */
                }
            }
        }

        return new PatternTenGodRouteAnalysis(
                interaction.dayMaster(),
                routes
        );
    }

    /*
     * =================================================
     * 印格
     * =================================================
     */

    private void addResourcePatternRoutes(

            PatternTenGodNode selectedResource,

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodRouteOccurrence> routes

    ) {

        PatternTenGodNode dayMaster =
                analysis.dayMasterNode();

        if (
                !generates(
                        selectedResource,
                        dayMaster,
                        analysis
                )
        ) {

            return;
        }

        for (
                PatternTenGodNode officer
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        )
        ) {

            if (
                    generates(
                            officer,
                            selectedResource,
                            analysis
                    )
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .RESOURCE_PATTERN_OFFICER_RESOURCE_DAY_MASTER,

                                List.of(
                                        officer,
                                        selectedResource,
                                        dayMaster
                                )
                        )
                );
            }
        }

        for (
                PatternTenGodNode killing
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
        ) {

            if (
                    generates(
                            killing,
                            selectedResource,
                            analysis
                    )
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .RESOURCE_PATTERN_KILLING_RESOURCE_DAY_MASTER,

                                List.of(
                                        killing,
                                        selectedResource,
                                        dayMaster
                                )
                        )
                );
            }
        }
    }

    /*
     * =================================================
     * 食神格
     * =================================================
     */

    private void addEatingGodPatternRoutes(

            PatternTenGodNode selectedEatingGod,

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodRouteOccurrence> routes

    ) {

        /*
         * 偏印 → 月令 食神
         */
        for (
                PatternTenGodNode indirectResource
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .INDIRECT_RESOURCE
        )
        ) {

            if (
                    !controls(
                            indirectResource,
                            selectedEatingGod,
                            analysis
                    )
            ) {

                continue;
            }

            routes.add(
                    new PatternTenGodRouteOccurrence(

                            PatternTenGodRouteType
                                    .EATING_GOD_PATTERN_INDIRECT_RESOURCE_CONTROLS_EATING_GOD,

                            List.of(
                                    indirectResource,
                                    selectedEatingGod
                            )
                    )
            );

            /*
             * 財 → 바로 그 偏印 → 食神
             *
             * 生財護食.
             */
            for (
                    PatternTenGodNode wealth
                    : visibleWealthNodes(
                    analysis
            )
            ) {

                if (
                        controls(
                                wealth,
                                indirectResource,
                                analysis
                        )
                ) {

                    routes.add(
                            new PatternTenGodRouteOccurrence(

                                    PatternTenGodRouteType
                                            .EATING_GOD_PATTERN_WEALTH_PROTECTS_EATING_GOD,

                                    List.of(
                                            wealth,
                                            indirectResource,
                                            selectedEatingGod
                                    )
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 傷官格
     * =================================================
     */

    private void addHurtingOfficerPatternRoutes(

            PatternTenGodNode selectedHurtingOfficer,

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodRouteOccurrence> routes

    ) {

        /*
         * 傷官 → 財
         */
        List<PatternTenGodNode> generatedWealth =
                new ArrayList<>();

        for (
                PatternTenGodNode wealth
                : visibleWealthNodes(analysis)
        ) {

            if (
                    generates(
                            selectedHurtingOfficer,
                            wealth,
                            analysis
                    )
            ) {

                generatedWealth.add(
                        wealth
                );

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .HURTING_OFFICER_PATTERN_GENERATES_WEALTH,

                                List.of(
                                        selectedHurtingOfficer,
                                        wealth
                                )
                        )
                );
            }
        }

        /*
         * 印 → 傷官
         */
        for (
                PatternTenGodNode resource
                : visibleResourceNodes(
                analysis
        )
        ) {

            if (
                    controls(
                            resource,
                            selectedHurtingOfficer,
                            analysis
                    )
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .HURTING_OFFICER_PATTERN_RESOURCE_CONTROLS_HURTING_OFFICER,

                                List.of(
                                        resource,
                                        selectedHurtingOfficer
                                )
                        )
                );
            }
        }

        /*
         * 傷官 → 七殺
         */
        for (
                PatternTenGodNode killing
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
        ) {

            if (
                    controls(
                            selectedHurtingOfficer,
                            killing,
                            analysis
                    )
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .HURTING_OFFICER_PATTERN_CONTROLS_SEVEN_KILLINGS,

                                List.of(
                                        selectedHurtingOfficer,
                                        killing
                                )
                        )
                );
            }
        }

        /*
         * 傷官 → 正官
         */
        for (
                PatternTenGodNode officer
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .DIRECT_OFFICER
        )
        ) {

            if (
                    controls(
                            selectedHurtingOfficer,
                            officer,
                            analysis
                    )
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .HURTING_OFFICER_PATTERN_CONTROLS_DIRECT_OFFICER,

                                List.of(
                                        selectedHurtingOfficer,
                                        officer
                                )
                        )
                );
            }
        }

        /*
         * 傷官 → 財 A → 正官
         *
         * 반드시 같은 財 A.
         */
        for (
                PatternTenGodNode wealth
                : generatedWealth
        ) {

            for (
                    PatternTenGodNode officer
                    : analysis.visibleNodesOf(
                    MonthCommandPatternCandidateType
                            .DIRECT_OFFICER
            )
            ) {

                if (
                        generates(
                                wealth,
                                officer,
                                analysis
                        )
                ) {

                    routes.add(
                            new PatternTenGodRouteOccurrence(

                                    PatternTenGodRouteType
                                            .HURTING_OFFICER_PATTERN_WEALTH_BRIDGES_DIRECT_OFFICER,

                                    List.of(
                                            selectedHurtingOfficer,
                                            wealth,
                                            officer
                                    )
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 七殺格
     * =================================================
     */

    private void addSevenKillingsPatternRoutes(

            PatternTenGodNode selectedSevenKillings,

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodRouteOccurrence> routes

    ) {

        /*
         * 食神 → 月令 七殺
         */
        List<PatternTenGodNode> controllingEatingGods =
                new ArrayList<>();

        for (
                PatternTenGodNode eatingGod
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .EATING_GOD
        )
        ) {

            if (
                    controls(
                            eatingGod,
                            selectedSevenKillings,
                            analysis
                    )
            ) {

                controllingEatingGods.add(
                        eatingGod
                );

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .SEVEN_KILLINGS_PATTERN_EATING_GOD_CONTROLS_KILLING,

                                List.of(
                                        eatingGod,
                                        selectedSevenKillings
                                )
                        )
                );
            }
        }

        /*
         * 偏印 → 바로 그 食神
         */
        List<PatternTenGodNode> suppressingResources =
                new ArrayList<>();

        for (
                PatternTenGodNode eatingGod
                : controllingEatingGods
        ) {

            for (
                    PatternTenGodNode indirectResource
                    : analysis.visibleNodesOf(
                    MonthCommandPatternCandidateType
                            .INDIRECT_RESOURCE
            )
            ) {

                if (
                        controls(
                                indirectResource,
                                eatingGod,
                                analysis
                        )
                ) {

                    suppressingResources.add(
                            indirectResource
                    );

                    routes.add(
                            new PatternTenGodRouteOccurrence(

                                    PatternTenGodRouteType
                                            .SEVEN_KILLINGS_PATTERN_RESOURCE_SUPPRESSES_EATING_GOD,

                                    List.of(
                                            indirectResource,
                                            eatingGod,
                                            selectedSevenKillings
                                    )
                            )
                    );
                }
            }
        }

        /*
         * 財 → 바로 그 偏印 → 食神 → 七殺
         */
        for (
                PatternTenGodNode resource
                : suppressingResources
        ) {

            for (
                    PatternTenGodNode wealth
                    : visibleWealthNodes(
                    analysis
            )
            ) {

                if (
                        !controls(
                                wealth,
                                resource,
                                analysis
                        )
                ) {

                    continue;
                }

                for (
                        PatternTenGodNode eatingGod
                        : controllingEatingGods
                ) {

                    if (
                            controls(
                                    resource,
                                    eatingGod,
                                    analysis
                            )
                    ) {

                        routes.add(
                                new PatternTenGodRouteOccurrence(

                                        PatternTenGodRouteType
                                                .SEVEN_KILLINGS_PATTERN_WEALTH_REMOVES_RESOURCE_PRESERVING_EATING_GOD,

                                        List.of(
                                                wealth,
                                                resource,
                                                eatingGod,
                                                selectedSevenKillings
                                        )
                                )
                        );
                    }
                }
            }
        }

        /*
         * 財 → 月令 七殺
         */
        List<PatternTenGodNode> wealthGeneratingKilling =
                new ArrayList<>();

        for (
                PatternTenGodNode wealth
                : visibleWealthNodes(
                analysis
        )
        ) {

            if (
                    generates(
                            wealth,
                            selectedSevenKillings,
                            analysis
                    )
            ) {

                wealthGeneratingKilling.add(
                        wealth
                );

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .SEVEN_KILLINGS_PATTERN_WEALTH_GENERATES_KILLING,

                                List.of(
                                        wealth,
                                        selectedSevenKillings
                                )
                        )
                );
            }
        }

        /*
         * 月令 七殺 → 印 A → 身
         */
        PatternTenGodNode dayMaster =
                analysis.dayMasterNode();

        List<PatternTenGodNode> killingResourceNodes =
                new ArrayList<>();

        for (
                PatternTenGodNode resource
                : visibleResourceNodes(
                analysis
        )
        ) {

            if (
                    generates(
                            selectedSevenKillings,
                            resource,
                            analysis
                    )

                            &&

                            generates(
                                    resource,
                                    dayMaster,
                                    analysis
                            )
            ) {

                killingResourceNodes.add(
                        resource
                );

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .SEVEN_KILLINGS_PATTERN_KILLING_RESOURCE_DAY_MASTER,

                                List.of(
                                        selectedSevenKillings,
                                        resource,
                                        dayMaster
                                )
                        )
                );
            }
        }

        /*
         * 財 A → 七殺 → 印 B → 身
         */
        for (
                PatternTenGodNode wealth
                : wealthGeneratingKilling
        ) {

            for (
                    PatternTenGodNode resource
                    : killingResourceNodes
            ) {

                routes.add(
                        new PatternTenGodRouteOccurrence(

                                PatternTenGodRouteType
                                        .SEVEN_KILLINGS_PATTERN_WEALTH_KILLING_RESOURCE_DAY_MASTER,

                                List.of(
                                        wealth,
                                        selectedSevenKillings,
                                        resource,
                                        dayMaster
                                )
                        )
                );

                /*
                 * 같은 印 B를 財 A가 剋하는가.
                 */
                if (
                        controls(
                                wealth,
                                resource,
                                analysis
                        )
                ) {

                    routes.add(
                            new PatternTenGodRouteOccurrence(

                                    PatternTenGodRouteType
                                            .SEVEN_KILLINGS_PATTERN_WEALTH_DAMAGES_KILLING_RESOURCE,

                                    List.of(
                                            wealth,
                                            selectedSevenKillings,
                                            resource,
                                            dayMaster
                                    )
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 공통 七殺 → 印 → 身
     * =================================================
     */

    private void addVisibleKillingResourceRoutes(

            PatternTenGodInteractionAnalysis analysis,

            List<PatternTenGodRouteOccurrence> routes

    ) {

        PatternTenGodNode dayMaster =
                analysis.dayMasterNode();

        for (
                PatternTenGodNode killing
                : analysis.visibleNodesOf(
                MonthCommandPatternCandidateType
                        .SEVEN_KILLINGS
        )
        ) {

            for (
                    PatternTenGodNode resource
                    : visibleResourceNodes(
                    analysis
            )
            ) {

                if (
                        generates(
                                killing,
                                resource,
                                analysis
                        )

                                &&

                                generates(
                                        resource,
                                        dayMaster,
                                        analysis
                                )
                ) {

                    routes.add(
                            new PatternTenGodRouteOccurrence(

                                    PatternTenGodRouteType
                                            .VISIBLE_KILLING_RESOURCE_DAY_MASTER,

                                    List.of(
                                            killing,
                                            resource,
                                            dayMaster
                                    )
                            )
                    );
                }
            }
        }
    }

    /*
     * =================================================
     * 선택 월령 MAIN 노드
     * =================================================
     */

    private PatternTenGodNode findSelectedMonthNode(

            MonthCommandPatternCandidate candidate,

            PatternTenGodInteractionAnalysis analysis

    ) {

        return analysis
                .nodes()
                .stream()
                .filter(
                        PatternTenGodNode
                                ::hidden
                )
                .filter(
                        node ->
                                node.position()
                                        == PillarPosition.MONTH
                )
                .filter(
                        node ->
                                node.stem()
                                        == candidate.hiddenStem()
                )
                .filter(
                        node ->
                                node.hiddenStemPhase()
                                        .orElse(null)
                                        == candidate.phase()
                )
                .filter(
                        node ->
                                node.hasTenGodType(
                                        candidate.candidateType()
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "선택된 月令 MAIN 십성 노드를 찾을 수 없습니다."
                                )
                );
    }

    /*
     * =================================================
     * node collections
     * =================================================
     */

    private List<PatternTenGodNode> visibleWealthNodes(
            PatternTenGodInteractionAnalysis analysis
    ) {

        List<PatternTenGodNode> result =
                new ArrayList<>();

        result.addAll(
                analysis.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_WEALTH
                )
        );

        result.addAll(
                analysis.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_WEALTH
                )
        );

        return List.copyOf(result);
    }

    private List<PatternTenGodNode> visibleResourceNodes(
            PatternTenGodInteractionAnalysis analysis
    ) {

        List<PatternTenGodNode> result =
                new ArrayList<>();

        result.addAll(
                analysis.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .DIRECT_RESOURCE
                )
        );

        result.addAll(
                analysis.visibleNodesOf(
                        MonthCommandPatternCandidateType
                                .INDIRECT_RESOURCE
                )
        );

        return List.copyOf(result);
    }

    /*
     * =================================================
     * graph
     * =================================================
     */

    private boolean generates(

            PatternTenGodNode source,

            PatternTenGodNode target,

            PatternTenGodInteractionAnalysis analysis

    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.GENERATES

                                        &&

                                        link.source()
                                                .equals(source)

                                        &&

                                        link.target()
                                                .equals(target)
                );
    }

    private boolean controls(

            PatternTenGodNode source,

            PatternTenGodNode target,

            PatternTenGodInteractionAnalysis analysis

    ) {

        return analysis
                .links()
                .stream()
                .anyMatch(
                        link ->
                                link.type()
                                        == PatternTenGodLinkType.CONTROLS

                                        &&

                                        link.source()
                                                .equals(source)

                                        &&

                                        link.target()
                                                .equals(target)
                );
    }

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis analysis

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                analysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "PatternInteractionAnalysis와 "
                            +
                            "PatternTenGodInteractionAnalysis의 일간이 일치하지 않습니다."
            );
        }
    }
}