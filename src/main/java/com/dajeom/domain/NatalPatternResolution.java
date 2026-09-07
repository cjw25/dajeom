package com.dajeom.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * 원국 격국 routing의 최종 결과.
 *
 * =================================================
 *
 * decision
 *
 * 격국 하나를 실제 선택하고
 * 판정까지 끝낸 경우.
 *
 * =================================================
 *
 * review
 *
 * 변격 후보가 아직 복수이거나
 * 후보 선택 자체가 끝나지 않은 경우.
 *
 * =================================================
 *
 * 둘 중 정확히 하나만 존재한다.
 */
public record NatalPatternResolution(

        Optional<NatalPatternDecision> decision,

        Optional<NatalPatternReview> review

) {

    public NatalPatternResolution {

        decision =
                decision == null
                        ? Optional.empty()
                        : decision;

        review =
                review == null
                        ? Optional.empty()
                        : review;

        if (
                decision.isPresent()
                        == review.isPresent()
        ) {

            throw new IllegalArgumentException(
                    "NatalPatternResolution은 decision 또는 review 중 정확히 하나만 가져야 합니다."
            );
        }
    }

    public static NatalPatternResolution decided(
            NatalPatternDecision decision
    ) {

        Objects.requireNonNull(
                decision,
                "NatalPatternDecision은 필수입니다."
        );

        return new NatalPatternResolution(
                Optional.of(decision),
                Optional.empty()
        );
    }

    public static NatalPatternResolution review(
            NatalPatternReview review
    ) {

        Objects.requireNonNull(
                review,
                "NatalPatternReview는 필수입니다."
        );

        return new NatalPatternResolution(
                Optional.empty(),
                Optional.of(review)
        );
    }

    public boolean decided() {

        return decision.isPresent();
    }

    public boolean reviewRequired() {

        return review.isPresent()
                ||
                decision
                        .map(
                                NatalPatternDecision
                                        ::reviewRequired
                        )
                        .orElse(false);
    }
}