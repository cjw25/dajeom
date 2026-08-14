package com.dajeom.type;

/**
 * 정밀화 9차-8.
 *
 * 六合·三合·方合 후보의
 * 최종 통합 역할 판정 근거.
 *
 * 점수 합산용이 아니다.
 *
 * =================================================
 *
 * 하나의 후보에는 여러 Reason이
 * 동시에 존재할 수 있다.
 */
public enum EarthlyBranchCombinationIntegrationReasonType {

    /*
     * =================================================
     * 기본 9-4 / 9-5 / 9-6 상태
     * =================================================
     */

    BASE_MEANINGFUL(
            "기본 결합·국 작용이 의미 있는 수준으로 성립했습니다."
    ),

    BASE_CONDITIONAL(
            "결합 후보는 남아 있으나 기본 성립 상태가 조건부입니다."
    ),

    BASE_INACTIVE(
            "현재 기본 상태에서는 실질적인 합화·국 후보로 보기 어렵습니다."
    ),

    /*
     * =================================================
     * 9-7 방해
     * =================================================
     */

    NO_OBSTRUCTION(
            "충·형·파·해 방해가 없습니다."
    ),

    MINOR_OBSTRUCTION(
            "경미한 충·형·파·해 방해가 존재합니다."
    ),

    MEANINGFUL_OBSTRUCTION(
            "결합 성립도에 영향을 주는 유의미한 방해가 존재합니다."
    ),

    SEVERE_OBSTRUCTION(
            "결합 구조를 강하게 흔드는 방해가 존재합니다."
    ),

    /*
     * =================================================
     * 구조 우선 Evidence
     * =================================================
     */

    COMPLETE_FORMATION(
            "완전한 삼합 또는 방합 구조입니다."
    ),

    CARDINAL_BRANCH_INCLUDED(
            "삼합·방합의 중심 왕지가 포함되어 있습니다."
    ),

    MONTH_BRANCH_INVOLVED(
            "월지가 결합 후보에 직접 참여합니다."
    ),

    /*
     * =================================================
     * 중첩
     * =================================================
     */

    NO_OVERLAPPING_CANDIDATE(
            "같은 궁위 구성원을 공유하는 다른 결합 후보가 없습니다."
    ),

    SAME_TARGET_CONVERGENCE(
            "동일 구성원을 공유하면서 같은 목표 오행으로 수렴하는 결합 후보가 존재합니다."
    ),

    MULTIPLE_SAME_TARGET_CONVERGENCE(
            "같은 목표 오행으로 수렴하는 중첩 결합 후보가 둘 이상 존재합니다."
    ),

    MEANINGFUL_CONVERGENT_SUPPORT(
            "같은 목표 오행의 의미 있는 결합 후보가 현재 후보를 보조합니다."
    ),

    HIGHER_PRIORITY_CONVERGENT_CANDIDATE(
            "같은 목표 오행에서 더 높은 우선도의 결합 후보가 존재합니다."
    ),

    /*
     * =================================================
     * 경쟁
     * =================================================
     */

    DIFFERENT_TARGET_COMPETITION(
            "같은 구성원을 공유하지만 서로 다른 목표 오행을 향하는 후보가 존재합니다."
    ),

    HIGHER_PRIORITY_COMPETITOR(
            "다른 목표 오행의 더 높은 우선도 후보가 존재합니다."
    ),

    EQUAL_PRIORITY_COMPETITOR(
            "다른 목표 오행의 동급 후보가 존재해 우선 방향을 확정하기 어렵습니다."
    ),

    OUTRANKS_CONFLICTING_CANDIDATES(
            "다른 목표 오행의 경쟁 후보보다 현재 후보의 우선도가 높습니다."
    ),

    /*
     * =================================================
     * 최종 역할
     * =================================================
     */

    PRIMARY_ROLE_SELECTED(
            "현재 결합 후보가 주결합 역할로 선택되었습니다."
    ),

    SUPPORTING_ROLE_SELECTED(
            "현재 결합 후보가 동일 목표 오행의 보조결합으로 선택되었습니다."
    ),

    COMPETING_ROLE_SELECTED(
            "동급의 다른 목표 오행 후보와 경쟁하는 상태로 유지됩니다."
    ),

    DEFERRED_ROLE_SELECTED(
            "현재 결합 후보는 후순위 검토 대상으로 보류되었습니다."
    ),

    SUPPRESSED_ROLE_SELECTED(
            "현재 원국에서는 이 결합 후보의 실질 작용을 억제 상태로 봅니다."
    );

    private final String koreanDescription;

    EarthlyBranchCombinationIntegrationReasonType(
            String koreanDescription
    ) {

        this.koreanDescription = koreanDescription;
    }

    public String koreanDescription() {

        return koreanDescription;
    }
}