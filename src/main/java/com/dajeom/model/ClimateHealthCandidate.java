package com.dajeom.model;

import com.dajeom.type.ClimateHealthCandidateReasonType;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthIntegrationState;
import com.dajeom.type.FiveElement;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-8.
 *
 * 동일한 五行에 대한:
 *
 * - 조후 후보
 * - 병약 후보
 * - 통관 후보
 *
 * 를 하나로 묶은 통합 후보.
 *
 * =================================================
 *
 * activeAxisCount
 *
 * 세 분석 중 후보로서 실제 작동하는 축의 수.
 *
 * 조후:
 * PRIMARY / SECONDARY / CONDITIONAL
 *
 * 병약:
 * PRIMARY / SECONDARY / CONDITIONAL
 *
 * 통관:
 * PRIMARY / SECONDARY / CONDITIONAL
 *
 * 을 포함한다.
 *
 * =================================================
 *
 * selectedAxisCount
 *
 * PRIMARY / SECONDARY처럼
 * 조건부가 아닌 상태로 선택된 분석 축의 수.
 *
 * =================================================
 *
 * primaryAxisCount
 *
 * 세 분석 중 PRIMARY로 지목한 축의 수.
 *
 * =================================================
 *
 * conditionalAxisCount
 *
 * CONDITIONAL인 분석 축의 수.
 *
 * =================================================
 *
 * hardConflict
 *
 * 단순히 후보가 조건부라는 뜻이 아니라,
 * 실제 다른 병·조후 방향을 악화시키는
 * Conflict Evidence가 존재하는지를 나타낸다.
 *
 * =================================================
 *
 * 아직 최종 용신 결과가 아니다.
 */
public record ClimateHealthCandidate(

        FiveElement element,

        ClimateHealthIntegrationState integrationState,

        ClimateHealthCandidateRole role,

        ClimateElementCandidate climateCandidate,

        RemedyCandidate remedyCandidate,

        PassageCandidate passageCandidate,

        int activeAxisCount,

        int selectedAxisCount,

        int primaryAxisCount,

        int conditionalAxisCount,

        boolean hardConflict,

        boolean saturated,

        List<ClimateHealthCandidateReasonType> reasons

) {

    public ClimateHealthCandidate {

        Objects.requireNonNull(
                element,
                "통합 후보 오행이 필요합니다."
        );

        Objects.requireNonNull(
                integrationState,
                "통합 상태가 필요합니다."
        );

        Objects.requireNonNull(
                role,
                "통합 후보 역할이 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidate,
                "조후 후보가 필요합니다."
        );

        Objects.requireNonNull(
                remedyCandidate,
                "병약 후보가 필요합니다."
        );

        Objects.requireNonNull(
                passageCandidate,
                "통관 후보가 필요합니다."
        );

        reasons =
                reasons == null
                        ? List.of()
                        : List.copyOf(
                        reasons
                );

        if (
                climateCandidate.element()
                        != element

                        || remedyCandidate.element()
                        != element

                        || passageCandidate.element()
                        != element
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 후보의 오행이 서로 일치하지 않습니다."
            );
        }

        if (
                climateCandidate.existingShareBasisPoints()
                        != remedyCandidate.existingShareBasisPoints()

                        || climateCandidate.existingShareBasisPoints()
                        != passageCandidate.existingShareBasisPoints()
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 후보의 현재 오행 비중이 서로 일치하지 않습니다."
            );
        }

        int expectedActiveAxisCount =
                countTrue(
                        climateCandidate.needed(),
                        remedyCandidate.candidate(),
                        passageCandidate.candidate()
                );

        int expectedSelectedAxisCount =
                countTrue(
                        climateCandidate.selected(),
                        remedyCandidate.selected(),
                        passageCandidate.selected()
                );

        int expectedPrimaryAxisCount =
                countTrue(
                        climateCandidate.primary(),
                        remedyCandidate.primary(),
                        passageCandidate.primary()
                );

        int expectedConditionalAxisCount =
                countTrue(
                        climateCandidate.conditional(),
                        remedyCandidate.conditional(),
                        passageCandidate.conditional()
                );

        if (
                activeAxisCount
                        != expectedActiveAxisCount
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 활성 분석 축 수가 실제 후보 상태와 일치하지 않습니다."
            );
        }

        if (
                selectedAxisCount
                        != expectedSelectedAxisCount
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 선택 분석 축 수가 실제 후보 상태와 일치하지 않습니다."
            );
        }

        if (
                primaryAxisCount
                        != expectedPrimaryAxisCount
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 PRIMARY 분석 축 수가 실제 후보 상태와 일치하지 않습니다."
            );
        }

        if (
                conditionalAxisCount
                        != expectedConditionalAxisCount
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 CONDITIONAL 분석 축 수가 실제 후보 상태와 일치하지 않습니다."
            );
        }

        boolean expectedHardConflict =
                climateCandidate.hasConflict()
                        || remedyCandidate.hasConflict()
                        || passageCandidate.crossAnalysisConflict();

        if (
                hardConflict
                        != expectedHardConflict
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 충돌 상태가 하위 분석 결과와 일치하지 않습니다."
            );
        }

        boolean expectedSaturated =
                climateCandidate.saturated()
                        || remedyCandidate.saturated()
                        || passageCandidate.saturated();

        if (
                saturated
                        != expectedSaturated
        ) {

            throw new IllegalArgumentException(
                    "통합 후보의 오행 과다 상태가 하위 분석 결과와 일치하지 않습니다."
            );
        }

        if (
                reasons
                        .stream()
                        .distinct()
                        .count()
                        != reasons.size()
        ) {

            throw new IllegalArgumentException(
                    "통합 후보 선택 근거가 중복되었습니다."
            );
        }

        validateIntegrationState(
                integrationState,
                activeAxisCount,
                selectedAxisCount,
                conditionalAxisCount,
                hardConflict
        );

        validateRole(
                integrationState,
                role
        );
    }

    private static void validateIntegrationState(

            ClimateHealthIntegrationState state,

            int activeAxisCount,

            int selectedAxisCount,

            int conditionalAxisCount,

            boolean hardConflict

    ) {

        if (
                state.conflicted()
        ) {

            if (
                    !hardConflict
            ) {

                throw new IllegalArgumentException(
                        "CONFLICTED 상태에는 실제 충돌 Evidence가 필요합니다."
                );
            }

            return;
        }

        if (
                hardConflict
        ) {

            throw new IllegalArgumentException(
                    "실제 충돌 Evidence가 존재하면 CONFLICTED 상태여야 합니다."
            );
        }

        if (
                state.inactive()
        ) {

            if (
                    activeAxisCount != 0
            ) {

                throw new IllegalArgumentException(
                        "INACTIVE 후보에는 활성 분석 축이 존재할 수 없습니다."
                );
            }

            return;
        }

        if (
                state.conditional()
        ) {

            if (
                    conditionalAxisCount == 0
            ) {

                throw new IllegalArgumentException(
                        "CONDITIONAL 상태에는 하나 이상의 조건부 분석 축이 필요합니다."
                );
            }

            return;
        }

        if (
                state.convergent()
        ) {

            if (
                    selectedAxisCount < 2
            ) {

                throw new IllegalArgumentException(
                        "CONVERGENT 상태에는 최소 두 분석 축의 선택이 필요합니다."
                );
            }

            if (
                    conditionalAxisCount > 0
            ) {

                throw new IllegalArgumentException(
                        "조건부 분석 축이 존재하면 깨끗한 CONVERGENT 상태로 볼 수 없습니다."
                );
            }

            return;
        }

        if (
                state.supportive()
        ) {

            if (
                    selectedAxisCount != 1
            ) {

                throw new IllegalArgumentException(
                        "SUPPORTIVE 상태는 정확히 하나의 선택 분석 축을 가져야 합니다."
                );
            }

            if (
                    conditionalAxisCount > 0
            ) {

                throw new IllegalArgumentException(
                        "조건부 축이 존재하면 SUPPORTIVE가 아니라 CONDITIONAL 상태여야 합니다."
                );
            }
        }
    }

    private static void validateRole(

            ClimateHealthIntegrationState state,

            ClimateHealthCandidateRole role

    ) {

        if (
                state.inactive()
                        && !role.rejected()
        ) {

            throw new IllegalArgumentException(
                    "INACTIVE 후보는 REJECTED여야 합니다."
            );
        }

        if (
                (
                        state.conflicted()
                                || state.conditional()
                )
                        && !role.deferred()
        ) {

            throw new IllegalArgumentException(
                    "CONFLICTED/CONDITIONAL 후보는 DEFERRED여야 합니다."
            );
        }

        if (
                state.convergent()
                        && !role.primary()
        ) {

            throw new IllegalArgumentException(
                    "다중 분석이 깨끗하게 수렴한 후보는 PRIMARY여야 합니다."
            );
        }

        if (
                state.supportive()

                        && (
                        role.deferred()
                                || role.rejected()
                )
        ) {

            throw new IllegalArgumentException(
                    "SUPPORTIVE 후보는 PRIMARY 또는 SECONDARY여야 합니다."
            );
        }
    }

    private static int countTrue(
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

    public boolean primary() {

        return role.primary();
    }

    public boolean secondary() {

        return role.secondary();
    }

    public boolean deferred() {

        return role.deferred();
    }

    public boolean rejected() {

        return role.rejected();
    }

    public boolean selected() {

        return role.selected();
    }

    public boolean convergent() {

        return integrationState
                .convergent();
    }

    public boolean conflicted() {

        return integrationState
                .conflicted();
    }

    public boolean conditional() {

        return integrationState
                .conditional();
    }

    public boolean supportive() {

        return integrationState
                .supportive();
    }

    public boolean inactive() {

        return integrationState
                .inactive();
    }

    public boolean tripleConvergence() {

        return selectedAxisCount == 3;
    }

    public boolean dualConvergence() {

        return selectedAxisCount == 2;
    }

    public boolean hasMultiplePrimarySources() {

        return primaryAxisCount >= 2;
    }

    public int existingShareBasisPoints() {

        return climateCandidate
                .existingShareBasisPoints();
    }

    public double existingSharePercent() {

        return existingShareBasisPoints()
                / 100.0;
    }

    /**
     * 서로 다른 분석축 가운데
     * HIGH/CRITICAL 또는
     * severe pathology를 실제로 다루는 축 수.
     *
     * 단순 합산 점수가 아니라
     * 정렬 및 설명용 count다.
     */
    public int highPriorityAxisCount() {

        int count = 0;

        if (
                climateCandidate.needed()
                        && climateCandidate
                        .addressedNeedLevel()
                        .highPriority()
        ) {

            count++;
        }

        if (
                remedyCandidate.candidate()
                        && remedyCandidate
                        .addressesSeverePathology()
        ) {

            count++;
        }

        if (
                passageCandidate.candidate()
                        && passageCandidate
                        .strongestNeed()
                        .highPriority()
        ) {

            count++;
        }

        return count;
    }

    public boolean hasReason(
            ClimateHealthCandidateReasonType reason
    ) {

        if (
                reason == null
        ) {

            return false;
        }

        return reasons.contains(
                reason
        );
    }
}