package com.dajeom.rule;

import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.OutputCompositePatternLevel;
import com.dajeom.type.OutputCompositePatternType;
import com.dajeom.type.OutputCompositeReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-3.
 *
 * 식상 복합구조 Rule.
 *
 * =================================================
 *
 * 점수 합산 방식이 아니다.
 *
 * 각 Pattern마다 독립적인
 * 구조 조건을 사용한다.
 *
 * =================================================
 *
 * 식상 family share 기준:
 *
 * 15% 이상 -> 의미 있는 식상
 * 25% 이상 -> 강한 식상
 *
 * =================================================
 *
 * 중심 십성 판정:
 *
 * 식신과 상관이 모두 존재할 경우
 * 한쪽이 상대의 125% 이상이고
 * 자체 세력도 의미 있어야
 * STRONG 중심 구조로 본다.
 *
 * =================================================
 *
 * 인성 제약:
 *
 * 인성이 식상 전체 세력의
 * 80% 이상에 접근할 때부터
 * 유의미한 제약 후보로 본다.
 *
 * =================================================
 *
 * 주의:
 *
 * RESOURCE_CONSTRAINED_OUTPUT은
 * 가족 단위 구조다.
 *
 * 이것만으로 효신탈식/편인도식 등을
 * 확정하지 않는다.
 */
@Component
public class OutputCompositeRule {

    public static final int
            MEANINGFUL_OUTPUT_SHARE_BASIS_POINTS =
            1_500;

    public static final int
            STRONG_OUTPUT_SHARE_BASIS_POINTS =
            2_500;

    public static final int
            CENTER_DOMINANCE_PERCENT =
            125;

    public static final int
            RESOURCE_CONSTRAINT_APPROACH_PERCENT =
            80;

    /*
     * =================================================
     * 일간 강약
     * =================================================
     */

    public boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        Objects.requireNonNull(
                level,
                "일간 강약 수준이 필요합니다."
        );

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    public boolean strongSide(
            DayMasterStrengthLevel level
    ) {

        Objects.requireNonNull(
                level,
                "일간 강약 수준이 필요합니다."
        );

        return level
                == DayMasterStrengthLevel.BALANCED_STRONG

                || level
                == DayMasterStrengthLevel.STRONG

                || level
                == DayMasterStrengthLevel.EXTREME_STRONG;
    }

    public boolean balanced(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED;
    }

    /*
     * =================================================
     * 식상 전체
     * =================================================
     */

    public boolean meaningfulFamily(
            int outputFamilyShareBasisPoints
    ) {

        return outputFamilyShareBasisPoints
                >= MEANINGFUL_OUTPUT_SHARE_BASIS_POINTS;
    }

    public boolean strongFamily(
            int outputFamilyShareBasisPoints
    ) {

        return outputFamilyShareBasisPoints
                >= STRONG_OUTPUT_SHARE_BASIS_POINTS;
    }

    /*
     * =================================================
     * 식신/상관 우세
     * =================================================
     */

    public boolean eatingGodClearlyDominates(

            int eatingGodPower,

            int hurtingOfficerPower

    ) {

        if (
                eatingGodPower <= 0
        ) {

            return false;
        }

        if (
                hurtingOfficerPower <= 0
        ) {

            return true;
        }

        return eatingGodPower
                * 100L

                >= hurtingOfficerPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    public boolean hurtingOfficerClearlyDominates(

            int hurtingOfficerPower,

            int eatingGodPower

    ) {

        if (
                hurtingOfficerPower <= 0
        ) {

            return false;
        }

        if (
                eatingGodPower <= 0
        ) {

            return true;
        }

        return hurtingOfficerPower
                * 100L

                >= eatingGodPower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    /*
     * =================================================
     * 인성 제약
     * =================================================
     */

    public boolean resourceApproachesOutput(

            int resourceFamilyPower,

            int outputFamilyPower

    ) {

        if (
                resourceFamilyPower <= 0
                        || outputFamilyPower <= 0
        ) {

            return false;
        }

        return resourceFamilyPower
                * 100L

                >= outputFamilyPower
                * (long) RESOURCE_CONSTRAINT_APPROACH_PERCENT;
    }

    public boolean resourceExceedsOutput(

            int resourceFamilyPower,

            int outputFamilyPower

    ) {

        return resourceFamilyPower > 0
                && outputFamilyPower > 0
                && resourceFamilyPower
                > outputFamilyPower;
    }

    /*
     * =================================================
     * 식상 발달
     * =================================================
     */

    public OutputCompositePatternLevel
    determineOutputDevelopedLevel(

            int outputFamilyShareBasisPoints,

            int totalOutputOccurrenceCount,

            boolean eatingGodVisible,

            boolean hurtingOfficerVisible

    ) {

        if (
                totalOutputOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        outputFamilyShareBasisPoints
                )
                        && totalOutputOccurrenceCount >= 3
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        outputFamilyShareBasisPoints
                )

                        || totalOutputOccurrenceCount >= 3

                        || (
                        eatingGodVisible
                                && hurtingOfficerVisible
                )
        ) {

            return OutputCompositePatternLevel
                    .MEANINGFUL;
        }

        return OutputCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 식신 중심
     * =================================================
     */

    public OutputCompositePatternLevel
    determineEatingGodCenteredLevel(

            TenGodPowerSummary eatingGodSummary,

            TenGodPowerSummary hurtingOfficerSummary

    ) {

        Objects.requireNonNull(
                eatingGodSummary,
                "식신 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                hurtingOfficerSummary,
                "상관 Summary가 필요합니다."
        );

        if (
                !eatingGodSummary.present()
        ) {

            return null;
        }

        int eatingGodPower =
                eatingGodSummary.allocatedPower();

        int hurtingOfficerPower =
                hurtingOfficerSummary.allocatedPower();

        if (
                !hurtingOfficerSummary.present()
        ) {

            if (
                    eatingGodSummary.strongSide()
            ) {

                return OutputCompositePatternLevel
                        .STRONG;
            }

            if (
                    eatingGodSummary.meaningful()
                            || eatingGodSummary.visible()
                            || eatingGodSummary.monthPresent()
            ) {

                return OutputCompositePatternLevel
                        .MEANINGFUL;
            }

            return OutputCompositePatternLevel
                    .REFERENCE;
        }

        if (
                eatingGodClearlyDominates(
                        eatingGodPower,
                        hurtingOfficerPower
                )
                        && eatingGodSummary.meaningful()
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        if (
                eatingGodPower
                        > hurtingOfficerPower
        ) {

            return OutputCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 상관 중심
     * =================================================
     */

    public OutputCompositePatternLevel
    determineHurtingOfficerCenteredLevel(

            TenGodPowerSummary eatingGodSummary,

            TenGodPowerSummary hurtingOfficerSummary

    ) {

        Objects.requireNonNull(
                eatingGodSummary,
                "식신 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                hurtingOfficerSummary,
                "상관 Summary가 필요합니다."
        );

        if (
                !hurtingOfficerSummary.present()
        ) {

            return null;
        }

        int eatingGodPower =
                eatingGodSummary.allocatedPower();

        int hurtingOfficerPower =
                hurtingOfficerSummary.allocatedPower();

        if (
                !eatingGodSummary.present()
        ) {

            if (
                    hurtingOfficerSummary.strongSide()
            ) {

                return OutputCompositePatternLevel
                        .STRONG;
            }

            if (
                    hurtingOfficerSummary.meaningful()
                            || hurtingOfficerSummary.visible()
                            || hurtingOfficerSummary.monthPresent()
            ) {

                return OutputCompositePatternLevel
                        .MEANINGFUL;
            }

            return OutputCompositePatternLevel
                    .REFERENCE;
        }

        if (
                hurtingOfficerClearlyDominates(
                        hurtingOfficerPower,
                        eatingGodPower
                )
                        && hurtingOfficerSummary.meaningful()
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        if (
                hurtingOfficerPower
                        > eatingGodPower
        ) {

            return OutputCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 식신·상관 혼재
     * =================================================
     */

    public OutputCompositePatternLevel
    determineMixedOutputLevel(

            TenGodPowerSummary eatingGodSummary,

            TenGodPowerSummary hurtingOfficerSummary

    ) {

        Objects.requireNonNull(
                eatingGodSummary,
                "식신 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                hurtingOfficerSummary,
                "상관 Summary가 필요합니다."
        );

        if (
                !eatingGodSummary.present()
                        || !hurtingOfficerSummary.present()
        ) {

            return null;
        }

        if (
                eatingGodSummary.meaningful()
                        && hurtingOfficerSummary.meaningful()
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        if (
                eatingGodSummary.meaningful()
                        || hurtingOfficerSummary.meaningful()
                        || eatingGodSummary.visible()
                        || hurtingOfficerSummary.visible()
        ) {

            return OutputCompositePatternLevel
                    .MEANINGFUL;
        }

        return OutputCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 신약 식상 과설
     * =================================================
     */

    public OutputCompositePatternLevel
    determineWeakDayMasterExcessiveDrainLevel(

            DayMasterStrengthLevel strengthLevel,

            int outputFamilyShareBasisPoints,

            int totalOutputOccurrenceCount

    ) {

        if (
                !weakSide(
                        strengthLevel
                )
                        || totalOutputOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        outputFamilyShareBasisPoints
                )
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        outputFamilyShareBasisPoints
                )
        ) {

            return OutputCompositePatternLevel
                    .MEANINGFUL;
        }

        return OutputCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 인성 제약
     * =================================================
     */

    public OutputCompositePatternLevel
    determineResourceConstrainedLevel(

            int outputFamilyPower,

            int resourceFamilyPower,

            int outputFamilyShareBasisPoints

    ) {

        if (
                outputFamilyPower <= 0
                        || resourceFamilyPower <= 0
        ) {

            return null;
        }

        /*
         * 인성이 식상의 80%에도 미치지 못하면
         * 이번 단계에서는 유의미한 제약 Pattern을
         * 만들지 않는다.
         */
        if (
                !resourceApproachesOutput(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            return null;
        }

        if (
                resourceExceedsOutput(
                        resourceFamilyPower,
                        outputFamilyPower
                )
                        && meaningfulFamily(
                        outputFamilyShareBasisPoints
                )
        ) {

            return OutputCompositePatternLevel
                    .STRONG;
        }

        return OutputCompositePatternLevel
                .MEANINGFUL;
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    public List<OutputCompositeReasonType>
    buildReasons(

            OutputCompositePatternType patternType,

            TenGodPowerSummary eatingGodSummary,

            TenGodPowerSummary hurtingOfficerSummary,

            DayMasterStrengthLevel strengthLevel,

            int outputFamilyShareBasisPoints,

            int outputFamilyPower,

            int resourceFamilyPower,

            int officerFamilyPower

    ) {

        Objects.requireNonNull(
                patternType,
                "식상 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                eatingGodSummary,
                "식신 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                hurtingOfficerSummary,
                "상관 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 수준이 필요합니다."
        );

        List<OutputCompositeReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 식신
         * =================================================
         */

        if (
                eatingGodSummary.present()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_PRESENT
            );
        }

        if (
                eatingGodSummary.visible()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_VISIBLE
            );
        }

        if (
                eatingGodSummary.monthPresent()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_MONTH_PRESENT
            );
        }

        if (
                eatingGodSummary.meaningful()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 상관
         * =================================================
         */

        if (
                hurtingOfficerSummary.present()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .HURTING_OFFICER_PRESENT
            );
        }

        if (
                hurtingOfficerSummary.visible()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .HURTING_OFFICER_VISIBLE
            );
        }

        if (
                hurtingOfficerSummary.monthPresent()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .HURTING_OFFICER_MONTH_PRESENT
            );
        }

        if (
                hurtingOfficerSummary.meaningful()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .HURTING_OFFICER_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 중심
         * =================================================
         */

        if (
                eatingGodSummary.allocatedPower()
                        > hurtingOfficerSummary.allocatedPower()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_DOMINATES_OUTPUT
            );

        } else if (
                hurtingOfficerSummary.allocatedPower()
                        > eatingGodSummary.allocatedPower()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .HURTING_OFFICER_DOMINATES_OUTPUT
            );
        }

        /*
         * =================================================
         * 식상 전체
         * =================================================
         */

        if (
                meaningfulFamily(
                        outputFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .OUTPUT_FAMILY_MEANINGFUL
            );
        }

        if (
                strongFamily(
                        outputFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .OUTPUT_FAMILY_STRONG
            );
        }

        int totalOccurrenceCount =
                eatingGodSummary.occurrenceCount()
                        + hurtingOfficerSummary.occurrenceCount();

        if (
                totalOccurrenceCount >= 2
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .MULTIPLE_OUTPUT_SOURCES
            );
        }

        if (
                eatingGodSummary.present()
                        && hurtingOfficerSummary.present()
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .EATING_GOD_AND_HURTING_OFFICER_COEXIST
            );
        }

        /*
         * =================================================
         * 신강·신약
         * =================================================
         */

        if (
                weakSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .DAY_MASTER_WEAK_SIDE
            );

        } else if (
                strongSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .DAY_MASTER_STRONG_SIDE
            );

        } else {

            reasons.add(
                    OutputCompositeReasonType
                            .DAY_MASTER_BALANCED
            );
        }

        /*
         * =================================================
         * 인성
         * =================================================
         */

        if (
                resourceFamilyPower > 0
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .RESOURCE_PRESENT
            );
        }

        if (
                resourceApproachesOutput(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .RESOURCE_APPROACHES_OUTPUT_POWER
            );
        }

        if (
                resourceExceedsOutput(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .RESOURCE_EXCEEDS_OUTPUT_POWER
            );
        }

        /*
         * =================================================
         * 관살
         * =================================================
         *
         * 여기서는 존재 Context만 보존.
         */

        if (
                officerFamilyPower > 0
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .OFFICER_PRESENT
            );
        }

        if (
                officerFamilyPower > 0
                        && outputFamilyPower > 0
        ) {

            reasons.add(
                    OutputCompositeReasonType
                            .OFFICER_AND_OUTPUT_COEXIST
            );
        }

        /*
         * =================================================
         * 최종 Pattern
         * =================================================
         */

        switch (
                patternType
        ) {

            case OUTPUT_DEVELOPED ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .OUTPUT_DEVELOPED_PATTERN
                    );

            case EATING_GOD_CENTERED ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .EATING_GOD_CENTERED_PATTERN
                    );

            case HURTING_OFFICER_CENTERED ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .HURTING_OFFICER_CENTERED_PATTERN
                    );

            case MIXED_OUTPUT ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .MIXED_OUTPUT_PATTERN
                    );

            case WEAK_DAY_MASTER_EXCESSIVE_DRAIN ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .WEAK_DAY_MASTER_EXCESSIVE_DRAIN_PATTERN
                    );

            case RESOURCE_CONSTRAINED_OUTPUT ->
                    reasons.add(
                            OutputCompositeReasonType
                                    .RESOURCE_CONSTRAINED_OUTPUT_PATTERN
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}