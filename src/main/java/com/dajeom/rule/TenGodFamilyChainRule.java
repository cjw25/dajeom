package com.dajeom.rule;

import com.dajeom.model.TenGodFamilyInteractionEvidence;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.TenGodFamilyChainReasonType;
import com.dajeom.type.TenGodFamilyInteractionLevel;
import com.dajeom.type.TenGodFamilyInteractionType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-7.
 *
 * Family Interaction / Chain Rule.
 *
 * =================================================
 *
 * Family share 기준:
 *
 * 8% 이상
 * -> 기본적인 세력 존재 기준
 *
 * 15% 이상
 * -> 의미 있는 Family 세력
 *
 * =================================================
 *
 * Interaction STRONG:
 *
 * 양쪽 모두 15% 이상.
 *
 * =================================================
 *
 * Interaction MEANINGFUL:
 *
 * 한쪽 15% 이상
 * +
 * 반대쪽 8% 이상.
 *
 * =================================================
 *
 * 나머지 양쪽 존재 관계:
 *
 * REFERENCE
 *
 * =================================================
 *
 * 임의의 총점이나 평균을 사용하지 않는다.
 */
@Component
public class TenGodFamilyChainRule {

    public static final int
            BASIC_FAMILY_SHARE_BASIS_POINTS =
            800;

    public static final int
            MEANINGFUL_FAMILY_SHARE_BASIS_POINTS =
            1_500;

    /*
     * =================================================
     * 관계 유효성
     * =================================================
     */

    public boolean validGeneration(

            DayMasterElementRole source,

            DayMasterElementRole target

    ) {

        if (
                source == null
                        || target == null
        ) {

            return false;
        }

        return switch (
                source
                ) {

            case COMPANION ->
                    target
                            == DayMasterElementRole.OUTPUT;

            case OUTPUT ->
                    target
                            == DayMasterElementRole.WEALTH;

            case WEALTH ->
                    target
                            == DayMasterElementRole.OFFICER;

            case OFFICER ->
                    target
                            == DayMasterElementRole.RESOURCE;

            case RESOURCE ->
                    target
                            == DayMasterElementRole.COMPANION;
        };
    }

    public boolean validControl(

            DayMasterElementRole source,

            DayMasterElementRole target

    ) {

        if (
                source == null
                        || target == null
        ) {

            return false;
        }

        return switch (
                source
                ) {

            case COMPANION ->
                    target
                            == DayMasterElementRole.WEALTH;

            case WEALTH ->
                    target
                            == DayMasterElementRole.RESOURCE;

            case RESOURCE ->
                    target
                            == DayMasterElementRole.OUTPUT;

            case OUTPUT ->
                    target
                            == DayMasterElementRole.OFFICER;

            case OFFICER ->
                    target
                            == DayMasterElementRole.COMPANION;
        };
    }

    /*
     * =================================================
     * Interaction 수준
     * =================================================
     */

    public TenGodFamilyInteractionLevel
    determineInteractionLevel(

            int sourceShareBasisPoints,

            int targetShareBasisPoints

    ) {

        if (
                sourceShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS

                        && targetShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
        ) {

            return TenGodFamilyInteractionLevel
                    .STRONG;
        }

        boolean sourceMeaningful =
                sourceShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS;

        boolean targetMeaningful =
                targetShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS;

        boolean sourceBasic =
                sourceShareBasisPoints
                        >= BASIC_FAMILY_SHARE_BASIS_POINTS;

        boolean targetBasic =
                targetShareBasisPoints
                        >= BASIC_FAMILY_SHARE_BASIS_POINTS;

        if (
                (
                        sourceMeaningful
                                && targetBasic
                )
                        || (
                        targetMeaningful
                                && sourceBasic
                )
        ) {

            return TenGodFamilyInteractionLevel
                    .MEANINGFUL;
        }

        return TenGodFamilyInteractionLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * Chain 수준
     * =================================================
     *
     * 가장 약한 Interaction이
     * 전체 Chain의 수준을 결정한다.
     */

    public TenGodFamilyInteractionLevel
    determineChainLevel(
            List<TenGodFamilyInteractionEvidence> interactions
    ) {

        Objects.requireNonNull(
                interactions,
                "Chain Interaction 목록이 필요합니다."
        );

        if (
                interactions.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Chain에는 하나 이상의 Interaction이 필요합니다."
            );
        }

        boolean allStrong =
                interactions
                        .stream()
                        .allMatch(
                                TenGodFamilyInteractionEvidence
                                        ::strong
                        );

        if (
                allStrong
        ) {

            return TenGodFamilyInteractionLevel
                    .STRONG;
        }

        boolean allMeaningful =
                interactions
                        .stream()
                        .allMatch(
                                TenGodFamilyInteractionEvidence
                                        ::meaningful
                        );

        if (
                allMeaningful
        ) {

            return TenGodFamilyInteractionLevel
                    .MEANINGFUL;
        }

        return TenGodFamilyInteractionLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * Interaction Reason
     * =================================================
     */

    public List<TenGodFamilyChainReasonType>
    buildInteractionReasons(

            TenGodFamilyInteractionType interactionType,

            TenGodFamilyInteractionLevel level,

            int sourcePower,

            int targetPower,

            int sourceShareBasisPoints,

            int targetShareBasisPoints,

            boolean sourceVisible,

            boolean targetVisible,

            boolean sourceMonthPresent,

            boolean targetMonthPresent

    ) {

        Objects.requireNonNull(
                interactionType,
                "Interaction 종류가 필요합니다."
        );

        Objects.requireNonNull(
                level,
                "Interaction 수준이 필요합니다."
        );

        List<TenGodFamilyChainReasonType> reasons =
                new ArrayList<>();

        reasons.add(
                TenGodFamilyChainReasonType
                        .SOURCE_FAMILY_PRESENT
        );

        reasons.add(
                TenGodFamilyChainReasonType
                        .TARGET_FAMILY_PRESENT
        );

        if (
                sourceShareBasisPoints
                        >= BASIC_FAMILY_SHARE_BASIS_POINTS
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SOURCE_FAMILY_MEANINGFUL
            );
        }

        if (
                targetShareBasisPoints
                        >= BASIC_FAMILY_SHARE_BASIS_POINTS
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .TARGET_FAMILY_MEANINGFUL
            );
        }

        if (
                sourceShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SOURCE_FAMILY_STRONG
            );
        }

        if (
                targetShareBasisPoints
                        >= MEANINGFUL_FAMILY_SHARE_BASIS_POINTS
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .TARGET_FAMILY_STRONG
            );
        }

        if (
                sourceVisible
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SOURCE_VISIBLE
            );
        }

        if (
                targetVisible
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .TARGET_VISIBLE
            );
        }

        if (
                sourceMonthPresent
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SOURCE_MONTH_PRESENT
            );
        }

        if (
                targetMonthPresent
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .TARGET_MONTH_PRESENT
            );
        }

        if (
                interactionType.generation()
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .GENERATION_RELATION
            );

        } else {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .CONTROL_RELATION
            );
        }

        if (
                sourcePower > targetPower
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SOURCE_EXCEEDS_TARGET
            );

        } else if (
                targetPower > sourcePower
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .TARGET_EXCEEDS_SOURCE
            );
        }

        if (
                similarPower(
                        sourcePower,
                        targetPower
                )
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .SIMILAR_POWER
            );
        }

        switch (
                level
        ) {

            case REFERENCE ->
                    reasons.add(
                            TenGodFamilyChainReasonType
                                    .REFERENCE_INTERACTION
                    );

            case MEANINGFUL ->
                    reasons.add(
                            TenGodFamilyChainReasonType
                                    .MEANINGFUL_INTERACTION
                    );

            case STRONG ->
                    reasons.add(
                            TenGodFamilyChainReasonType
                                    .STRONG_INTERACTION
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * Chain Reason
     * =================================================
     */

    public List<TenGodFamilyChainReasonType>
    buildChainReasons(

            List<TenGodFamilyInteractionEvidence> interactions,

            TenGodFamilyInteractionLevel chainLevel

    ) {

        Objects.requireNonNull(
                interactions,
                "Chain Interaction 목록이 필요합니다."
        );

        Objects.requireNonNull(
                chainLevel,
                "Chain 수준이 필요합니다."
        );

        List<TenGodFamilyChainReasonType> reasons =
                new ArrayList<>();

        reasons.add(
                TenGodFamilyChainReasonType
                        .FULL_CHAIN_PRESENT
        );

        if (
                interactions
                        .stream()
                        .allMatch(
                                TenGodFamilyInteractionEvidence
                                        ::meaningful
                        )
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .ALL_CHAIN_INTERACTIONS_MEANINGFUL
            );
        }

        if (
                interactions
                        .stream()
                        .allMatch(
                                TenGodFamilyInteractionEvidence
                                        ::strong
                        )
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .ALL_CHAIN_INTERACTIONS_STRONG
            );
        }

        if (
                interactions
                        .stream()
                        .anyMatch(
                                interaction ->
                                        interaction.sourceVisible()
                                                || interaction.targetVisible()
                        )
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .CHAIN_HAS_VISIBLE_FAMILY
            );
        }

        if (
                interactions
                        .stream()
                        .anyMatch(
                                interaction ->
                                        interaction.sourceMonthPresent()
                                                || interaction.targetMonthPresent()
                        )
        ) {

            reasons.add(
                    TenGodFamilyChainReasonType
                            .CHAIN_HAS_MONTH_ANCHOR
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private boolean similarPower(

            int firstPower,

            int secondPower

    ) {

        if (
                firstPower <= 0
                        || secondPower <= 0
        ) {

            return false;
        }

        int larger =
                Math.max(
                        firstPower,
                        secondPower
                );

        int smaller =
                Math.min(
                        firstPower,
                        secondPower
                );

        return smaller
                * 100L

                >= larger
                * 80L;
    }
}