package com.dajeom.rule;

import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.PathologyEvidence;
import com.dajeom.type.BalancingUsefulGodCandidateRole;
import com.dajeom.type.BalancingUsefulGodReasonType;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.DayMasterStrengthLevel;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;
import com.dajeom.type.UsefulGodMethodApplicability;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 정밀화 7차-2.
 *
 * 억부용신 후보 선정 Rule.
 *
 * =================================================
 *
 * 핵심 원칙
 *
 * 일반 신약:
 *
 * RESOURCE
 * COMPANION
 *
 * 방향을 검토한다.
 *
 * 하지만:
 *
 * 식상 설기 과다
 * 관살 압박
 * -> RESOURCE 우선 근거
 *
 * 재성 부담
 * -> COMPANION 우선 근거
 *
 * =================================================
 *
 * 일반 신강:
 *
 * OUTPUT
 * WEALTH
 * OFFICER
 *
 * 방향을 검토한다.
 *
 * 생조 과다 병목이 존재하면
 * OUTPUT을 우선 검토한다.
 *
 * 별도 병목이 없으면
 * 현재 세력이 상대적으로 적은
 * 설기·소모·극제 역할을 우선 검토한다.
 *
 * =================================================
 *
 * 종격:
 *
 * 일반 억부 방향 자체는 기록할 수 있으나
 * PRIMARY/SECONDARY로 선택하지 않는다.
 *
 * 실제 종격용신은 7-7의 책임이다.
 *
 * =================================================
 *
 * 현재 세력이 VERY_HIGH인 오행은
 * 억부 방향에 맞더라도
 * 추가 강화 여부를 조건부로 남긴다.
 *
 * =================================================
 *
 * 최종 용신 결정은 7-8의 책임이다.
 */
@Component
public class BalancingUsefulGodRule {

    public DayMasterElementRole
    determineDayMasterRole(

            FiveElement dayMasterElement,

            FiveElement targetElement

    ) {

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetElement,
                "대상 오행이 필요합니다."
        );

        if (
                dayMasterElement == targetElement
        ) {

            return DayMasterElementRole
                    .COMPANION;
        }

        if (
                produces(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .RESOURCE;
        }

        if (
                produces(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .OUTPUT;
        }

        if (
                controls(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .WEALTH;
        }

        if (
                controls(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .OFFICER;
        }

        throw new IllegalStateException(
                "일간과 대상 오행의 관계를 결정할 수 없습니다."
        );
    }

    /**
     * 현재 신강신약 방향에서
     * 해당 십성군이 억부 방향과 일치하는지.
     */
    public boolean strengthDirectionMatch(

            DayMasterStrengthLevel strengthLevel,

            DayMasterElementRole role

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        if (
                weakSide(
                        strengthLevel
                )
        ) {

            return role
                    == DayMasterElementRole.COMPANION

                    || role
                    == DayMasterElementRole.RESOURCE;
        }

        if (
                strongSide(
                        strengthLevel
                )
        ) {

            return role
                    == DayMasterElementRole.OUTPUT

                    || role
                    == DayMasterElementRole.WEALTH

                    || role
                    == DayMasterElementRole.OFFICER;
        }

        return false;
    }

    /**
     * 실제 병 구조를 직접 완화하는
     * 억부 역할인지.
     */
    public boolean directPathologyMatch(

            DayMasterElementRole role,

            PathologyAnalysis pathology

    ) {

        Objects.requireNonNull(
                role,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        if (
                role == DayMasterElementRole.RESOURCE
        ) {

            return pathology.hasPattern(
                    PathologyPatternType
                            .OUTPUT_DRAIN_OVERLOAD
            )
                    || pathology.hasPattern(
                    PathologyPatternType
                            .OFFICER_CONTROL_OVERLOAD
            );
        }

        if (
                role == DayMasterElementRole.COMPANION
        ) {

            return pathology.hasPattern(
                    PathologyPatternType
                            .WEALTH_EXERTION_OVERLOAD
            );
        }

        if (
                role == DayMasterElementRole.OUTPUT
        ) {

            return pathology.hasPattern(
                    PathologyPatternType
                            .SUPPORT_OVERCONCENTRATION
            );
        }

        return false;
    }

    /**
     * 현재 명식에서 억부용신법이
     * 우선적으로 볼 역할을 결정한다.
     *
     * 여러 실제 병목이 동시에 존재하면
     * 두 역할 이상이 preferred가 될 수 있다.
     *
     * 아무 병목이 명확하지 않은 경우에는
     * 현재 실제 세력을 이용해
     * 하나의 기본 우선 역할을 선택한다.
     */
    public Set<DayMasterElementRole>
    determinePreferredRoles(

            DayMasterStrengthLevel strengthLevel,

            PathologyAnalysis pathology,

            int companionShareBasisPoints,

            int resourceShareBasisPoints,

            int outputShareBasisPoints,

            int wealthShareBasisPoints,

            int officerShareBasisPoints

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        validateShare(
                companionShareBasisPoints
        );

        validateShare(
                resourceShareBasisPoints
        );

        validateShare(
                outputShareBasisPoints
        );

        validateShare(
                wealthShareBasisPoints
        );

        validateShare(
                officerShareBasisPoints
        );

        EnumSet<DayMasterElementRole> preferred =
                EnumSet.noneOf(
                        DayMasterElementRole.class
                );

        /*
         * ================================================
         * 신약
         * ================================================
         */
        if (
                weakSide(
                        strengthLevel
                )
        ) {

            boolean outputProblem =
                    pathology.hasPattern(
                            PathologyPatternType
                                    .OUTPUT_DRAIN_OVERLOAD
                    );

            boolean officerProblem =
                    pathology.hasPattern(
                            PathologyPatternType
                                    .OFFICER_CONTROL_OVERLOAD
                    );

            boolean wealthProblem =
                    pathology.hasPattern(
                            PathologyPatternType
                                    .WEALTH_EXERTION_OVERLOAD
                    );

            if (
                    outputProblem
                            || officerProblem
            ) {

                preferred.add(
                        DayMasterElementRole.RESOURCE
                );
            }

            if (
                    wealthProblem
            ) {

                preferred.add(
                        DayMasterElementRole.COMPANION
                );
            }

            /*
             * 별도 병목이 없다면
             * 인성·비겁 가운데 현재 더 부족한 쪽을
             * 기본 우선 역할로 둔다.
             *
             * 완전히 같다면 RESOURCE를 우선한다.
             */
            if (
                    preferred.isEmpty()
            ) {

                if (
                        resourceShareBasisPoints
                                <= companionShareBasisPoints
                ) {

                    preferred.add(
                            DayMasterElementRole.RESOURCE
                    );

                } else {

                    preferred.add(
                            DayMasterElementRole.COMPANION
                    );
                }
            }

            return preferred;
        }

        /*
         * ================================================
         * 신강
         * ================================================
         */
        if (
                strongSide(
                        strengthLevel
                )
        ) {

            if (
                    pathology.hasPattern(
                            PathologyPatternType
                                    .SUPPORT_OVERCONCENTRATION
                    )
            ) {

                /*
                 * 강한 비겁·인성 세력을
                 * 가장 직접적으로 빼내는
                 * 식상 설기 방향을 우선.
                 */
                preferred.add(
                        DayMasterElementRole.OUTPUT
                );

                return preferred;
            }

            /*
             * 별도 생조과다 병목이 없다면
             * 식상·재성·관살 가운데
             * 현재 가장 적은 역할을 우선 검토한다.
             *
             * 동률 우선순위:
             *
             * OUTPUT
             * -> WEALTH
             * -> OFFICER
             *
             * 관살을 무조건 최우선으로 올리는
             * 강한 극제 방식은 피한다.
             */
            int minimum =
                    Math.min(
                            outputShareBasisPoints,
                            Math.min(
                                    wealthShareBasisPoints,
                                    officerShareBasisPoints
                            )
                    );

            if (
                    outputShareBasisPoints
                            == minimum
            ) {

                preferred.add(
                        DayMasterElementRole.OUTPUT
                );

            } else if (
                    wealthShareBasisPoints
                            == minimum
            ) {

                preferred.add(
                        DayMasterElementRole.WEALTH
                );

            } else {

                preferred.add(
                        DayMasterElementRole.OFFICER
                );
            }

            return preferred;
        }

        /*
         * 완전 중화.
         */
        return preferred;
    }

    public BalancingUsefulGodCandidateRole
    determineCandidateRole(

            DayMasterStrengthLevel strengthLevel,

            UsefulGodMethodApplicability methodApplicability,

            DayMasterElementRole dayMasterRole,

            boolean strengthDirectionMatch,

            boolean directPathologyMatch,

            boolean preferredRole,

            ClimateElementPresenceLevel presenceLevel,

            boolean followStructureSuppressed

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                methodApplicability,
                "억부용신법 적용도가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
        );

        if (
                methodApplicability.inactive()
        ) {

            return BalancingUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 완전 중화에서는
         * 일반 억부법으로 적극적인 오행 후보를
         * 만들지 않는다.
         */
        if (
                strengthLevel
                        == DayMasterStrengthLevel.BALANCED
        ) {

            return BalancingUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        if (
                !strengthDirectionMatch
        ) {

            return BalancingUsefulGodCandidateRole
                    .NOT_NEEDED;
        }

        /*
         * 종격 모드에서는
         * 일반 억부 방향을 기록만 하고
         * 7-7 판단을 기다린다.
         */
        if (
                followStructureSuppressed
        ) {

            return BalancingUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * 해당 오행이 이미 지나치게 강하면
         * 억부 방향 자체는 맞더라도
         * 단순 추가 강화를 피한다.
         */
        if (
                presenceLevel.saturated()
        ) {

            return BalancingUsefulGodCandidateRole
                    .CONDITIONAL;
        }

        /*
         * 실제 병 구조까지 직접 완화하고
         * 현재 억부 우선 역할이라면
         * 가장 강한 후보.
         */
        if (
                preferredRole
                        && directPathologyMatch
        ) {

            return BalancingUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * 현재 명식의 억부 우선 역할.
         */
        if (
                preferredRole
        ) {

            /*
             * 현재 이미 많이 존재하는 경우
             * 방향은 맞지만 PRIMARY로 강하게
             * 올리지 않는다.
             */
            if (
                    presenceLevel.high()
            ) {

                return BalancingUsefulGodCandidateRole
                        .SECONDARY;
            }

            return BalancingUsefulGodCandidateRole
                    .PRIMARY;
        }

        /*
         * 우선 역할은 아니지만
         * 같은 억부 방향의 보조 역할.
         */
        return BalancingUsefulGodCandidateRole
                .SECONDARY;
    }

    public List<BalancingUsefulGodReasonType>
    reasonsFor(

            DayMasterStrengthLevel strengthLevel,

            DayMasterElementRole dayMasterRole,

            ClimateElementPresenceLevel presenceLevel,

            boolean strengthDirectionMatch,

            boolean directPathologyMatch,

            boolean preferredRole,

            boolean followStructureSuppressed,

            PathologyAnalysis pathology

    ) {

        Objects.requireNonNull(
                strengthLevel,
                "일간 강약 단계가 필요합니다."
        );

        Objects.requireNonNull(
                dayMasterRole,
                "일간 기준 오행 역할이 필요합니다."
        );

        Objects.requireNonNull(
                presenceLevel,
                "현재 오행 보유 수준이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        List<BalancingUsefulGodReasonType> reasons =
                new ArrayList<>();

        /*
         * ================================================
         * 강약 상태
         * ================================================
         */
        switch (
                strengthLevel
        ) {

            case EXTREME_WEAK ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .EXTREME_WEAK_DAY_MASTER
                    );

            case WEAK ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .WEAK_DAY_MASTER
                    );

            case BALANCED_WEAK ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .BALANCED_WEAK_DAY_MASTER
                    );

            case BALANCED ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .BALANCED_DAY_MASTER
                    );

            case BALANCED_STRONG ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .BALANCED_STRONG_DAY_MASTER
                    );

            case STRONG ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .STRONG_DAY_MASTER
                    );

            case EXTREME_STRONG ->
                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .EXTREME_STRONG_DAY_MASTER
                    );
        }

        /*
         * ================================================
         * 억부 방향
         * ================================================
         */
        if (
                strengthDirectionMatch
        ) {

            switch (
                    dayMasterRole
            ) {

                case RESOURCE ->
                        reasons.add(
                                BalancingUsefulGodReasonType
                                        .RESOURCE_SUPPORT_DIRECTION
                        );

                case COMPANION ->
                        reasons.add(
                                BalancingUsefulGodReasonType
                                        .COMPANION_SUPPORT_DIRECTION
                        );

                case OUTPUT ->
                        reasons.add(
                                BalancingUsefulGodReasonType
                                        .OUTPUT_RELEASE_DIRECTION
                        );

                case WEALTH ->
                        reasons.add(
                                BalancingUsefulGodReasonType
                                        .WEALTH_RELEASE_DIRECTION
                        );

                case OFFICER ->
                        reasons.add(
                                BalancingUsefulGodReasonType
                                        .OFFICER_CONTROL_DIRECTION
                        );
            }
        }

        /*
         * ================================================
         * 실제 병 구조
         * ================================================
         */
        if (
                directPathologyMatch
        ) {

            if (
                    dayMasterRole
                            == DayMasterElementRole.RESOURCE
            ) {

                if (
                        pathology.hasPattern(
                                PathologyPatternType
                                        .OUTPUT_DRAIN_OVERLOAD
                        )
                ) {

                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .RESOURCE_PRIORITY_FOR_OUTPUT_DRAIN
                    );
                }

                if (
                        pathology.hasPattern(
                                PathologyPatternType
                                        .OFFICER_CONTROL_OVERLOAD
                        )
                ) {

                    reasons.add(
                            BalancingUsefulGodReasonType
                                    .RESOURCE_PRIORITY_FOR_OFFICER_PRESSURE
                    );
                }
            }

            if (
                    dayMasterRole
                            == DayMasterElementRole.COMPANION

                            && pathology.hasPattern(
                            PathologyPatternType
                                    .WEALTH_EXERTION_OVERLOAD
                    )
            ) {

                reasons.add(
                        BalancingUsefulGodReasonType
                                .COMPANION_PRIORITY_FOR_WEALTH_PRESSURE
                );
            }

            if (
                    dayMasterRole
                            == DayMasterElementRole.OUTPUT

                            && pathology.hasPattern(
                            PathologyPatternType
                                    .SUPPORT_OVERCONCENTRATION
                    )
            ) {

                reasons.add(
                        BalancingUsefulGodReasonType
                                .OUTPUT_PRIORITY_FOR_EXCESS_SUPPORT
                );
            }
        }

        /*
         * ================================================
         * 현재 오행량
         * ================================================
         */
        if (
                presenceLevel.absent()
                        || presenceLevel.low()
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .LOW_EXISTING_ELEMENT_POWER
            );

        } else if (
                presenceLevel.moderate()
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .MODERATE_EXISTING_ELEMENT_POWER
            );

        } else if (
                presenceLevel.high()
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .HIGH_EXISTING_ELEMENT_POWER
            );

        } else if (
                presenceLevel.saturated()
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .SATURATED_ELEMENT_POWER
            );
        }

        if (
                preferredRole
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .PREFERRED_BALANCING_ROLE
            );

            if (
                    presenceLevel.scarce()
            ) {

                reasons.add(
                        BalancingUsefulGodReasonType
                                .RELATIVELY_SCARCE_PREFERRED_ROLE
                );
            }

        } else if (
                strengthDirectionMatch
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .SUPPORTING_BALANCING_ROLE
            );
        }

        if (
                followStructureSuppressed
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .FOLLOW_STRUCTURE_SUPPRESSION
            );
        }

        if (
                strengthLevel
                        == DayMasterStrengthLevel.BALANCED
        ) {

            reasons.add(
                    BalancingUsefulGodReasonType
                            .NO_BALANCING_DIRECTION
            );
        }

        return List.copyOf(
                reasons
        );
    }

    public PathologySeverity
    severityOf(

            PathologyAnalysis pathology,

            PathologyPatternType patternType

    ) {

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        Objects.requireNonNull(
                patternType,
                "병 구조 유형이 필요합니다."
        );

        return pathology
                .evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.patternType()
                                        == patternType
                )
                .map(
                        PathologyEvidence
                                ::severity
                )
                .max(
                        java.util.Comparator.comparingInt(
                                PathologySeverity
                                        ::rank
                        )
                )
                .orElse(
                        PathologySeverity.NONE
                );
    }

    public FiveElement producedElementOf(
            FiveElement source
    ) {

        Objects.requireNonNull(
                source,
                "기준 오행이 필요합니다."
        );

        return switch (
                source
                ) {

            case WOOD ->
                    FiveElement.FIRE;

            case FIRE ->
                    FiveElement.EARTH;

            case EARTH ->
                    FiveElement.METAL;

            case METAL ->
                    FiveElement.WATER;

            case WATER ->
                    FiveElement.WOOD;
        };
    }

    public FiveElement controllerOf(
            FiveElement target
    ) {

        Objects.requireNonNull(
                target,
                "대상 오행이 필요합니다."
        );

        return switch (
                target
                ) {

            case WOOD ->
                    FiveElement.METAL;

            case FIRE ->
                    FiveElement.WATER;

            case EARTH ->
                    FiveElement.WOOD;

            case METAL ->
                    FiveElement.FIRE;

            case WATER ->
                    FiveElement.EARTH;
        };
    }

    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return producedElementOf(
                source
        )
                == target;
    }

    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return controllerOf(
                target
        )
                == source;
    }

    private boolean weakSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_WEAK

                || level
                == DayMasterStrengthLevel.WEAK

                || level
                == DayMasterStrengthLevel.EXTREME_WEAK;
    }

    private boolean strongSide(
            DayMasterStrengthLevel level
    ) {

        return level
                == DayMasterStrengthLevel.BALANCED_STRONG

                || level
                == DayMasterStrengthLevel.STRONG

                || level
                == DayMasterStrengthLevel.EXTREME_STRONG;
    }

    private void validateShare(
            int shareBasisPoints
    ) {

        if (
                shareBasisPoints < 0
                        || shareBasisPoints > 10_000
        ) {

            throw new IllegalArgumentException(
                    "오행 비중은 0~10000bp 사이여야 합니다."
            );
        }
    }
}