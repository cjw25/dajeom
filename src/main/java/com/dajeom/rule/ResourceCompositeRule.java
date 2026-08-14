package com.dajeom.rule;

import com.dajeom.model.TenGodPowerSummary;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.ResourceCompositePatternLevel;
import com.dajeom.type.ResourceCompositePatternType;
import com.dajeom.type.ResourceCompositeReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-6.
 *
 * 인성 복합구조 Rule.
 *
 * =================================================
 *
 * 점수 총합 방식이 아니다.
 *
 * Pattern별 구조조건을 각각 판단한다.
 *
 * =================================================
 *
 * 인성 family share:
 *
 * 15% 이상 -> 의미 있는 인성
 * 25% 이상 -> 강한 인성
 *
 * =================================================
 *
 * 정인/편인 중심:
 *
 * 양쪽이 모두 존재할 때
 * 한쪽이 상대방의 125% 이상이고
 * 자체 세력도 의미 있어야
 * STRONG 중심 Pattern으로 본다.
 *
 * =================================================
 *
 * 상대 family Context:
 *
 * 80% 이상 접근
 * -> MEANINGFUL 후보
 *
 * 상대방보다 강함
 * -> STRONG 후보
 *
 * =================================================
 *
 * 주의:
 *
 * 이 단계의 Context만으로
 *
 * 효신탈식
 * 상관패인
 * 살인상생
 * 관인상생
 * 재극인
 *
 * 을 최종 확정하지 않는다.
 */
@Component
public class ResourceCompositeRule {

    public static final int
            MEANINGFUL_RESOURCE_SHARE_BASIS_POINTS =
            1_500;

    public static final int
            STRONG_RESOURCE_SHARE_BASIS_POINTS =
            2_500;

    public static final int
            CENTER_DOMINANCE_PERCENT =
            125;

    public static final int
            CONTEXT_APPROACH_PERCENT =
            80;

    /*
     * =================================================
     * 신강·신약
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

    /*
     * =================================================
     * 인성 family
     * =================================================
     */

    public boolean meaningfulFamily(
            int resourceFamilyShareBasisPoints
    ) {

        return resourceFamilyShareBasisPoints
                >= MEANINGFUL_RESOURCE_SHARE_BASIS_POINTS;
    }

    public boolean strongFamily(
            int resourceFamilyShareBasisPoints
    ) {

        return resourceFamilyShareBasisPoints
                >= STRONG_RESOURCE_SHARE_BASIS_POINTS;
    }

    /*
     * =================================================
     * 중심성
     * =================================================
     */

    public boolean directResourceClearlyDominates(

            int directResourcePower,

            int indirectResourcePower

    ) {

        if (
                directResourcePower <= 0
        ) {

            return false;
        }

        if (
                indirectResourcePower <= 0
        ) {

            return true;
        }

        return directResourcePower
                * 100L

                >= indirectResourcePower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    public boolean indirectResourceClearlyDominates(

            int indirectResourcePower,

            int directResourcePower

    ) {

        if (
                indirectResourcePower <= 0
        ) {

            return false;
        }

        if (
                directResourcePower <= 0
        ) {

            return true;
        }

        return indirectResourcePower
                * 100L

                >= directResourcePower
                * (long) CENTER_DOMINANCE_PERCENT;
    }

    /*
     * =================================================
     * 상대 family
     * =================================================
     */

    public boolean approaches(

            int sourceFamilyPower,

            int targetFamilyPower

    ) {

        if (
                sourceFamilyPower <= 0
                        || targetFamilyPower <= 0
        ) {

            return false;
        }

        return sourceFamilyPower
                * 100L

                >= targetFamilyPower
                * (long) CONTEXT_APPROACH_PERCENT;
    }

    public boolean exceeds(

            int sourceFamilyPower,

            int targetFamilyPower

    ) {

        return sourceFamilyPower > 0
                && targetFamilyPower > 0
                && sourceFamilyPower
                > targetFamilyPower;
    }

    /*
     * =================================================
     * 인성 발달
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineResourceDevelopedLevel(

            int resourceFamilyShareBasisPoints,

            int totalOccurrenceCount,

            boolean directResourceVisible,

            boolean indirectResourceVisible

    ) {

        if (
                totalOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        resourceFamilyShareBasisPoints
                )
                        && totalOccurrenceCount >= 3
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        resourceFamilyShareBasisPoints
                )

                        || totalOccurrenceCount >= 3

                        || (
                        directResourceVisible
                                && indirectResourceVisible
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 정인 중심
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineDirectResourceCenteredLevel(

            TenGodPowerSummary directResourceSummary,

            TenGodPowerSummary indirectResourceSummary

    ) {

        Objects.requireNonNull(
                directResourceSummary,
                "정인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectResourceSummary,
                "편인 Summary가 필요합니다."
        );

        if (
                !directResourceSummary.present()
        ) {

            return null;
        }

        int directPower =
                directResourceSummary
                        .allocatedPower();

        int indirectPower =
                indirectResourceSummary
                        .allocatedPower();

        if (
                !indirectResourceSummary.present()
        ) {

            if (
                    directResourceSummary.strongSide()
            ) {

                return ResourceCompositePatternLevel
                        .STRONG;
            }

            if (
                    directResourceSummary.meaningful()
                            || directResourceSummary.visible()
                            || directResourceSummary.monthPresent()
            ) {

                return ResourceCompositePatternLevel
                        .MEANINGFUL;
            }

            return ResourceCompositePatternLevel
                    .REFERENCE;
        }

        if (
                directResourceClearlyDominates(
                        directPower,
                        indirectPower
                )
                        && directResourceSummary.meaningful()
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                directPower > indirectPower
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 편인 중심
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineIndirectResourceCenteredLevel(

            TenGodPowerSummary directResourceSummary,

            TenGodPowerSummary indirectResourceSummary

    ) {

        Objects.requireNonNull(
                directResourceSummary,
                "정인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectResourceSummary,
                "편인 Summary가 필요합니다."
        );

        if (
                !indirectResourceSummary.present()
        ) {

            return null;
        }

        int directPower =
                directResourceSummary
                        .allocatedPower();

        int indirectPower =
                indirectResourceSummary
                        .allocatedPower();

        if (
                !directResourceSummary.present()
        ) {

            if (
                    indirectResourceSummary.strongSide()
            ) {

                return ResourceCompositePatternLevel
                        .STRONG;
            }

            if (
                    indirectResourceSummary.meaningful()
                            || indirectResourceSummary.visible()
                            || indirectResourceSummary.monthPresent()
            ) {

                return ResourceCompositePatternLevel
                        .MEANINGFUL;
            }

            return ResourceCompositePatternLevel
                    .REFERENCE;
        }

        if (
                indirectResourceClearlyDominates(
                        indirectPower,
                        directPower
                )
                        && indirectResourceSummary.meaningful()
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                indirectPower > directPower
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return null;
    }

    /*
     * =================================================
     * 정인·편인 혼재
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineMixedResourceLevel(

            TenGodPowerSummary directResourceSummary,

            TenGodPowerSummary indirectResourceSummary

    ) {

        Objects.requireNonNull(
                directResourceSummary,
                "정인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectResourceSummary,
                "편인 Summary가 필요합니다."
        );

        if (
                !directResourceSummary.present()
                        || !indirectResourceSummary.present()
        ) {

            return null;
        }

        if (
                directResourceSummary.meaningful()
                        && indirectResourceSummary.meaningful()
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                directResourceSummary.meaningful()
                        || indirectResourceSummary.meaningful()
                        || directResourceSummary.visible()
                        || indirectResourceSummary.visible()
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 신약 인성 보조
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineWeakDayMasterResourceSupportLevel(

            DayMasterStrengthLevel strengthLevel,

            int resourceFamilyShareBasisPoints,

            int totalOccurrenceCount

    ) {

        if (
                !weakSide(
                        strengthLevel
                )
                        || totalOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        resourceFamilyShareBasisPoints
                )
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        resourceFamilyShareBasisPoints
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 신강 인성 과다
     * =================================================
     */

    public ResourceCompositePatternLevel
    determineStrongDayMasterExcessiveResourceLevel(

            DayMasterStrengthLevel strengthLevel,

            int resourceFamilyShareBasisPoints,

            int totalOccurrenceCount

    ) {

        if (
                !strongSide(
                        strengthLevel
                )
                        || totalOccurrenceCount <= 0
        ) {

            return null;
        }

        if (
                strongFamily(
                        resourceFamilyShareBasisPoints
                )
                        && totalOccurrenceCount >= 3
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                meaningfulFamily(
                        resourceFamilyShareBasisPoints
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 식상 제약 Context
     * =================================================
     *
     * 인성 -> 식상 제약.
     *
     * 하지만 편인-식신인지
     * 정인-상관인지 등을 아직 분리하지 않는다.
     */

    public ResourceCompositePatternLevel
    determineOutputConstraintContextLevel(

            int resourceFamilyPower,

            int outputFamilyPower

    ) {

        if (
                resourceFamilyPower <= 0
                        || outputFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceeds(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                approaches(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 관살 생인 Context
     * =================================================
     *
     * 관살 -> 인성 방향.
     */

    public ResourceCompositePatternLevel
    determineOfficerGenerationContextLevel(

            int resourceFamilyPower,

            int officerFamilyPower

    ) {

        if (
                resourceFamilyPower <= 0
                        || officerFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceeds(
                        officerFamilyPower,
                        resourceFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                approaches(
                        officerFamilyPower,
                        resourceFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * 재성 압박 Context
     * =================================================
     *
     * 재성 -> 인성 극.
     */

    public ResourceCompositePatternLevel
    determineWealthPressureContextLevel(

            int resourceFamilyPower,

            int wealthFamilyPower

    ) {

        if (
                resourceFamilyPower <= 0
                        || wealthFamilyPower <= 0
        ) {

            return null;
        }

        if (
                exceeds(
                        wealthFamilyPower,
                        resourceFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .STRONG;
        }

        if (
                approaches(
                        wealthFamilyPower,
                        resourceFamilyPower
                )
        ) {

            return ResourceCompositePatternLevel
                    .MEANINGFUL;
        }

        return ResourceCompositePatternLevel
                .REFERENCE;
    }

    /*
     * =================================================
     * Reason
     * =================================================
     */

    public List<ResourceCompositeReasonType>
    buildReasons(

            ResourceCompositePatternType patternType,

            TenGodPowerSummary directResourceSummary,

            TenGodPowerSummary indirectResourceSummary,

            DayMasterStrengthLevel strengthLevel,

            int resourceFamilyShareBasisPoints,

            int resourceFamilyPower,

            int outputFamilyPower,

            int officerFamilyPower,

            int wealthFamilyPower

    ) {

        Objects.requireNonNull(
                patternType,
                "인성 Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                directResourceSummary,
                "정인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                indirectResourceSummary,
                "편인 Summary가 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 수준이 필요합니다."
        );

        List<ResourceCompositeReasonType> reasons =
                new ArrayList<>();

        /*
         * =================================================
         * 정인
         * =================================================
         */

        if (
                directResourceSummary.present()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_RESOURCE_PRESENT
            );
        }

        if (
                directResourceSummary.visible()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_RESOURCE_VISIBLE
            );
        }

        if (
                directResourceSummary.monthPresent()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_RESOURCE_MONTH_PRESENT
            );
        }

        if (
                directResourceSummary.meaningful()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_RESOURCE_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 편인
         * =================================================
         */

        if (
                indirectResourceSummary.present()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .INDIRECT_RESOURCE_PRESENT
            );
        }

        if (
                indirectResourceSummary.visible()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .INDIRECT_RESOURCE_VISIBLE
            );
        }

        if (
                indirectResourceSummary.monthPresent()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .INDIRECT_RESOURCE_MONTH_PRESENT
            );
        }

        if (
                indirectResourceSummary.meaningful()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .INDIRECT_RESOURCE_MEANINGFUL_POWER
            );
        }

        /*
         * =================================================
         * 중심 우세
         * =================================================
         */

        if (
                directResourceSummary.allocatedPower()
                        > indirectResourceSummary.allocatedPower()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_RESOURCE_DOMINATES
            );

        } else if (
                indirectResourceSummary.allocatedPower()
                        > directResourceSummary.allocatedPower()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .INDIRECT_RESOURCE_DOMINATES
            );
        }

        /*
         * =================================================
         * 인성 전체
         * =================================================
         */

        if (
                meaningfulFamily(
                        resourceFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .RESOURCE_FAMILY_MEANINGFUL
            );
        }

        if (
                strongFamily(
                        resourceFamilyShareBasisPoints
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .RESOURCE_FAMILY_STRONG
            );
        }

        int totalOccurrenceCount =
                directResourceSummary.occurrenceCount()
                        + indirectResourceSummary.occurrenceCount();

        if (
                totalOccurrenceCount >= 2
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .MULTIPLE_RESOURCE_SOURCES
            );
        }

        if (
                directResourceSummary.present()
                        && indirectResourceSummary.present()
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DIRECT_AND_INDIRECT_RESOURCE_COEXIST
            );
        }

        /*
         * =================================================
         * 일간 강약
         * =================================================
         */

        if (
                weakSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DAY_MASTER_WEAK_SIDE
            );

        } else if (
                strongSide(
                        strengthLevel
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .DAY_MASTER_STRONG_SIDE
            );

        } else {

            reasons.add(
                    ResourceCompositeReasonType
                            .DAY_MASTER_BALANCED
            );
        }

        /*
         * =================================================
         * 식상
         * =================================================
         */

        if (
                outputFamilyPower > 0
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .OUTPUT_PRESENT
            );
        }

        if (
                approaches(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .RESOURCE_APPROACHES_OUTPUT_POWER
            );
        }

        if (
                exceeds(
                        resourceFamilyPower,
                        outputFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .RESOURCE_EXCEEDS_OUTPUT_POWER
            );
        }

        /*
         * =================================================
         * 관살
         * =================================================
         */

        if (
                officerFamilyPower > 0
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .OFFICER_PRESENT
            );
        }

        if (
                approaches(
                        officerFamilyPower,
                        resourceFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .OFFICER_APPROACHES_RESOURCE_POWER
            );
        }

        if (
                exceeds(
                        officerFamilyPower,
                        resourceFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .OFFICER_EXCEEDS_RESOURCE_POWER
            );
        }

        /*
         * =================================================
         * 재성
         * =================================================
         */

        if (
                wealthFamilyPower > 0
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .WEALTH_PRESENT
            );
        }

        if (
                approaches(
                        wealthFamilyPower,
                        resourceFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .WEALTH_APPROACHES_RESOURCE_POWER
            );
        }

        if (
                exceeds(
                        wealthFamilyPower,
                        resourceFamilyPower
                )
        ) {

            reasons.add(
                    ResourceCompositeReasonType
                            .WEALTH_EXCEEDS_RESOURCE_POWER
            );
        }

        /*
         * =================================================
         * Pattern
         * =================================================
         */

        switch (
                patternType
        ) {

            case RESOURCE_DEVELOPED ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .RESOURCE_DEVELOPED_PATTERN
                    );

            case DIRECT_RESOURCE_CENTERED ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .DIRECT_RESOURCE_CENTERED_PATTERN
                    );

            case INDIRECT_RESOURCE_CENTERED ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .INDIRECT_RESOURCE_CENTERED_PATTERN
                    );

            case MIXED_RESOURCE ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .MIXED_RESOURCE_PATTERN
                    );

            case WEAK_DAY_MASTER_RESOURCE_SUPPORT ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .WEAK_DAY_MASTER_RESOURCE_SUPPORT_PATTERN
                    );

            case STRONG_DAY_MASTER_EXCESSIVE_RESOURCE ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .STRONG_DAY_MASTER_EXCESSIVE_RESOURCE_PATTERN
                    );

            case OUTPUT_CONSTRAINT_CONTEXT ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .OUTPUT_CONSTRAINT_CONTEXT_PATTERN
                    );

            case OFFICER_GENERATION_CONTEXT ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .OFFICER_GENERATION_CONTEXT_PATTERN
                    );

            case WEALTH_PRESSURE_CONTEXT ->
                    reasons.add(
                            ResourceCompositeReasonType
                                    .WEALTH_PRESSURE_CONTEXT_PATTERN
                    );
        }

        return List.copyOf(
                reasons
        );
    }
}