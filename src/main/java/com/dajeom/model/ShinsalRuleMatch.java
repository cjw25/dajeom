package com.dajeom.model;

import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;

import java.util.List;
import java.util.Objects;

/**
 * 정밀화 17차-1.
 *
 * 하나의 신살 Rule이 실제 조건을
 * 충족했음을 나타내는 raw Match.
 *
 * =================================================
 *
 * referencePoints
 *
 * 신살 계산 기준.
 *
 * 예:
 *
 * 일간 丁
 *
 * =================================================
 *
 * matchedPoints
 *
 * 실제 조건을 만족한 대상.
 *
 * 예:
 *
 * YEAR Branch 酉
 *
 * 또는:
 *
 * Annual Branch ○
 *
 * =================================================
 *
 * 이 객체는 길흉판정을 포함하지 않는다.
 */
public record ShinsalRuleMatch(

        ShinsalType type,

        ShinsalRuleBasis basis,

        String ruleCode,

        String conventionId,

        List<ShinsalPoint> referencePoints,

        List<ShinsalPoint> matchedPoints,

        List<String> reasons

) {

    public ShinsalRuleMatch {

        Objects.requireNonNull(
                type,
                "신살 Match에는 ShinsalType이 필요합니다."
        );

        Objects.requireNonNull(
                basis,
                "신살 Match에는 RuleBasis가 필요합니다."
        );

        if (
                ruleCode == null
                        || ruleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "신살 Rule Code가 필요합니다."
            );
        }

        ruleCode =
                ruleCode.trim();

        if (
                conventionId == null
                        || conventionId.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "신살 Rule의 conventionId가 필요합니다."
            );
        }

        conventionId =
                conventionId.trim();

        referencePoints =
                referencePoints == null
                        ? List.of()
                        : List.copyOf(
                        referencePoints
                );

        matchedPoints =
                matchedPoints == null
                        ? List.of()
                        : List.copyOf(
                        matchedPoints
                );

        if (
                matchedPoints.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "신살 Match에는 최소 하나의 matchedPoint가 필요합니다."
            );
        }

        for (
                ShinsalPoint point
                : referencePoints
        ) {

            Objects.requireNonNull(
                    point,
                    "referencePoint는 null일 수 없습니다."
            );
        }

        for (
                ShinsalPoint point
                : matchedPoints
        ) {

            Objects.requireNonNull(
                    point,
                    "matchedPoint는 null일 수 없습니다."
            );
        }

        reasons =
                reasons == null
                        ? List.of()
                        : reasons
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                reason ->
                                        !reason.isBlank()
                        )
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "신살 Match에는 최소 하나의 근거가 필요합니다."
            );
        }
    }

    public boolean natalMatch() {

        return matchedPoints
                .stream()
                .anyMatch(
                        ShinsalPoint::natal
                );
    }

    public boolean luckMatch() {

        return matchedPoints
                .stream()
                .anyMatch(
                        ShinsalPoint::luck
                );
    }
}