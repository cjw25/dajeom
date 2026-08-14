package com.dajeom.rule;

import com.dajeom.model.TenGodCorePatternSignal;
import com.dajeom.model.TenGodCrossValidationContext;
import com.dajeom.model.TenGodCrossValidationEvidence;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.StructureStatus;
import com.dajeom.type.TenGodCrossValidationFactorType;
import com.dajeom.type.TenGodCrossValidationReasonType;
import com.dajeom.type.TenGodCrossValidationStatus;
import com.dajeom.type.TenGodCorePatternLevel;
import com.dajeom.type.TenGodCorePatternType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 10차-9.
 *
 * 핵심 십성 Pattern 교차검증 Rule.
 *
 * =================================================
 *
 * 중요 정책
 *
 * 1.
 * 10-8 Pattern은 절대 삭제하지 않는다.
 *
 * 2.
 * 격국·강약·용신·합화는 독립된 Evidence다.
 *
 * 3.
 * 서로 다른 방법론의 충돌을
 * 강제로 하나로 합치지 않는다.
 *
 * 4.
 * 합화에 의해 원기능이 감소하면
 * Pattern을 삭제하지 않고 WEAKENED 처리한다.
 *
 * 5.
 * 출생시간 미상은
 * SUPPORTED 최종 확정을 QUALIFIED로 제한한다.
 */
@Component
public class TenGodCrossValidationRule {

    /*
     * =================================================
     * 격국
     * =================================================
     */

    public TenGodCrossValidationEvidence
    evaluateStructure(
            StructureStatus status
    ) {

        Objects.requireNonNull(
                status,
                "격국 상태가 필요합니다."
        );

        return switch (
                status
                ) {

            case ESTABLISHED ->
                    evidence(
                            TenGodCrossValidationFactorType.STRUCTURE,
                            TenGodCrossValidationStatus.SUPPORTED,
                            TenGodCrossValidationReasonType
                                    .STRUCTURE_ESTABLISHED
                    );

            case ESTABLISHED_WITH_STRAIN ->
                    evidence(
                            TenGodCrossValidationFactorType.STRUCTURE,
                            TenGodCrossValidationStatus.QUALIFIED,
                            TenGodCrossValidationReasonType
                                    .STRUCTURE_ESTABLISHED_WITH_STRAIN
                    );

            case COMPROMISED ->
                    evidence(
                            TenGodCrossValidationFactorType.STRUCTURE,
                            TenGodCrossValidationStatus.WEAKENED,
                            TenGodCrossValidationReasonType
                                    .STRUCTURE_COMPROMISED
                    );

            case UNSTABLE ->
                    evidence(
                            TenGodCrossValidationFactorType.STRUCTURE,
                            TenGodCrossValidationStatus.WEAKENED,
                            TenGodCrossValidationReasonType
                                    .STRUCTURE_UNSTABLE
                    );

            case SPECIAL_REVIEW ->
                    evidence(
                            TenGodCrossValidationFactorType.STRUCTURE,
                            TenGodCrossValidationStatus.QUALIFIED,
                            TenGodCrossValidationReasonType
                                    .STRUCTURE_SPECIAL_REVIEW
                    );
        };
    }

    /*
     * =================================================
     * 신강·신약
     * =================================================
     */

    public TenGodCrossValidationEvidence
    evaluateStrength(

            TenGodCorePatternSignal signal,

            DayMasterStrengthLevel strengthLevel

    ) {

        Objects.requireNonNull(
                signal,
                "10-8 Pattern Signal이 필요합니다."
        );

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 수준이 필요합니다."
        );

        TenGodCorePatternType patternType =
                signal.patternType();

        List<TenGodCrossValidationReasonType> reasons =
                new ArrayList<>();

        TenGodCrossValidationStatus status;

        switch (
                patternType
        ) {

            /*
             * 재다신약은 신약 자체가
             * Pattern의 필수 전제다.
             */
            case WEALTH_HEAVY_WEAK_DAY_MASTER -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .CONFLICTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_CONFLICTS_PATTERN
                    );
                }
            }

            /*
             * 식신제살.
             *
             * 신약·극약인데 칠살이 식신보다
             * 훨씬 강한 경우 일반적인
             * 제살 해석은 제한한다.
             */
            case EATING_GOD_CONTROLS_SEVEN_KILLINGS -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                                && signal.secondaryPower()
                                > signal.primaryPower()
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );

                    if (
                            strengthLevel
                                    == DayMasterStrengthLevel
                                    .EXTREME_WEAK
                    ) {

                        reasons.add(
                                TenGodCrossValidationReasonType
                                        .EXTREME_WEAKNESS_REQUIRES_CAUTION
                        );
                    }

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );
                }
            }

            /*
             * 상관견관은 강약만으로
             * 성립 여부를 취소하지 않는다.
             */
            case HURTING_OFFICER_MEETS_DIRECT_OFFICER -> {

                status =
                        TenGodCrossValidationStatus
                                .QUALIFIED;

                reasons.add(
                        TenGodCrossValidationReasonType
                                .STRENGTH_REQUIRES_QUALIFICATION
                );
            }

            /*
             * 상관패인은 신약에서
             * 인성의 보조 의미가 더 분명해질 수 있다.
             */
            case HURTING_OFFICER_WITH_RESOURCE -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );
                }
            }

            /*
             * 편인도식은 일간 강약 하나로
             * 성립 여부를 결정하지 않는다.
             */
            case INDIRECT_RESOURCE_SEIZES_EATING_GOD -> {

                status =
                        TenGodCrossValidationStatus
                                .QUALIFIED;

                reasons.add(
                        TenGodCrossValidationReasonType
                                .STRENGTH_REQUIRES_QUALIFICATION
                );
            }

            /*
             * 재생관.
             *
             * 신약한데 재성과 관살이 강하면
             * 구조는 있어도 부담 해석이 함께 필요하다.
             */
            case WEALTH_GENERATES_DIRECT_OFFICER -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );
                }
            }

            /*
             * 관인상생 / 살인상생.
             *
             * 신약 방향에서는 인성 매개가
             * 구조적으로 더 의미 있을 수 있다.
             */
            case DIRECT_OFFICER_GENERATES_RESOURCE,
                 SEVEN_KILLINGS_GENERATES_RESOURCE -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );
                }
            }

            /*
             * 재극인.
             */
            case WEALTH_CONTROLS_RESOURCE -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );
                }
            }

            /*
             * 비겁쟁재.
             *
             * 신강이면 경쟁 구조가 더 직접적일 수 있다.
             * 신약이면 비겁이 일간 보조라는
             * 반대 기능도 함께 존재할 수 있다.
             */
            case COMPANION_CONTESTS_WEALTH -> {

                if (
                        strongSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRONG_DAY_MASTER_SUPPORTS_COMPANION_COMPETITION
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );

                    if (
                            weakSide(
                                    strengthLevel
                            )
                    ) {

                        reasons.add(
                                TenGodCrossValidationReasonType
                                        .WEAK_DAY_MASTER_CHANGES_COMPANION_FUNCTION
                        );
                    }
                }
            }

            /*
             * 관살 혼재.
             *
             * 약한 일간에서는 압박 구조의 의미가
             * 더 직접적으로 드러날 수 있지만,
             * 신강이라고 Pattern이 없어지는 것은 아니다.
             */
            case MIXED_DIRECT_OFFICER_AND_SEVEN_KILLINGS -> {

                if (
                        weakSide(
                                strengthLevel
                        )
                ) {

                    status =
                            TenGodCrossValidationStatus
                                    .SUPPORTED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_SUPPORTS_PATTERN
                    );

                } else {

                    status =
                            TenGodCrossValidationStatus
                                    .QUALIFIED;

                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .STRENGTH_REQUIRES_QUALIFICATION
                    );
                }
            }

            default ->
                    throw new IllegalStateException(
                            "처리되지 않은 핵심 십성 Pattern입니다: "
                                    + patternType
                    );
        }

        return new TenGodCrossValidationEvidence(
                TenGodCrossValidationFactorType
                        .DAY_MASTER_STRENGTH,
                status,
                reasons
        );
    }

    /*
     * =================================================
     * 용신
     * =================================================
     */

    public TenGodCrossValidationEvidence
    evaluateUsefulGod(

            TenGodCorePatternType patternType,

            TenGodCrossValidationContext context

    ) {

        Objects.requireNonNull(
                patternType,
                "Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                context,
                "교차검증 Context가 필요합니다."
        );

        boolean supports =
                context
                        .usefulGodSupports(
                                patternType
                        );

        boolean conflicts =
                context
                        .usefulGodConflicts(
                                patternType
                        );

        if (
                supports
                        && conflicts
        ) {

            return evidence(
                    TenGodCrossValidationFactorType.USEFUL_GOD,
                    TenGodCrossValidationStatus.CONFLICTED,
                    TenGodCrossValidationReasonType
                            .USEFUL_GOD_SUPPORTS_PATTERN,
                    TenGodCrossValidationReasonType
                            .USEFUL_GOD_CONFLICTS_PATTERN,
                    TenGodCrossValidationReasonType
                            .USEFUL_GOD_METHOD_DISAGREEMENT
            );
        }

        if (
                conflicts
        ) {

            return evidence(
                    TenGodCrossValidationFactorType.USEFUL_GOD,
                    TenGodCrossValidationStatus.CONFLICTED,
                    TenGodCrossValidationReasonType
                            .USEFUL_GOD_CONFLICTS_PATTERN
            );
        }

        if (
                supports
        ) {

            return evidence(
                    TenGodCrossValidationFactorType.USEFUL_GOD,
                    TenGodCrossValidationStatus.SUPPORTED,
                    TenGodCrossValidationReasonType
                            .USEFUL_GOD_SUPPORTS_PATTERN
            );
        }

        return evidence(
                TenGodCrossValidationFactorType.USEFUL_GOD,
                TenGodCrossValidationStatus.QUALIFIED,
                TenGodCrossValidationReasonType
                        .USEFUL_GOD_NEUTRAL
        );
    }

    /*
     * =================================================
     * 합화
     * =================================================
     */

    public TenGodCrossValidationEvidence
    evaluateTransformation(

            TenGodCorePatternType patternType,

            TenGodCrossValidationContext context

    ) {

        Objects.requireNonNull(
                patternType,
                "Pattern 종류가 필요합니다."
        );

        Objects.requireNonNull(
                context,
                "교차검증 Context가 필요합니다."
        );

        boolean supports =
                context
                        .transformationSupports(
                                patternType
                        );

        boolean weakens =
                context
                        .transformationWeakens(
                                patternType
                        );

        boolean conflicts =
                context
                        .transformationConflicts(
                                patternType
                        );

        List<TenGodCrossValidationReasonType> reasons =
                new ArrayList<>();

        if (
                supports
        ) {

            reasons.add(
                    TenGodCrossValidationReasonType
                            .TRANSFORMATION_SUPPORTS_PATTERN
            );
        }

        if (
                weakens
        ) {

            reasons.add(
                    TenGodCrossValidationReasonType
                            .TRANSFORMATION_WEAKENS_PATTERN
            );
        }

        if (
                conflicts
        ) {

            reasons.add(
                    TenGodCrossValidationReasonType
                            .TRANSFORMATION_CONFLICTS_PATTERN
            );
        }

        if (
                context.transformationMixed(
                        patternType
                )
        ) {

            reasons.add(
                    TenGodCrossValidationReasonType
                            .TRANSFORMATION_MIXED_EFFECT
            );
        }

        /*
         * Pattern의 핵심 전제가 크게 흔들리는 경우
         * 가장 보수적으로 CONFLICTED.
         */
        if (
                conflicts
        ) {

            return new TenGodCrossValidationEvidence(
                    TenGodCrossValidationFactorType
                            .TRANSFORMATION,
                    TenGodCrossValidationStatus
                            .CONFLICTED,
                    reasons
            );
        }

        /*
         * 원기능 감소가 있다면
         * Pattern 자체는 남기되 WEAKENED.
         */
        if (
                weakens
        ) {

            return new TenGodCrossValidationEvidence(
                    TenGodCrossValidationFactorType
                            .TRANSFORMATION,
                    TenGodCrossValidationStatus
                            .WEAKENED,
                    reasons
            );
        }

        if (
                supports
        ) {

            return new TenGodCrossValidationEvidence(
                    TenGodCrossValidationFactorType
                            .TRANSFORMATION,
                    TenGodCrossValidationStatus
                            .SUPPORTED,
                    reasons
            );
        }

        return evidence(
                TenGodCrossValidationFactorType.TRANSFORMATION,
                TenGodCrossValidationStatus.QUALIFIED,
                TenGodCrossValidationReasonType
                        .TRANSFORMATION_NEUTRAL
        );
    }

    /*
     * =================================================
     * 최종 상태
     * =================================================
     */

    public TenGodCrossValidationStatus
    determineFinalStatus(

            TenGodCorePatternLevel coreLevel,

            List<TenGodCrossValidationEvidence> evidences,

            boolean completeChart

    ) {

        Objects.requireNonNull(
                coreLevel,
                "10-8 Pattern 수준이 필요합니다."
        );

        Objects.requireNonNull(
                evidences,
                "교차검증 Evidence가 필요합니다."
        );

        if (
                evidences.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "최종 상태를 판정할 Evidence가 없습니다."
            );
        }

        /*
         * =================================================
         * 1. 명확한 충돌
         * =================================================
         */

        boolean conflicted =
                evidences
                        .stream()
                        .anyMatch(
                                TenGodCrossValidationEvidence
                                        ::conflicted
                        );

        if (
                conflicted
        ) {

            return TenGodCrossValidationStatus
                    .CONFLICTED;
        }

        /*
         * =================================================
         * 2. 기능 약화
         * =================================================
         */

        boolean weakened =
                evidences
                        .stream()
                        .anyMatch(
                                TenGodCrossValidationEvidence
                                        ::weakened
                        );

        if (
                weakened
        ) {

            return TenGodCrossValidationStatus
                    .WEAKENED;
        }

        /*
         * =================================================
         * 3. 다른 분석축의 지지 수
         * =================================================
         */

        long supportCount =
                evidences
                        .stream()
                        .filter(
                                TenGodCrossValidationEvidence
                                        ::supported
                        )
                        .count();

        TenGodCrossValidationStatus candidate;

        if (
                coreLevel
                        == TenGodCorePatternLevel.STRONG
                        && supportCount >= 1
        ) {

            candidate =
                    TenGodCrossValidationStatus
                            .SUPPORTED;

        } else if (
                coreLevel
                        == TenGodCorePatternLevel.MEANINGFUL
                        && supportCount >= 2
        ) {

            candidate =
                    TenGodCrossValidationStatus
                            .SUPPORTED;

        } else {

            /*
             * 10-8이 REFERENCE이면
             * 다른 축이 강하다고 해서
             * 억지로 SUPPORTED까지 끌어올리지 않는다.
             */
            candidate =
                    TenGodCrossValidationStatus
                            .QUALIFIED;
        }

        /*
         * =================================================
         * 4. 시간 미상
         * =================================================
         *
         * 시주 없는 명식은
         * SUPPORTED 최종 확정을 보수적으로 제한.
         */

        if (
                !completeChart
                        && candidate.supported()
        ) {

            return TenGodCrossValidationStatus
                    .QUALIFIED;
        }

        return candidate;
    }

    /*
     * =================================================
     * 최종 Reason
     * =================================================
     */

    public List<TenGodCrossValidationReasonType>
    buildFinalReasons(

            TenGodCorePatternLevel coreLevel,

            TenGodCrossValidationStatus finalStatus,

            boolean completeChart

    ) {

        Objects.requireNonNull(
                coreLevel,
                "10-8 Pattern 수준이 필요합니다."
        );

        Objects.requireNonNull(
                finalStatus,
                "최종 상태가 필요합니다."
        );

        List<TenGodCrossValidationReasonType> reasons =
                new ArrayList<>();

        switch (
                coreLevel
        ) {

            case REFERENCE ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .CORE_PATTERN_REFERENCE
                    );

            case MEANINGFUL ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .CORE_PATTERN_MEANINGFUL
                    );

            case STRONG ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .CORE_PATTERN_STRONG
                    );
        }

        if (
                !completeChart
        ) {

            reasons.add(
                    TenGodCrossValidationReasonType
                            .INCOMPLETE_CHART_REQUIRES_QUALIFICATION
            );
        }

        switch (
                finalStatus
        ) {

            case SUPPORTED ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .FINAL_SUPPORTED
                    );

            case QUALIFIED ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .FINAL_QUALIFIED
                    );

            case WEAKENED ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .FINAL_WEAKENED
                    );

            case CONFLICTED ->
                    reasons.add(
                            TenGodCrossValidationReasonType
                                    .FINAL_CONFLICTED
                    );
        }

        return List.copyOf(
                reasons
        );
    }

    /*
     * =================================================
     * 강약 Helper
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

    private TenGodCrossValidationEvidence evidence(

            TenGodCrossValidationFactorType factorType,

            TenGodCrossValidationStatus status,

            TenGodCrossValidationReasonType... reasons

    ) {

        return new TenGodCrossValidationEvidence(
                factorType,
                status,
                List.of(
                        reasons
                )
        );
    }
}