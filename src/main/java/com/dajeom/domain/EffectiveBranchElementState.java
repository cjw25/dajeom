package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 원국의 한 지지가 가지는
 * 본래 오행 + 합화 작용 오행 상태.
 *
 * 매우 중요:
 *
 * originalElement는 절대로 사라지지 않는다.
 *
 * 합화가 ACCEPTED되어도:
 *
 * branch
 * originalElement
 * 지장간
 *
 * 은 원국 데이터로 그대로 남는다.
 *
 * transformationElement는
 * 해석/작용층에서 추가로 참고하는 오행이다.
 */
public record EffectiveBranchElementState(

        PillarPosition position,

        EarthlyBranch branch,

        FiveElement originalElement,

        List<BranchCombinationTransformationAssessment>
        acceptedTransformations

) {

    public EffectiveBranchElementState {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        Objects.requireNonNull(
                branch,
                "지지는 필수입니다."
        );

        Objects.requireNonNull(
                originalElement,
                "지지 본래 오행은 필수입니다."
        );

        acceptedTransformations =
                acceptedTransformations == null
                        ? List.of()
                        : List.copyOf(
                        acceptedTransformations
                );

        for (
                BranchCombinationTransformationAssessment assessment
                : acceptedTransformations
        ) {

            Objects.requireNonNull(
                    assessment,
                    "합화 목록에 null이 포함될 수 없습니다."
            );

            if (
                    !assessment
                            .transformationAccepted()
            ) {

                throw new IllegalArgumentException(
                        "acceptedTransformations에는 ACCEPTED 합화만 포함할 수 있습니다."
                );
            }

            if (
                    !matchesParticipant(
                            assessment,
                            position,
                            branch
                    )
            ) {

                throw new IllegalArgumentException(
                        "현재 지지와 관계없는 합화 결과가 포함되어 있습니다."
                );
            }
        }
    }

    /**
     * 현재 지지에 적용된
     * ACCEPTED 합화 오행들을 조회한다.
     *
     * 정상 상태라면:
     *
     * 0개
     * 또는
     * 동일 오행 1종
     *
     * 이어야 한다.
     */
    public Set<FiveElement> transformationElements() {

        return acceptedTransformations
                .stream()
                .map(
                        BranchCombinationTransformationAssessment
                                ::transformationElement
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    /**
     * 단일한 합화 작용 오행이 존재할 때만 반환.
     *
     * 본래 오행을 대체한다는 뜻이 아니다.
     */
    public Optional<FiveElement> transformationElement() {

        Set<FiveElement> elements =
                transformationElements();

        if (
                elements.size()
                        != 1
        ) {

            return Optional.empty();
        }

        return elements
                .stream()
                .findFirst();
    }

    public EffectiveBranchElementMode mode() {

        Set<FiveElement> elements =
                transformationElements();

        if (elements.isEmpty()) {

            return EffectiveBranchElementMode
                    .ORIGINAL_ONLY;
        }

        if (
                elements.size()
                        == 1
        ) {

            return EffectiveBranchElementMode
                    .TRANSFORMATION_EFFECT_ACTIVE;
        }

        return EffectiveBranchElementMode
                .CONFLICTING_ACCEPTED_TRANSFORMATIONS;
    }

    public boolean hasTransformationEffect() {

        return mode()
                == EffectiveBranchElementMode
                .TRANSFORMATION_EFFECT_ACTIVE;
    }

    public boolean hasTransformationConflict() {

        return mode()
                == EffectiveBranchElementMode
                .CONFLICTING_ACCEPTED_TRANSFORMATIONS;
    }

    /**
     * ACCEPTED된 합화가 여러 개더라도
     * 모두 같은 오행을 향한다면
     * 작용 오행은 하나로 조회할 수 있다.
     */
    public boolean hasSingleTransformationElement() {

        return transformationElements()
                .size()
                == 1;
    }

    private static boolean matchesParticipant(

            BranchCombinationTransformationAssessment assessment,

            PillarPosition position,

            EarthlyBranch branch

    ) {

        BranchCombinationOccurrence occurrence =
                assessment.occurrence();

        List<PillarPosition> positions =
                occurrence.positions();

        List<EarthlyBranch> branches =
                occurrence.branches();

        for (
                int i = 0;
                i < positions.size();
                i++
        ) {

            if (
                    positions.get(i)
                            == position
                            &&
                            branches.get(i)
                                    == branch
            ) {

                return true;
            }
        }

        return false;
    }
}