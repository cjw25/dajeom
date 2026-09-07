package com.dajeom.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 네 지지의
 * 본래 오행 + 합화 작용 오행 전체.
 *
 * =================================================
 *
 * 원국층:
 *
 * 년지 / 월지 / 일지 / 시지
 * 본래 오행
 * 기존 지장간
 *
 * 그대로 유지
 *
 * =================================================
 *
 * 작용층:
 *
 * ACCEPTED 합화가 있을 때만
 * transformationElement 추가
 *
 * =================================================
 *
 * 이 객체는 지지를 다른 지지로 바꾸거나
 * 지장간을 삭제하지 않는다.
 */
public record EffectiveBranchElementAnalysis(

        EffectiveBranchElementState yearBranch,

        EffectiveBranchElementState monthBranch,

        EffectiveBranchElementState dayBranch,

        Optional<EffectiveBranchElementState> hourBranch

) {

    public EffectiveBranchElementAnalysis {

        Objects.requireNonNull(
                yearBranch,
                "년지 상태는 필수입니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지 상태는 필수입니다."
        );

        Objects.requireNonNull(
                dayBranch,
                "일지 상태는 필수입니다."
        );

        Objects.requireNonNull(
                hourBranch,
                "시지 Optional은 필수입니다."
        );

        if (
                yearBranch.position()
                        != PillarPosition.YEAR
        ) {

            throw new IllegalArgumentException(
                    "년지 상태의 위치가 YEAR가 아닙니다."
            );
        }

        if (
                monthBranch.position()
                        != PillarPosition.MONTH
        ) {

            throw new IllegalArgumentException(
                    "월지 상태의 위치가 MONTH가 아닙니다."
            );
        }

        if (
                dayBranch.position()
                        != PillarPosition.DAY
        ) {

            throw new IllegalArgumentException(
                    "일지 상태의 위치가 DAY가 아닙니다."
            );
        }

        if (
                hourBranch.isPresent()
                        &&
                        hourBranch
                                .get()
                                .position()
                                != PillarPosition.HOUR
        ) {

            throw new IllegalArgumentException(
                    "시지 상태의 위치가 HOUR가 아닙니다."
            );
        }
    }

    public Optional<EffectiveBranchElementState> stateAt(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return switch (position) {

            case YEAR ->
                    Optional.of(
                            yearBranch
                    );

            case MONTH ->
                    Optional.of(
                            monthBranch
                    );

            case DAY ->
                    Optional.of(
                            dayBranch
                    );

            case HOUR ->
                    hourBranch;
        };
    }

    public List<EffectiveBranchElementState> allPresentBranches() {

        java.util.stream.Stream<
                EffectiveBranchElementState
                > base =
                java.util.stream.Stream.of(
                        yearBranch,
                        monthBranch,
                        dayBranch
                );

        return java.util.stream.Stream
                .concat(
                        base,
                        hourBranch.stream()
                )
                .toList();
    }

    /**
     * 합화 작용이 실제 ACCEPTED된 지지만 조회.
     */
    public List<EffectiveBranchElementState>
    transformedBranches() {

        return allPresentBranches()
                .stream()
                .filter(
                        EffectiveBranchElementState
                                ::hasTransformationEffect
                )
                .toList();
    }

    /**
     * 정상적인 계산에서는 없어야 하는
     * 서로 다른 ACCEPTED 합화 충돌.
     */
    public List<EffectiveBranchElementState>
    conflictingBranches() {

        return allPresentBranches()
                .stream()
                .filter(
                        EffectiveBranchElementState
                                ::hasTransformationConflict
                )
                .toList();
    }

    public boolean hasTransformationConflict() {

        return !conflictingBranches()
                .isEmpty();
    }
}