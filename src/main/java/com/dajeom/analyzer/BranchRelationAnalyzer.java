package com.dajeom.analyzer;

import com.dajeom.constant.BranchRelationTable;
import com.dajeom.model.BranchRelation;
import com.dajeom.model.FourPillars;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.RelationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 원국의 지지 육합·삼합·반합·충·형·파·해를 분석한다.
 */
@Component
public class BranchRelationAnalyzer {

    public List<BranchRelation> analyze(
            FourPillars fourPillars
    ) {
        List<BranchPosition> branches =
                createBranchPositions(
                        fourPillars
                );

        List<BranchRelation> result =
                new ArrayList<>();

        Set<Set<EarthlyBranch>>
                completedHarmonyGroups =
                analyzeThreeHarmony(
                        branches,
                        result
                );

        analyzeTriplePunishments(
                branches,
                result
        );

        analyzeSelfPunishments(
                branches,
                result
        );

        analyzePairs(
                branches,
                completedHarmonyGroups,
                result
        );

        return List.copyOf(result);
    }

    private Set<Set<EarthlyBranch>>
    analyzeThreeHarmony(
            List<BranchPosition> branches,
            List<BranchRelation> destination
    ) {
        Set<Set<EarthlyBranch>> completedGroups =
                new HashSet<>();

        Set<EarthlyBranch> existingBranches =
                collectBranchSet(branches);

        for (BranchRelationTable.BranchTripleRelation
                harmony
                : BranchRelationTable.getThreeHarmonies()) {

            if (!existingBranches.containsAll(
                    harmony.branches()
            )) {
                continue;
            }

            List<BranchPosition> members =
                    findMembers(
                            branches,
                            harmony.branches()
                    );

            destination.add(
                    new BranchRelation(
                            RelationType.THREE_HARMONY,

                            members.stream()
                                    .map(BranchPosition::position)
                                    .toList(),

                            members.stream()
                                    .map(BranchPosition::branch)
                                    .toList(),

                            harmony.transformationElement(),

                            true,

                            false,

                            createBranchNames(members)
                                    + " 삼합이 존재하지만 "
                                    + "4차에서는 삼합국 후보로만 판정함"
                    )
            );

            completedGroups.add(
                    harmony.branches()
            );
        }

        return completedGroups;
    }

    private void analyzeTriplePunishments(
            List<BranchPosition> branches,
            List<BranchRelation> destination
    ) {
        Set<EarthlyBranch> existingBranches =
                collectBranchSet(branches);

        for (BranchRelationTable.BranchTripleRelation
                punishment
                : BranchRelationTable.getTriplePunishments()) {

            if (!existingBranches.containsAll(
                    punishment.branches()
            )) {
                continue;
            }

            List<BranchPosition> members =
                    findMembers(
                            branches,
                            punishment.branches()
                    );

            destination.add(
                    new BranchRelation(
                            RelationType.PUNISHMENT,

                            members.stream()
                                    .map(BranchPosition::position)
                                    .toList(),

                            members.stream()
                                    .map(BranchPosition::branch)
                                    .toList(),

                            null,

                            false,

                            false,

                            createBranchNames(members)
                                    + " 삼형 관계가 성립함"
                    )
            );
        }
    }

    private void analyzeSelfPunishments(
            List<BranchPosition> branches,
            List<BranchRelation> destination
    ) {
        Map<EarthlyBranch, List<BranchPosition>>
                grouped =
                new EnumMap<>(EarthlyBranch.class);

        for (BranchPosition branch : branches) {
            grouped.computeIfAbsent(
                    branch.branch(),
                    ignored -> new ArrayList<>()
            ).add(branch);
        }

        for (Map.Entry<
                EarthlyBranch,
                List<BranchPosition>
                > entry : grouped.entrySet()) {

            if (entry.getValue().size() < 2) {
                continue;
            }

            if (!BranchRelationTable
                    .isSelfPunishment(
                            entry.getKey()
                    )) {
                continue;
            }

            List<BranchPosition> members =
                    entry.getValue();

            destination.add(
                    new BranchRelation(
                            RelationType.PUNISHMENT,

                            members.stream()
                                    .map(BranchPosition::position)
                                    .toList(),

                            members.stream()
                                    .map(BranchPosition::branch)
                                    .toList(),

                            null,

                            false,

                            false,

                            entry.getKey()
                                    .getChineseCharacter()
                                    + entry.getKey()
                                    .getChineseCharacter()
                                    + " 자형 관계가 성립함"
                    )
            );
        }
    }

    private void analyzePairs(
            List<BranchPosition> branches,
            Set<Set<EarthlyBranch>>
                    completedHarmonyGroups,
            List<BranchRelation> destination
    ) {
        for (int firstIndex = 0;
             firstIndex < branches.size();
             firstIndex++) {

            for (int secondIndex = firstIndex + 1;
                 secondIndex < branches.size();
                 secondIndex++) {

                BranchPosition first =
                        branches.get(firstIndex);

                BranchPosition second =
                        branches.get(secondIndex);

                addPairRelation(
                        destination,
                        RelationType.SIX_HARMONY,
                        first,
                        second
                );

                addPairRelation(
                        destination,
                        RelationType.CLASH,
                        first,
                        second
                );

                addPairRelation(
                        destination,
                        RelationType.PUNISHMENT,
                        first,
                        second
                );

                addPairRelation(
                        destination,
                        RelationType.BREAK,
                        first,
                        second
                );

                addPairRelation(
                        destination,
                        RelationType.HARM,
                        first,
                        second
                );

                addHalfHarmony(
                        destination,
                        completedHarmonyGroups,
                        first,
                        second
                );
            }
        }
    }

    private void addPairRelation(
            List<BranchRelation> destination,
            RelationType type,
            BranchPosition first,
            BranchPosition second
    ) {
        BranchRelationTable.findPairRelation(
                        type,
                        first.branch(),
                        second.branch()
                )
                .ifPresent(definition ->
                        destination.add(
                                new BranchRelation(
                                        type,

                                        List.of(
                                                first.position(),
                                                second.position()
                                        ),

                                        List.of(
                                                first.branch(),
                                                second.branch()
                                        ),

                                        definition
                                                .transformationElement(),

                                        type
                                                == RelationType
                                                .SIX_HARMONY,

                                        false,

                                        first.branch()
                                                .getChineseCharacter()
                                                + second.branch()
                                                .getChineseCharacter()
                                                + " "
                                                + type.getKoreanName()
                                                + " 관계가 존재함"
                                )
                        )
                );
    }

    private void addHalfHarmony(
            List<BranchRelation> destination,
            Set<Set<EarthlyBranch>>
                    completedHarmonyGroups,
            BranchPosition first,
            BranchPosition second
    ) {
        BranchRelationTable
                .findHarmonyGroupContaining(
                        first.branch(),
                        second.branch()
                )
                .ifPresent(group -> {
                    if (completedHarmonyGroups.contains(
                            group.branches()
                    )) {
                        return;
                    }

                    destination.add(
                            new BranchRelation(
                                    RelationType.HALF_HARMONY,

                                    List.of(
                                            first.position(),
                                            second.position()
                                    ),

                                    List.of(
                                            first.branch(),
                                            second.branch()
                                    ),

                                    group.transformationElement(),

                                    true,

                                    false,

                                    first.branch()
                                            .getChineseCharacter()
                                            + second.branch()
                                            .getChineseCharacter()
                                            + " 반합 후보가 존재함"
                            )
                    );
                });
    }

    private Set<EarthlyBranch> collectBranchSet(
            List<BranchPosition> branches
    ) {
        Set<EarthlyBranch> result =
                new HashSet<>();

        for (BranchPosition branch : branches) {
            result.add(branch.branch());
        }

        return result;
    }

    private List<BranchPosition> findMembers(
            List<BranchPosition> branches,
            Set<EarthlyBranch> targets
    ) {
        List<BranchPosition> result =
                new ArrayList<>();

        for (EarthlyBranch target : targets) {
            branches.stream()
                    .filter(position ->
                            position.branch() == target
                    )
                    .findFirst()
                    .ifPresent(result::add);
        }

        return result;
    }

    private String createBranchNames(
            List<BranchPosition> branches
    ) {
        return branches.stream()
                .map(branch ->
                        branch.branch()
                                .getChineseCharacter()
                )
                .reduce(
                        "",
                        String::concat
                );
    }

    private List<BranchPosition> createBranchPositions(
            FourPillars fourPillars
    ) {
        return List.of(
                new BranchPosition(
                        PillarPosition.YEAR_BRANCH,
                        fourPillars.yearPillar().branch()
                ),

                new BranchPosition(
                        PillarPosition.MONTH_BRANCH,
                        fourPillars.monthPillar().branch()
                ),

                new BranchPosition(
                        PillarPosition.DAY_BRANCH,
                        fourPillars.dayPillar().branch()
                ),

                new BranchPosition(
                        PillarPosition.HOUR_BRANCH,
                        fourPillars.hourPillar().branch()
                )
        );
    }

    private record BranchPosition(
            PillarPosition position,
            EarthlyBranch branch
    ) {
    }
}