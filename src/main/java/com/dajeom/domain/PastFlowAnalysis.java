package com.dajeom.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 지나온 흐름 해석을 위한 최종 내부 분석 묶음.
 *
 * =================================================
 *
 * 흐름:
 *
 * PeriodMyeongriFacts
 *      ↓
 * PeriodMyeongriInfluence
 *      ↓
 * PeriodLifeSignal
 *      ↓
 * PeriodLifeClaim
 *
 * =================================================
 *
 * 이 객체는 AI에 직접 노출하는 DTO가 아니다.
 *
 * 내부 계산 검증 및 이후 Narrative 계층에서
 * 필요한 PeriodLifeClaim을 전달하기 위한
 * 종합 내부 결과다.
 *
 * =================================================
 *
 * 실제 사건은 포함하지 않는다.
 *
 * 예:
 *
 * - 취업
 * - 퇴사
 * - 결혼
 * - 이별
 * - 이사
 * - 재물 획득
 *
 * 같은 사건 정보는 이 계층에서 생성하지 않는다.
 */
public record PastFlowAnalysis(

        List<PeriodMyeongriFacts> facts,

        List<PeriodMyeongriInfluence> influences,

        List<PeriodLifeSignal> signals,

        List<PeriodLifeClaim> claims

) {

    public PastFlowAnalysis {

        facts =
                copyAndValidateFacts(
                        facts
                );

        influences =
                copyAndValidateInfluences(
                        influences
                );

        signals =
                copyAndValidateSignals(
                        signals
                );

        claims =
                copyAndValidateClaims(
                        claims
                );
    }

    /**
     * 지나온 흐름에 사용할 기간 Claim이
     * 하나라도 존재하는가.
     */
    public boolean hasClaims() {

        return !claims.isEmpty();
    }

    /**
     * 특정 운 단위의 Claim만 조회한다.
     */
    public List<PeriodLifeClaim> claimsOf(
            LifePeriodType periodType
    ) {

        Objects.requireNonNull(
                periodType,
                "LifePeriodType은 필수입니다."
        );

        return claims
                .stream()
                .filter(
                        claim ->
                                claim
                                        .period()
                                        .type()
                                        == periodType
                )
                .toList();
    }

    public List<PeriodLifeClaim> daewoonClaims() {

        return claimsOf(
                LifePeriodType.DAEWOON
        );
    }

    public List<PeriodLifeClaim> sewoonClaims() {

        return claimsOf(
                LifePeriodType.SEWOON
        );
    }

    public List<PeriodLifeClaim> wolwoonClaims() {

        return claimsOf(
                LifePeriodType.WOLWOON
        );
    }

    /**
     * 특정 기간의 Claim 조회.
     */
    public List<PeriodLifeClaim> claimsFor(
            LifePeriodKey period
    ) {

        Objects.requireNonNull(
                period,
                "LifePeriodKey는 필수입니다."
        );

        return claims
                .stream()
                .filter(
                        claim ->
                                claim
                                        .period()
                                        .equals(period)
                )
                .toList();
    }

    /**
     * 시간순으로 정렬된 모든 기간 Claim.
     */
    public List<PeriodLifeClaim> orderedClaims() {

        List<PeriodLifeClaim> ordered =
                new ArrayList<>(
                        claims
                );

        ordered.sort(

                Comparator
                        .comparing(
                                (PeriodLifeClaim claim) ->
                                        claim
                                                .period()
                                                .startInclusive()
                        )
                        .thenComparing(
                                claim ->
                                        claim
                                                .period()
                                                .type()
                                                .ordinal()
                        )
                        .thenComparing(
                                claim ->
                                        claim
                                                .period()
                                                .key()
                        )
                        .thenComparing(
                                claim ->
                                        claim
                                                .code()
                                                .ordinal()
                        )
        );

        return List.copyOf(
                ordered
        );
    }

    private static List<PeriodMyeongriFacts>
    copyAndValidateFacts(
            List<PeriodMyeongriFacts> values
    ) {

        if (values == null) {

            return List.of();
        }

        for (
                PeriodMyeongriFacts value
                : values
        ) {

            Objects.requireNonNull(
                    value,
                    "PastFlowAnalysis facts에 null이 포함될 수 없습니다."
            );
        }

        return List.copyOf(
                values
        );
    }

    private static List<PeriodMyeongriInfluence>
    copyAndValidateInfluences(
            List<PeriodMyeongriInfluence> values
    ) {

        if (values == null) {

            return List.of();
        }

        for (
                PeriodMyeongriInfluence value
                : values
        ) {

            Objects.requireNonNull(
                    value,
                    "PastFlowAnalysis influences에 null이 포함될 수 없습니다."
            );
        }

        return List.copyOf(
                values
        );
    }

    private static List<PeriodLifeSignal>
    copyAndValidateSignals(
            List<PeriodLifeSignal> values
    ) {

        if (values == null) {

            return List.of();
        }

        for (
                PeriodLifeSignal value
                : values
        ) {

            Objects.requireNonNull(
                    value,
                    "PastFlowAnalysis signals에 null이 포함될 수 없습니다."
            );
        }

        return List.copyOf(
                values
        );
    }

    private static List<PeriodLifeClaim>
    copyAndValidateClaims(
            List<PeriodLifeClaim> values
    ) {

        if (values == null) {

            return List.of();
        }

        for (
                PeriodLifeClaim value
                : values
        ) {

            Objects.requireNonNull(
                    value,
                    "PastFlowAnalysis claims에 null이 포함될 수 없습니다."
            );
        }

        return List.copyOf(
                values
        );
    }
}