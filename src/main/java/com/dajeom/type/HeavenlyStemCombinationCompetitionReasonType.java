package com.dajeom.type;

/**
 * 정밀화 9차-2.
 *
 * 천간합 후보의 쟁합·다중합·방해
 * 판정 근거.
 *
 * =================================================
 *
 * 하나의 Candidate에 여러 Reason이
 * 동시에 존재할 수 있다.
 *
 * 예:
 *
 * DAY 甲이
 * YEAR 己 / HOUR 己와 모두 합하면:
 *
 * SHARED_MEMBER_COMPETITION
 * DAY_MASTER_IN_MULTIPLE_COMBINATIONS
 *
 * 등이 함께 기록될 수 있다.
 *
 * =================================================
 *
 * 외부 극제 정책:
 *
 * 합 구성원이 제3 천간을 극하는 것은
 * 그 자체로 합의 방해로 처리하지 않는다.
 *
 * 제3 천간이 합 구성원을 직접 극하는 경우,
 * 또는 합 구성원이 외부 천간충 관계에
 * 들어가는 경우를 방해 Evidence로 본다.
 */
public enum HeavenlyStemCombinationCompetitionReasonType {

    NO_COMPETING_CANDIDATE(
            "동일 천간을 공유하는 다른 합 후보가 없습니다."
    ),

    SHARED_MEMBER_COMPETITION(
            "하나의 천간이 다른 합 후보와 중복 참여합니다."
    ),

    MULTIPLE_COMPETING_CANDIDATES(
            "현재 합 후보와 경쟁하는 후보가 둘 이상 존재합니다."
    ),

    DAY_MASTER_IN_MULTIPLE_COMBINATIONS(
            "일간이 둘 이상의 천간합 후보에 동시에 참여합니다."
    ),

    ADJACENT_COMPETITOR(
            "인접한 궁위의 경쟁 합 후보가 존재합니다."
    ),

    EQUAL_OR_STRONGER_COMPETITOR(
            "현재 후보와 동급 또는 더 높은 기본 적용도의 경쟁 후보가 존재합니다."
    ),

    ONLY_WEAKER_COMPETITORS(
            "경쟁 후보는 존재하지만 현재 후보보다 기본 적용도가 낮습니다."
    ),

    EXTERNAL_CLASH_ON_MEMBER(
            "합 구성원이 제3 천간과 외부 천간충 관계에 있습니다."
    ),

    EXTERNAL_CONTROL_ON_MEMBER(
            "합 구성원이 제3 천간으로부터 직접 극을 받습니다."
    ),

    ADJACENT_EXTERNAL_OBSTRUCTION(
            "합 구성원에 인접한 외부 천간의 방해 관계가 존재합니다."
    ),

    DISTANT_EXTERNAL_OBSTRUCTION(
            "합 구성원에 원거리 외부 천간의 방해 관계가 존재합니다."
    ),

    MULTIPLE_EXTERNAL_OBSTRUCTIONS(
            "합 구성원에 둘 이상의 외부 방해 관계가 존재합니다."
    ),

    INTERNAL_PAIR_CONTROL_IGNORED(
            "합 Pair 내부의 오행 극제는 외부 방해로 계산하지 않습니다."
    ),

    UNCONTESTED_STATE(
            "현재 후보는 기본적으로 독립된 합 후보입니다."
    ),

    COMPETING_STATE(
            "경쟁 후보가 있으나 현재 후보가 기본 적용도에서 상대적으로 우세합니다."
    ),

    CONTESTED_STATE(
            "동급 또는 상위 경쟁 후보가 있어 실제 합 선택 검토가 필요합니다."
    ),

    OBSTRUCTED_STATE(
            "외부 충·극 구조가 있어 합화 성립 여부를 추가 검토해야 합니다."
    );

    private final String koreanDescription;

    HeavenlyStemCombinationCompetitionReasonType(
            String koreanDescription
    ) {

        this.koreanDescription =
                koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}