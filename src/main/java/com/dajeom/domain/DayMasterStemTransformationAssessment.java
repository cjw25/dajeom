package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 하나의 일간 천간오합에 대한
 * 真化 / 假化 / 不化 / 爭合 판정.
 *
 * 기존 천간 자체는 삭제하지 않는다.
 *
 * 예:
 *
 * 丁壬真化木
 *
 * 이라고 해도 원국에는
 *
 * 日干 丁
 * 상대 壬
 *
 * 이 그대로 보존된다.
 *
 * 화기 작용층에서 木을 별도로 사용한다.
 */
public record DayMasterStemTransformationAssessment(

        DayMasterStemTransformationCandidate candidate,

        DayMasterStemTransformationState state,

        Set<DayMasterStemTransformationReason> reasons

) {

    public DayMasterStemTransformationAssessment {

        Objects.requireNonNull(
                candidate,
                "화기 후보는 필수입니다."
        );

        Objects.requireNonNull(
                state,
                "화기 판정 상태는 필수입니다."
        );

        reasons =
                reasons == null
                        ? Set.of()
                        : Set.copyOf(
                        reasons
                );

        if (reasons.isEmpty()) {

            throw new IllegalArgumentException(
                    "화기 판정에는 최소 하나의 판정 이유가 필요합니다."
            );
        }
    }

    public HeavenlyStem dayMaster() {

        return candidate.dayMaster();
    }

    public HeavenlyStem counterpartStem() {

        return candidate.counterpartStem();
    }

    public PillarPosition counterpartPosition() {

        return candidate.counterpartPosition();
    }

    public FiveElement transformationElement() {

        return candidate.transformationElement();
    }

    public boolean trueTransformation() {

        return state
                == DayMasterStemTransformationState
                .TRUE_TRANSFORMATION;
    }

    public boolean fakeTransformation() {

        return state
                == DayMasterStemTransformationState
                .FAKE_TRANSFORMATION;
    }

    public boolean notTransformed() {

        return state
                == DayMasterStemTransformationState
                .NOT_TRANSFORMED;
    }

    public boolean contested() {

        return state
                == DayMasterStemTransformationState
                .CONTESTED;
    }

    /**
     * 실제 화기격 작용을
     * 확정적으로 사용할 수 있는 것은
     * 真化에서만 허용.
     *
     * 假化는 별도 해석 경로로 유지한다.
     */
    public boolean transformationEffectFullyAccepted() {

        return trueTransformation();
    }
}