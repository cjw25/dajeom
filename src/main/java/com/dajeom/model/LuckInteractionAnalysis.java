package com.dajeom.model;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 원국 + 대운 + 세운의
 * 종합 상호작용 분석 결과.
 *
 * 포함:
 *
 * - 해당 세운
 * - 해당 시점의 대운
 * - 천간 관계
 * - 지지 관계
 * - 대운/세운 십성
 * - 용신/희신/기신 후보 오행 유입
 *
 * 아직 최종 길흉 해석은 하지 않는다.
 */
public record LuckInteractionAnalysis(

        /**
         * 해당 세운과 함께 분석한 대운.
         *
         * 출생시간 미상 또는
         * 첫 대운 이전인 경우 null 가능.
         */
        DecadeLuckPeriod decadeLuckPeriod,

        AnnualLuckAnalysis annualLuckAnalysis,

        List<LuckStemInteraction> stemInteractions,

        List<LuckBranchInteraction> branchInteractions,

        List<LuckTenGodInfluence> tenGodInfluences,

        List<ElementCandidateMatch> elementCandidateMatches,

        /**
         * 대운 계산 자체가 가능했는지.
         */
        boolean decadeLuckCalculationAvailable,

        /**
         * 해당 세운 기간 안에서
         * 대운 교체가 발생하는지.
         *
         * true라면 한 해 안에서도
         * 전반/후반의 대운 배경이 달라질 수 있다.
         */
        boolean decadeTransitionDuringAnnualLuck,

        String ruleVersion

) {

    public LuckInteractionAnalysis {

        Objects.requireNonNull(
                annualLuckAnalysis,
                "세운 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                stemInteractions,
                "천간 상호작용 목록은 필수입니다."
        );

        Objects.requireNonNull(
                branchInteractions,
                "지지 상호작용 목록은 필수입니다."
        );

        Objects.requireNonNull(
                tenGodInfluences,
                "십성 영향 목록은 필수입니다."
        );

        Objects.requireNonNull(
                elementCandidateMatches,
                "오행 후보 매칭 결과는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        stemInteractions =
                List.copyOf(
                        stemInteractions
                );

        branchInteractions =
                List.copyOf(
                        branchInteractions
                );

        tenGodInfluences =
                List.copyOf(
                        tenGodInfluences
                );

        elementCandidateMatches =
                List.copyOf(
                        elementCandidateMatches
                );

        if (
                !decadeLuckCalculationAvailable
                        && decadeLuckPeriod != null
        ) {

            throw new IllegalArgumentException(
                    "대운 계산 불가 상태에서는 대운 구간이 존재할 수 없습니다."
            );
        }
    }

    public Optional<DecadeLuckPeriod>
    optionalDecadeLuckPeriod() {

        return Optional.ofNullable(
                decadeLuckPeriod
        );
    }

    public boolean hasDecadeLuck() {

        return decadeLuckPeriod != null;
    }

    public boolean hasStemInteraction() {

        return !stemInteractions.isEmpty();
    }

    public boolean hasBranchInteraction() {

        return !branchInteractions.isEmpty();
    }

    public boolean hasRelations() {

        return hasStemInteraction()
                || hasBranchInteraction();
    }
}