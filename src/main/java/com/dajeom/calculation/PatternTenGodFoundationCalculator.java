package com.dajeom.calculation;

import com.dajeom.domain.HeavenlyStem;
import com.dajeom.domain.MonthCommandPatternCandidate;
import com.dajeom.domain.MonthCommandPatternCandidateType;
import com.dajeom.domain.PatternInteractionAnalysis;
import com.dajeom.domain.PatternTenGodFoundationAnalysis;
import com.dajeom.domain.PatternTenGodFoundationProfile;
import com.dajeom.domain.PatternTenGodFoundationReason;
import com.dajeom.domain.PatternTenGodFoundationState;
import com.dajeom.domain.PatternTenGodInteractionAnalysis;
import com.dajeom.domain.PatternTenGodNode;
import com.dajeom.domain.PatternTenGodRootHit;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 각 십성의 구조적 foundation을 계산한다.
 *
 * =================================================
 *
 * foundation 확인 조건:
 *
 * 1. 선택된 月令 MAIN 십성 자체
 *
 * 또는
 *
 * 2. 투출 천간이 같은 십성 계열의
 *    지장간에 실제 통근
 *
 * =================================================
 *
 * 같은 계열:
 *
 * 官:
 * 正官 / 七殺
 *
 * 財:
 * 正財 / 偏財
 *
 * 印:
 * 正印 / 偏印
 *
 * 食傷:
 * 食神 / 傷官
 *
 * 比劫:
 * 比肩 / 劫財
 *
 * =================================================
 *
 * 예:
 *
 * 正財 천간이
 * 偏財 지장간에 뿌리를 둬도
 *
 * 같은 財 오행의 통근 기반으로 본다.
 *
 * =================================================
 *
 * 숫자 / 점수 / 가중치 없음.
 */
@Component
public class PatternTenGodFoundationCalculator {

    public PatternTenGodFoundationProfile analyze(

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

        Optional<MonthCommandPatternCandidate> selectedCandidate =
                interaction
                        .provisionalStandardCandidate();

        List<PatternTenGodFoundationAnalysis> results =
                new ArrayList<>();

        for (
                MonthCommandPatternCandidateType type
                : MonthCommandPatternCandidateType.values()
        ) {

            results.add(
                    analyzeType(
                            type,
                            selectedCandidate,
                            tenGodInteractions
                    )
            );
        }

        return new PatternTenGodFoundationProfile(
                interaction.dayMaster(),
                results
        );
    }

    private PatternTenGodFoundationAnalysis analyzeType(

            MonthCommandPatternCandidateType type,

            Optional<MonthCommandPatternCandidate> selectedCandidate,

            PatternTenGodInteractionAnalysis interactions

    ) {

        EnumSet<PatternTenGodFoundationReason> reasons =
                EnumSet.noneOf(
                        PatternTenGodFoundationReason.class
                );

        List<PatternTenGodNode> visibleNodes =
                interactions
                        .visibleNodesOf(type);

        List<PatternTenGodNode> hiddenFamilyNodes =
                interactions
                        .nodes()
                        .stream()
                        .filter(
                                PatternTenGodNode
                                        ::hidden
                        )
                        .filter(
                                node ->
                                        belongsToSameFamily(
                                                node,
                                                type
                                        )
                        )
                        .toList();

        if (!visibleNodes.isEmpty()) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .VISIBLE_STEM_PRESENT
            );
        }

        if (!hiddenFamilyNodes.isEmpty()) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .HIDDEN_FAMILY_PRESENT
            );
        }

        boolean monthCommandMain =
                selectedCandidate
                        .map(
                                candidate ->
                                        candidate.candidateType()
                                                == type
                        )
                        .orElse(false);

        if (monthCommandMain) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .MONTH_COMMAND_MAIN_FOUNDATION
            );
        }

        List<PatternTenGodRootHit> rootHits =
                findRootHits(
                        visibleNodes,
                        hiddenFamilyNodes
                );

        if (!rootHits.isEmpty()) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .VISIBLE_STEM_ROOTED
            );
        }

        if (
                rootHits
                        .stream()
                        .anyMatch(
                                PatternTenGodRootHit
                                        ::exactStem
                        )
        ) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .EXACT_STEM_ROOT_PRESENT
            );
        }

        if (
                rootHits
                        .stream()
                        .anyMatch(
                                hit ->
                                        !hit.exactStem()
                        )
        ) {

            reasons.add(
                    PatternTenGodFoundationReason
                            .SAME_FAMILY_ROOT_PRESENT
            );
        }

        PatternTenGodFoundationState state;

        boolean present =
                monthCommandMain
                        ||
                        !visibleNodes.isEmpty()
                        ||
                        !hiddenFamilyNodes.isEmpty();

        if (!present) {

            state =
                    PatternTenGodFoundationState
                            .ABSENT;

        } else if (
                monthCommandMain
                        ||
                        !rootHits.isEmpty()
        ) {

            state =
                    PatternTenGodFoundationState
                            .FOUNDATION_CONFIRMED;

        } else {

            reasons.add(
                    PatternTenGodFoundationReason
                            .FOUNDATION_NOT_CONFIRMED
            );

            state =
                    PatternTenGodFoundationState
                            .PRESENT_WITHOUT_CONFIRMED_FOUNDATION;
        }

        return new PatternTenGodFoundationAnalysis(

                type,

                visibleNodes,

                hiddenFamilyNodes,

                rootHits,

                monthCommandMain,

                state,

                reasons
        );
    }

    private List<PatternTenGodRootHit> findRootHits(

            List<PatternTenGodNode> visibleNodes,

            List<PatternTenGodNode> hiddenFamilyNodes

    ) {

        List<PatternTenGodRootHit> results =
                new ArrayList<>();

        for (
                PatternTenGodNode visible
                : visibleNodes
        ) {

            for (
                    PatternTenGodNode hidden
                    : hiddenFamilyNodes
            ) {

                /*
                 * 같은 십성 family는
                 * 일간 기준 같은 오행 관계이므로
                 * 통근 기반으로 사용한다.
                 */
                results.add(
                        new PatternTenGodRootHit(

                                visible,

                                hidden,

                                visible.stem()
                                        == hidden.stem()
                        )
                );
            }
        }

        return List.copyOf(results);
    }

    private boolean belongsToSameFamily(

            PatternTenGodNode node,

            MonthCommandPatternCandidateType requestedType

    ) {

        for (
                MonthCommandPatternCandidateType familyType
                : familyOf(requestedType)
        ) {

            if (
                    node.hasTenGodType(
                            familyType
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    private List<MonthCommandPatternCandidateType> familyOf(
            MonthCommandPatternCandidateType type
    ) {

        return switch (type) {

            case DIRECT_OFFICER,
                 SEVEN_KILLINGS ->

                    List.of(
                            MonthCommandPatternCandidateType
                                    .DIRECT_OFFICER,
                            MonthCommandPatternCandidateType
                                    .SEVEN_KILLINGS
                    );

            case DIRECT_WEALTH,
                 INDIRECT_WEALTH ->

                    List.of(
                            MonthCommandPatternCandidateType
                                    .DIRECT_WEALTH,
                            MonthCommandPatternCandidateType
                                    .INDIRECT_WEALTH
                    );

            case DIRECT_RESOURCE,
                 INDIRECT_RESOURCE ->

                    List.of(
                            MonthCommandPatternCandidateType
                                    .DIRECT_RESOURCE,
                            MonthCommandPatternCandidateType
                                    .INDIRECT_RESOURCE
                    );

            case EATING_GOD,
                 HURTING_OFFICER ->

                    List.of(
                            MonthCommandPatternCandidateType
                                    .EATING_GOD,
                            MonthCommandPatternCandidateType
                                    .HURTING_OFFICER
                    );

            case MONTH_COMPANION,
                 MONTH_ROB_WEALTH ->

                    List.of(
                            MonthCommandPatternCandidateType
                                    .MONTH_COMPANION,
                            MonthCommandPatternCandidateType
                                    .MONTH_ROB_WEALTH
                    );
        };
    }

    private void validateDayMaster(

            PatternInteractionAnalysis interaction,

            PatternTenGodInteractionAnalysis tenGodInteractions

    ) {

        HeavenlyStem dayMaster =
                interaction.dayMaster();

        if (
                tenGodInteractions.dayMaster()
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