package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 하나의 일간 통근 지점과
 * 해당 지지의 합화 작용을 연결한 Context.
 *
 * =================================================
 *
 * 예:
 *
 * 甲 일간
 * 辰 속 乙에 같은 木 계열 연결
 *
 * 辰酉合化金 ACCEPTED
 *
 * 그러면:
 *
 * original root
 * = 辰 속 乙
 *
 * original branch element
 * = 土
 *
 * transformation effect
 * = 金
 *
 * transformation relation to day master
 * = 金克木
 * = CONTROLS_DAY_MASTER
 *
 * =================================================
 *
 * 여기서도:
 *
 * - 기존 통근 삭제
 * - 통근 무효
 * - 통근 약화
 *
 * 를 판정하지 않는다.
 */
public record DayMasterRootTransformationContext(

        StemGroundHit root,

        EffectiveBranchElementState branchElementState,

        Optional<FiveElement> transformationElement,

        Optional<DayMasterRelationType> transformationRelation

) {

    public DayMasterRootTransformationContext {

        Objects.requireNonNull(
                root,
                "통근 지점은 필수입니다."
        );

        Objects.requireNonNull(
                branchElementState,
                "지지 작용 오행 상태는 필수입니다."
        );

        Objects.requireNonNull(
                transformationElement,
                "합화 작용 오행 Optional은 필수입니다."
        );

        Objects.requireNonNull(
                transformationRelation,
                "합화 작용 관계 Optional은 필수입니다."
        );

        if (
                root.branchPosition()
                        != branchElementState.position()
        ) {

            throw new IllegalArgumentException(
                    "통근 지점의 위치와 지지 작용 상태의 위치가 일치하지 않습니다."
            );
        }

        if (
                root.branch()
                        != branchElementState.branch()
        ) {

            throw new IllegalArgumentException(
                    "통근 지지와 지지 작용 상태의 지지가 일치하지 않습니다."
            );
        }

        if (
                transformationElement.isPresent()
                        != transformationRelation.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "합화 작용 오행과 일간 관계는 함께 존재하거나 함께 없어야 합니다."
            );
        }

        if (
                branchElementState
                        .hasTransformationEffect()
                        &&
                        transformationElement.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "합화 작용이 존재하는 지지는 합화 오행을 가져야 합니다."
            );
        }

        if (
                !branchElementState
                        .hasTransformationEffect()
                        &&
                        transformationElement.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "합화 작용이 없는 지지에 합화 오행을 지정할 수 없습니다."
            );
        }
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

    public HeavenlyStem rootHiddenStem() {

        return root.hiddenStem();
    }

    public HiddenStemPhase rootPhase() {

        return root.hiddenStemPhase();
    }

    public StemGroundRelationType rootRelationType() {

        return root.relationType();
    }

    public FiveElement originalBranchElement() {

        return branchElementState
                .originalElement();
    }

    /**
     * 실제 ACCEPTED된 합화 작용이
     * 이 통근 지지에 걸려 있는가.
     */
    public boolean hasTransformationEffect() {

        return transformationElement
                .isPresent();
    }

    public boolean exactStemRoot() {

        return root.exactStem();
    }

    public boolean sameElementOtherStemRoot() {

        return root.sameElementOtherStem();
    }
}