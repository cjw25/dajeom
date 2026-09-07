package com.dajeom.domain;

import java.util.List;
import java.util.Objects;

/**
 * 원국 LifeClaim 전체 파이프라인 실행 결과.
 *
 * =================================================
 *
 * claims
 *
 * 다음 계층을 모두 통과한 최종 Claim 목록.
 *
 * - NatalLifeClaimCalculator
 * - TemperamentLifeClaimCalculator
 * - RelationshipLifeClaimCalculator
 * - WealthLifeClaimCalculator
 * - WorkAchievementLifeClaimCalculator
 * - HealthLifestyleLifeClaimCalculator
 * - NatalBaseLifeClaimCalculator
 *
 * =================================================
 *
 * coverage
 *
 * 최종 Claim이 7개 해석 카테고리를
 * 실제로 어느 정도 채우고 있는지 확인한 결과.
 *
 * =================================================
 *
 * 이 객체는 사용자 공개 DTO가 아니다.
 *
 * 내부 AI 해석 준비 단계에서 사용한다.
 */
public record NatalLifeClaimPipelineResult(

        List<LifeClaim> claims,

        LifeClaimCoverageAnalysis coverage

) {

    public NatalLifeClaimPipelineResult {

        Objects.requireNonNull(
                claims,
                "LifeClaim 목록은 필수입니다."
        );

        Objects.requireNonNull(
                coverage,
                "LifeClaimCoverageAnalysis는 필수입니다."
        );

        claims =
                List.copyOf(
                        claims
                );

        for (
                LifeClaim claim
                : claims
        ) {

            Objects.requireNonNull(
                    claim,
                    "LifeClaim 목록에 null이 포함될 수 없습니다."
            );
        }
    }

    /**
     * 7개 해석 카테고리가
     * 모두 최소 하나 이상의 Claim으로 채워졌는가.
     */
    public boolean complete() {

        return coverage.complete();
    }

    /**
     * 특정 카테고리에
     * 실제 해석 근거가 존재하는가.
     */
    public boolean covered(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return coverage.covered(
                category
        );
    }

    /**
     * 특정 카테고리에
     * 아직 Claim이 없는가.
     */
    public boolean missing(
            LifeInterpretationCategory category
    ) {

        Objects.requireNonNull(
                category,
                "LifeInterpretationCategory는 필수입니다."
        );

        return coverage.missing(
                category
        );
    }
}