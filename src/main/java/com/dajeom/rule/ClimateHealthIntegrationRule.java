package com.dajeom.rule;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.PassageCandidate;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.type.ClimateHealthCandidateReasonType;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthIntegrationState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-8.
 *
 * 조후·병약·통관 세 분석을
 * 하나의 오행 후보로 통합한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 세 분석의 rank를 단순 합산하지 않는다.
 *
 * 2.
 * 독립적인 분석 두 개 이상이
 *    동일 오행을 깨끗하게 선택하면
 *    CONVERGENT로 본다.
 *
 * 3.
 * 한 분석만 선택했더라도
 *    그 분석의 필요성이 매우 강하다면
 *    PRIMARY 후보가 될 수 있다.
 *
 * 4.
 * 실제 Conflict Evidence가 하나라도 있으면
 *    CONFLICTED로 보존하고 DEFERRED한다.
 *
 * 5.
 * 명시적 충돌은 없지만
 *    하위 단계에서 CONDITIONAL인 축이 존재하면
 *    CONDITIONAL / DEFERRED로 보존한다.
 *
 * 6.
 * 현재 오행 과다도
 *    최종적으로 단순 증강하기 어려운
 *    조건부 근거로 사용한다.
 *
 * 7.
 * 아직 격국과의 적합성은 최종 검증하지 않는다.
 *    그 책임은 6-9이다.
 */
@Component
public class ClimateHealthIntegrationRule {

    public ClimateHealthIntegrationState
    determineIntegrationState(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        Objects.requireNonNull(
                climate,
                "조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                remedy,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                passage,
                "통관 후보가 필요합니다."
        );

        boolean hardConflict =
                hasHardConflict(
                        climate,
                        remedy,
                        passage
                );

        if (
                hardConflict
        ) {

            return ClimateHealthIntegrationState
                    .CONFLICTED;
        }

        int activeCount =
                activeAxisCount(
                        climate,
                        remedy,
                        passage
                );

        if (
                activeCount == 0
        ) {

            return ClimateHealthIntegrationState
                    .INACTIVE;
        }

        int conditionalCount =
                conditionalAxisCount(
                        climate,
                        remedy,
                        passage
                );

        if (
                conditionalCount > 0
        ) {

            return ClimateHealthIntegrationState
                    .CONDITIONAL;
        }

        int selectedCount =
                selectedAxisCount(
                        climate,
                        remedy,
                        passage
                );

        if (
                selectedCount >= 2
        ) {

            return ClimateHealthIntegrationState
                    .CONVERGENT;
        }

        if (
                selectedCount == 1
        ) {

            return ClimateHealthIntegrationState
                    .SUPPORTIVE;
        }

        /*
         * active인데 selected도 conditional도 없다면
         * 정상적인 하위 모델에서는 나오지 않아야 한다.
         */
        throw new IllegalStateException(
                "통합 후보의 하위 분석 상태를 결정할 수 없습니다."
        );
    }

    public ClimateHealthCandidateRole
    determineRole(

            ClimateHealthIntegrationState state,

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        Objects.requireNonNull(
                state,
                "통합 상태가 필요합니다."
        );

        Objects.requireNonNull(
                climate,
                "조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                remedy,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                passage,
                "통관 후보가 필요합니다."
        );

        if (
                state.inactive()
        ) {

            return ClimateHealthCandidateRole
                    .REJECTED;
        }

        if (
                state.conflicted()
                        || state.conditional()
        ) {

            return ClimateHealthCandidateRole
                    .DEFERRED;
        }

        /*
         * 서로 다른 두 분석 이상이
         * 같은 오행을 깨끗하게 선택.
         */
        if (
                state.convergent()
        ) {

            return ClimateHealthCandidateRole
                    .PRIMARY;
        }

        /*
         * 하나의 분석만 선택했지만
         * 그 하나가 매우 강한 필요성을
         * 나타낼 수 있다.
         *
         * 예:
         *
         * 조후 CRITICAL
         * 병약 없음
         * 통관 없음
         *
         * 이런 경우 단순히 SECONDARY로
         * 강등시키지 않는다.
         */
        if (
                state.supportive()
                        && hasSingleHighPrioritySource(
                        climate,
                        remedy,
                        passage
                )
        ) {

            return ClimateHealthCandidateRole
                    .PRIMARY;
        }

        return ClimateHealthCandidateRole
                .SECONDARY;
    }

    public int activeAxisCount(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        return countTrue(
                climate.needed(),
                remedy.candidate(),
                passage.candidate()
        );
    }

    public int selectedAxisCount(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        return countTrue(
                climate.selected(),
                remedy.selected(),
                passage.selected()
        );
    }

    public int primaryAxisCount(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        return countTrue(
                climate.primary(),
                remedy.primary(),
                passage.primary()
        );
    }

    public int conditionalAxisCount(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        return countTrue(
                climate.conditional(),
                remedy.conditional(),
                passage.conditional()
        );
    }

    public boolean hasHardConflict(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        return climate.hasConflict()
                || remedy.hasConflict()
                || passage.crossAnalysisConflict();
    }

    public boolean saturated(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        /*
         * 세 후보는 동일한 3차 오행 비중을
         * 사용하므로 정상적인 경우
         * 동일한 Presence 상태를 가진다.
         *
         * 방어적으로 어느 하나라도 saturated면
         * true로 보존한다.
         */
        return climate.saturated()
                || remedy.saturated()
                || passage.saturated();
    }

    /**
     * 하나의 분석축만 선택됐지만
     * 그 필요성이 높은 경우.
     */
    public boolean hasSingleHighPrioritySource(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        int selectedCount =
                selectedAxisCount(
                        climate,
                        remedy,
                        passage
                );

        if (
                selectedCount != 1
        ) {

            return false;
        }

        if (
                climate.selected()
        ) {

            return climate
                    .addressedNeedLevel()
                    .highPriority();
        }

        if (
                remedy.selected()
        ) {

            return remedy
                    .addressesSeverePathology();
        }

        if (
                passage.selected()
        ) {

            return passage
                    .strongestNeed()
                    .highPriority();
        }

        return false;
    }

    public List<ClimateHealthCandidateReasonType>
    reasonsFor(

            ClimateElementCandidate climate,

            RemedyCandidate remedy,

            PassageCandidate passage

    ) {

        Objects.requireNonNull(
                climate,
                "조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                remedy,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                passage,
                "통관 후보가 필요합니다."
        );

        List<ClimateHealthCandidateReasonType> reasons =
                new ArrayList<>();

        /*
         * ================================================
         * 조후
         * ================================================
         */
        if (
                climate.primary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .CLIMATE_PRIMARY
            );

        } else if (
                climate.secondary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .CLIMATE_SECONDARY
            );

        } else if (
                climate.conditional()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .CLIMATE_CONDITIONAL
            );
        }

        /*
         * ================================================
         * 병약
         * ================================================
         */
        if (
                remedy.primary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .REMEDY_PRIMARY
            );

        } else if (
                remedy.secondary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .REMEDY_SECONDARY
            );

        } else if (
                remedy.conditional()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .REMEDY_CONDITIONAL
            );
        }

        /*
         * ================================================
         * 통관
         * ================================================
         */
        if (
                passage.primary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .PASSAGE_PRIMARY
            );

        } else if (
                passage.secondary()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .PASSAGE_SECONDARY
            );

        } else if (
                passage.conditional()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .PASSAGE_CONDITIONAL
            );
        }

        int selectedCount =
                selectedAxisCount(
                        climate,
                        remedy,
                        passage
                );

        if (
                selectedCount == 3
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .TRIPLE_CONVERGENCE
            );

        } else if (
                selectedCount == 2
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .DUAL_CONVERGENCE
            );
        }

        if (
                selectedCount == 1
                        && hasSingleHighPrioritySource(
                        climate,
                        remedy,
                        passage
                )
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .SINGLE_HIGH_PRIORITY_SOURCE
            );
        }

        if (
                remedy
                        .meaningfulAddressedPathologyCount()
                        >= 2
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .MULTIPLE_PATHOLOGIES_ADDRESSED
            );
        }

        if (
                passage.dayMasterDisadvantaged()
                        && passage.candidate()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .DAY_MASTER_PASSAGE_SUPPORT
            );
        }

        if (
                climate.hasConflict()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .CLIMATE_CONFLICT
            );
        }

        if (
                remedy.hasConflict()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .REMEDY_CONFLICT
            );
        }

        if (
                passage.crossAnalysisConflict()
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .PASSAGE_CONFLICT
            );
        }

        if (
                saturated(
                        climate,
                        remedy,
                        passage
                )
        ) {

            reasons.add(
                    ClimateHealthCandidateReasonType
                            .ELEMENT_SATURATED
            );
        }

        return List.copyOf(
                reasons
        );
    }

    private int countTrue(
            boolean... values
    ) {

        int count = 0;

        for (
                boolean value
                : values
        ) {

            if (
                    value
            ) {

                count++;
            }
        }

        return count;
    }
}