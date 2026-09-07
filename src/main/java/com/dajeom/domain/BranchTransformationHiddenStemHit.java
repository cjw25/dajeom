package com.dajeom.domain;

import java.util.Objects;

/**
 * 합화 목표 오행과 같은 오행을 가진
 * 지장간 위치.
 *
 * 이것이 있다고 해서
 * 합화 성립이라는 뜻은 아니다.
 *
 * 화신의 실제 근기 Context 중 하나다.
 */
public record BranchTransformationHiddenStemHit(

        PillarPosition branchPosition,

        EarthlyBranch branch,

        HeavenlyStem hiddenStem,

        HiddenStemPhase hiddenStemPhase

) {

    public BranchTransformationHiddenStemHit {

        Objects.requireNonNull(
                branchPosition,
                "지지 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간은 필수입니다."
        );

        Objects.requireNonNull(
                hiddenStemPhase,
                "지장간 단계는 필수입니다."
        );
    }
}