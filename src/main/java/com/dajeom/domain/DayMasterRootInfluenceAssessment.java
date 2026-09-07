package com.dajeom.domain;

import java.util.Objects;

/**
 * 하나의 일간 통근 지점에 대한
 * 관계 영향 Context.
 *
 * 보존:
 *
 * - 실제 통근 지점
 * - 해당 지점의 관계 신호
 * - 월령 계절 상태
 * - 분류된 관계 Context
 *
 * 여기서 뿌리의 삭제/생존을 확정하지 않는다.
 */
public record DayMasterRootInfluenceAssessment(

        StemGroundHit root,

        DayMasterRootRelationSignals signals,

        FiveElementSeasonState seasonState,

        DayMasterRootInfluenceState influenceState

) {

    public DayMasterRootInfluenceAssessment {

        Objects.requireNonNull(
                root,
                "통근 지점은 필수입니다."
        );

        Objects.requireNonNull(
                signals,
                "지지 관계 신호는 필수입니다."
        );

        Objects.requireNonNull(
                seasonState,
                "월령 계절 상태는 필수입니다."
        );

        Objects.requireNonNull(
                influenceState,
                "통근 관계 상태는 필수입니다."
        );
    }

    public HeavenlyStem dayMaster() {

        return root.targetStem();
    }

    public PillarPosition rootPosition() {

        return root.branchPosition();
    }

    public EarthlyBranch rootBranch() {

        return root.branch();
    }

    public HiddenStemPhase rootPhase() {

        return root.hiddenStemPhase();
    }

    public boolean exactStemRoot() {

        return root.exactStem();
    }

    public boolean sameElementOtherStemRoot() {

        return root.sameElementOtherStem();
    }

    public boolean clashPresent() {

        return signals.hasClash();
    }

    public boolean combinationPresent() {

        return signals.hasCombination();
    }
}