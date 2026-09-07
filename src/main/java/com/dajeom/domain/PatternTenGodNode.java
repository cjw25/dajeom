package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 격국 생극 관계 그래프의 한 노드.
 *
 * DAY_MASTER
 * - 일간 자신
 *
 * VISIBLE_STEM
 * - 년간 / 월간 / 시간
 *
 * HIDDEN_STEM
 * - 각 지지 내부의 지장간
 *
 * 위치와 투출/암장을 그대로 보존한다.
 */
public record PatternTenGodNode(

        PatternTenGodNodeKind kind,

        PillarPosition position,

        HeavenlyStem stem,

        Optional<EarthlyBranch> branch,

        Optional<HiddenStemPhase> hiddenStemPhase,

        Optional<MonthCommandPatternCandidateType> tenGodType

) {

    public PatternTenGodNode {

        Objects.requireNonNull(
                kind,
                "노드 종류는 필수입니다."
        );

        Objects.requireNonNull(
                position,
                "위치는 필수입니다."
        );

        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        branch =
                branch == null
                        ? Optional.empty()
                        : branch;

        hiddenStemPhase =
                hiddenStemPhase == null
                        ? Optional.empty()
                        : hiddenStemPhase;

        tenGodType =
                tenGodType == null
                        ? Optional.empty()
                        : tenGodType;

        validate(
                kind,
                position,
                branch,
                hiddenStemPhase,
                tenGodType
        );
    }

    /**
     * 일간 노드 생성.
     */
    public static PatternTenGodNode dayMaster(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        return new PatternTenGodNode(
                PatternTenGodNodeKind.DAY_MASTER,
                PillarPosition.DAY,
                dayMaster,
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * 년간 / 월간 / 시간 노드 생성.
     */
    public static PatternTenGodNode visibleStem(

            PillarPosition position,

            HeavenlyStem stem,

            MonthCommandPatternCandidateType tenGodType

    ) {

        Objects.requireNonNull(
                position,
                "천간 위치는 필수입니다."
        );

        Objects.requireNonNull(
                stem,
                "천간은 필수입니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "십성 타입은 필수입니다."
        );

        return new PatternTenGodNode(
                PatternTenGodNodeKind.VISIBLE_STEM,
                position,
                stem,
                Optional.empty(),
                Optional.empty(),
                Optional.of(
                        tenGodType
                )
        );
    }

    /**
     * 지장간 노드 생성.
     */
    public static PatternTenGodNode hiddenStem(

            PillarPosition branchPosition,

            EarthlyBranch branch,

            HeavenlyStem hiddenStem,

            HiddenStemPhase hiddenStemPhase,

            MonthCommandPatternCandidateType tenGodType

    ) {

        Objects.requireNonNull(
                branchPosition,
                "지지 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemPhase,
                "지장간 단계는 필수입니다."
        );

        Objects.requireNonNull(
                tenGodType,
                "십성 타입은 필수입니다."
        );

        return new PatternTenGodNode(
                PatternTenGodNodeKind.HIDDEN_STEM,
                branchPosition,
                hiddenStem,
                Optional.of(
                        branch
                ),
                Optional.of(
                        hiddenStemPhase
                ),
                Optional.of(
                        tenGodType
                )
        );
    }

    public boolean dayMaster() {

        return kind
                == PatternTenGodNodeKind.DAY_MASTER;
    }

    public boolean visible() {

        return kind
                == PatternTenGodNodeKind.VISIBLE_STEM;
    }

    public boolean hidden() {

        return kind
                == PatternTenGodNodeKind.HIDDEN_STEM;
    }

    public boolean hasTenGodType(
            MonthCommandPatternCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "십성 타입은 필수입니다."
        );

        return tenGodType
                .map(
                        value ->
                                value == type
                )
                .orElse(
                        false
                );
    }

    private static void validate(

            PatternTenGodNodeKind kind,

            PillarPosition position,

            Optional<EarthlyBranch> branch,

            Optional<HiddenStemPhase> hiddenStemPhase,

            Optional<MonthCommandPatternCandidateType> tenGodType

    ) {

        switch (kind) {

            case DAY_MASTER -> {

                if (
                        position
                                != PillarPosition.DAY
                ) {

                    throw new IllegalArgumentException(
                            "DAY_MASTER 노드는 DAY 위치여야 합니다."
                    );
                }

                if (
                        branch.isPresent()
                                ||
                                hiddenStemPhase.isPresent()
                                ||
                                tenGodType.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "DAY_MASTER 노드에는 지지/지장간 단계/십성 타입을 넣을 수 없습니다."
                    );
                }
            }

            case VISIBLE_STEM -> {

                if (
                        position
                                == PillarPosition.DAY
                ) {

                    throw new IllegalArgumentException(
                            "VISIBLE_STEM에는 일간 자신을 넣을 수 없습니다."
                    );
                }

                if (
                        branch.isPresent()
                                ||
                                hiddenStemPhase.isPresent()
                ) {

                    throw new IllegalArgumentException(
                            "VISIBLE_STEM에는 지지 또는 지장간 단계가 존재할 수 없습니다."
                    );
                }

                if (
                        tenGodType.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "VISIBLE_STEM에는 십성 타입이 필요합니다."
                    );
                }
            }

            case HIDDEN_STEM -> {

                if (
                        branch.isEmpty()
                                ||
                                hiddenStemPhase.isEmpty()
                                ||
                                tenGodType.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "HIDDEN_STEM에는 지지, 지장간 단계, 십성 타입이 모두 필요합니다."
                    );
                }
            }
        }
    }
}