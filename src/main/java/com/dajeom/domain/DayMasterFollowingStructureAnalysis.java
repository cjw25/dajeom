package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 종격 계열 실제 판정 결과.
 *
 * =================================================
 *
 * state:
 *
 * 從財
 * 從官殺
 * 從兒
 * 從勢
 * 從旺
 * 從強
 * 不成立
 *
 * =================================================
 *
 * reasons:
 *
 * 왜 그 판정이 나왔는지
 * 실제 명리 조건을 그대로 보존.
 *
 * =================================================
 *
 * 숫자:
 * 없음
 */
public record DayMasterFollowingStructureAnalysis(

        HeavenlyStem dayMaster,

        DayMasterFollowingStructureState state,

        FiveElementSeasonState seasonState,

        DayMasterGroundStatus groundStatus,

        DayMasterRelationType monthMainRelation,

        boolean monthMainExposed,

        Set<DayMasterFollowingStructureReason> reasons

) {

    public DayMasterFollowingStructureAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "종격 판정 상태는 필수입니다."
        );

        Objects.requireNonNull(
                seasonState,
                "월령 상태는 필수입니다."
        );

        Objects.requireNonNull(
                groundStatus,
                "득지 구조 상태는 필수입니다."
        );

        Objects.requireNonNull(
                monthMainRelation,
                "월령 MAIN과 일간 관계는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(reasons);

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "종격 판정에는 최소 하나의 이유가 필요합니다."
            );
        }
    }

    public boolean established() {

        return state
                != DayMasterFollowingStructureState
                .NOT_ESTABLISHED;
    }

    public boolean weakSideFollowing() {

        return switch (state) {

            case FOLLOW_WEALTH,
                 FOLLOW_CONTROL,
                 FOLLOW_OUTPUT,
                 FOLLOW_MOMENTUM ->
                    true;

            case NOT_ESTABLISHED,
                 FOLLOW_PROSPEROUS,
                 FOLLOW_STRONG ->
                    false;
        };
    }

    public boolean strongSideFollowing() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_PROSPEROUS

                ||

                state
                        == DayMasterFollowingStructureState
                        .FOLLOW_STRONG;
    }

    public boolean followWealth() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_WEALTH;
    }

    public boolean followControl() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_CONTROL;
    }

    public boolean followOutput() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_OUTPUT;
    }

    public boolean followMomentum() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_MOMENTUM;
    }

    public boolean followProsperous() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_PROSPEROUS;
    }

    public boolean followStrong() {

        return state
                == DayMasterFollowingStructureState
                .FOLLOW_STRONG;
    }
}