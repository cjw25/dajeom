package com.dajeom.rule;

import com.dajeom.model.StructureAnalysis;
import com.dajeom.type.ClimateAdjustmentNeedLevel;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.PassageNeedLevel;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.UsefulGodMethodApplicability;
import com.dajeom.type.UsefulGodMethodReasonType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 7차-1.
 *
 * 현재 명식에 각 용신법을
 * 어느 정도 적용할지 결정한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 이 단계에서는 오행 후보를 만들지 않는다.
 *
 * 2.
 * 어떤 용신법이 중요한지만 결정한다.
 *
 * 3.
 * 여러 CORE 방법이 동시에 존재할 수 있다.
 *
 * 4.
 * 종격 후보가 실제 주격이면
 *    일반 억부법을 핵심 방법으로 사용하지 않는다.
 *
 * 5.
 * 조후·병약·통관은
 *    6차에서 계산한 필요도를 그대로 재사용한다.
 *
 * 6.
 * 격국도 5차 결과를 다시 계산하지 않는다.
 */
@Component
public class UsefulGodMethodRule {

    /*
     * =================================================
     * 억부용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determineBalancingApplicability(

            DayMasterStrengthLevel strengthLevel,

            boolean followStructureMode

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        /*
         * 종격 주격에서는
         * 일반적인 억부법으로
         * 원래의 종하는 방향을 되돌리면 안 된다.
         */
        if (
                followStructureMode
        ) {

            return UsefulGodMethodApplicability
                    .CONDITIONAL;
        }

        return switch (
                strengthLevel
                ) {

            case EXTREME_STRONG,
                 EXTREME_WEAK ->
                    UsefulGodMethodApplicability.CORE;

            case STRONG,
                 WEAK ->
                    UsefulGodMethodApplicability.CORE;

            case BALANCED_STRONG,
                 BALANCED_WEAK ->
                    UsefulGodMethodApplicability.ACTIVE;

            case BALANCED ->
                    UsefulGodMethodApplicability.CONDITIONAL;
        };
    }

    public List<UsefulGodMethodReasonType>
    balancingReasons(

            DayMasterStrengthLevel strengthLevel,

            boolean followStructureMode

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        List<UsefulGodMethodReasonType> reasons =
                new ArrayList<>();

        if (
                followStructureMode
        ) {

            reasons.add(
                    UsefulGodMethodReasonType
                            .FOLLOW_STRUCTURE_SUPPRESSES_NORMAL_BALANCING
            );

            return List.copyOf(
                    reasons
            );
        }

        switch (
                strengthLevel
        ) {

            case EXTREME_STRONG,
                 EXTREME_WEAK ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .EXTREME_DAY_MASTER_IMBALANCE
                    );

            case STRONG,
                 WEAK ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .CLEAR_DAY_MASTER_IMBALANCE
                    );

            case BALANCED_STRONG,
                 BALANCED_WEAK ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .SLIGHT_DAY_MASTER_IMBALANCE
                    );

            case BALANCED ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .BALANCED_DAY_MASTER
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * 조후용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determineClimateApplicability(
            ClimateAdjustmentNeedLevel need
    ) {

        Objects.requireNonNull(
                need,
                "조후 필요도가 필요합니다."
        );

        return switch (
                need
                ) {

            case CRITICAL,
                 HIGH ->
                    UsefulGodMethodApplicability.CORE;

            case MODERATE ->
                    UsefulGodMethodApplicability.ACTIVE;

            case LOW ->
                    UsefulGodMethodApplicability.CONDITIONAL;

            case NONE ->
                    UsefulGodMethodApplicability.INACTIVE;
        };
    }

    public List<UsefulGodMethodReasonType>
    climateReasons(
            ClimateAdjustmentNeedLevel need
    ) {

        Objects.requireNonNull(
                need,
                "조후 필요도가 필요합니다."
        );

        UsefulGodMethodReasonType reason =
                switch (
                        need
                        ) {

                    case CRITICAL ->
                            UsefulGodMethodReasonType
                                    .CRITICAL_CLIMATE_NEED;

                    case HIGH ->
                            UsefulGodMethodReasonType
                                    .HIGH_CLIMATE_NEED;

                    case MODERATE ->
                            UsefulGodMethodReasonType
                                    .MODERATE_CLIMATE_NEED;

                    case LOW ->
                            UsefulGodMethodReasonType
                                    .LOW_CLIMATE_NEED;

                    case NONE ->
                            UsefulGodMethodReasonType
                                    .NO_CLIMATE_NEED;
                };

        return List.of(
                reason
        );
    }

    /*
     * =================================================
     * 격국용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determineStructureApplicability(

            StructureAnalysis structure,

            boolean followStructureMode

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        /*
         * 종격이 실제 주격이면
         * 종격용신법에서 핵심 방향을 처리한다.
         *
         * 일반 격국용신법은
         * 보조 검토로 내린다.
         */
        if (
                followStructureMode
        ) {

            return UsefulGodMethodApplicability
                    .CONDITIONAL;
        }

        /*
         * 건록·양인 등의 특수격은
         * 일반 내격 공식은 아니지만
         * 격 구조 검토 자체는 필요하다.
         */
        if (
                structure.primaryIsSpecial()
        ) {

            return UsefulGodMethodApplicability
                    .ACTIVE;
        }

        StructureStatus status =
                structure.status();

        return switch (
                status
                ) {

            case ESTABLISHED ->
                    UsefulGodMethodApplicability.ACTIVE;

            case ESTABLISHED_WITH_STRAIN,
                 COMPROMISED ->
                    UsefulGodMethodApplicability.CORE;

            case UNSTABLE ->
                    UsefulGodMethodApplicability.CONDITIONAL;

            case SPECIAL_REVIEW ->
                    UsefulGodMethodApplicability.ACTIVE;
        };
    }

    public List<UsefulGodMethodReasonType>
    structureReasons(

            StructureAnalysis structure,

            boolean followStructureMode

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석이 필요합니다."
        );

        List<UsefulGodMethodReasonType> reasons =
                new ArrayList<>();

        if (
                followStructureMode
        ) {

            reasons.add(
                    UsefulGodMethodReasonType
                            .FOLLOW_STRUCTURE_HANDLED_SEPARATELY
            );

            return List.copyOf(
                    reasons
            );
        }

        if (
                structure.primaryIsSpecial()
        ) {

            reasons.add(
                    UsefulGodMethodReasonType
                            .SPECIAL_STRUCTURE_PRIMARY
            );

            return List.copyOf(
                    reasons
            );
        }

        switch (
                structure.status()
        ) {

            case ESTABLISHED ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .STANDARD_STRUCTURE_ESTABLISHED
                    );

            case ESTABLISHED_WITH_STRAIN ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .STRUCTURE_WITH_STRAIN
                    );

            case COMPROMISED ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .STRUCTURE_COMPROMISED
                    );

            case UNSTABLE ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .STRUCTURE_UNSTABLE
                    );

            case SPECIAL_REVIEW ->
                    reasons.add(
                            UsefulGodMethodReasonType
                                    .SPECIAL_STRUCTURE_PRIMARY
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * 병약용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determineRemedyApplicability(
            PathologySeverity severity
    ) {

        Objects.requireNonNull(
                severity,
                "병 구조 심각도가 필요합니다."
        );

        return switch (
                severity
                ) {

            case CRITICAL,
                 HIGH ->
                    UsefulGodMethodApplicability.CORE;

            case MODERATE ->
                    UsefulGodMethodApplicability.ACTIVE;

            case LOW ->
                    UsefulGodMethodApplicability.CONDITIONAL;

            case NONE ->
                    UsefulGodMethodApplicability.INACTIVE;
        };
    }

    public List<UsefulGodMethodReasonType>
    remedyReasons(
            PathologySeverity severity
    ) {

        Objects.requireNonNull(
                severity,
                "병 구조 심각도가 필요합니다."
        );

        UsefulGodMethodReasonType reason =
                switch (
                        severity
                        ) {

                    case CRITICAL ->
                            UsefulGodMethodReasonType
                                    .CRITICAL_PATHOLOGY;

                    case HIGH ->
                            UsefulGodMethodReasonType
                                    .HIGH_PATHOLOGY;

                    case MODERATE ->
                            UsefulGodMethodReasonType
                                    .MODERATE_PATHOLOGY;

                    case LOW ->
                            UsefulGodMethodReasonType
                                    .LOW_PATHOLOGY;

                    case NONE ->
                            UsefulGodMethodReasonType
                                    .NO_PATHOLOGY;
                };

        return List.of(
                reason
        );
    }

    /*
     * =================================================
     * 통관용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determinePassageApplicability(
            PassageNeedLevel need
    ) {

        Objects.requireNonNull(
                need,
                "통관 필요도가 필요합니다."
        );

        return switch (
                need
                ) {

            case CRITICAL,
                 HIGH ->
                    UsefulGodMethodApplicability.CORE;

            case MODERATE ->
                    UsefulGodMethodApplicability.ACTIVE;

            case LOW ->
                    UsefulGodMethodApplicability.CONDITIONAL;

            case NONE ->
                    UsefulGodMethodApplicability.INACTIVE;
        };
    }

    public List<UsefulGodMethodReasonType>
    passageReasons(
            PassageNeedLevel need
    ) {

        Objects.requireNonNull(
                need,
                "통관 필요도가 필요합니다."
        );

        UsefulGodMethodReasonType reason =
                switch (
                        need
                        ) {

                    case CRITICAL ->
                            UsefulGodMethodReasonType
                                    .CRITICAL_PASSAGE_NEED;

                    case HIGH ->
                            UsefulGodMethodReasonType
                                    .HIGH_PASSAGE_NEED;

                    case MODERATE ->
                            UsefulGodMethodReasonType
                                    .MODERATE_PASSAGE_NEED;

                    case LOW ->
                            UsefulGodMethodReasonType
                                    .LOW_PASSAGE_NEED;

                    case NONE ->
                            UsefulGodMethodReasonType
                                    .NO_PASSAGE_NEED;
                };

        return List.of(
                reason
        );
    }

    /*
     * =================================================
     * 종격용신법
     * =================================================
     */

    public UsefulGodMethodApplicability
    determineFollowApplicability(
            boolean followStructureMode
    ) {

        if (
                followStructureMode
        ) {

            return UsefulGodMethodApplicability
                    .CORE;
        }

        return UsefulGodMethodApplicability
                .INACTIVE;
    }

    public List<UsefulGodMethodReasonType>
    followReasons(
            boolean followStructureMode
    ) {

        if (
                followStructureMode
        ) {

            return List.of(
                    UsefulGodMethodReasonType
                            .FOLLOW_STRUCTURE_PRIMARY
            );
        }

        return List.of(
                UsefulGodMethodReasonType
                        .NO_FOLLOW_STRUCTURE_PRIMARY
        );
    }
}