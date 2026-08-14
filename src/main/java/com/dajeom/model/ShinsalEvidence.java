package com.dajeom.model;

import com.dajeom.type.ShinsalEvidenceCertainty;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalRuleBasis;
import com.dajeom.type.ShinsalType;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 17차-6.
 *
 * 중복 ShinsalRuleMatch를 정규화한
 * 최종 신살 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 동일 ANNUAL 午 도화가
 *
 * Segment A
 * Segment B
 *
 * 에서 반복되더라도:
 *
 * ShinsalEvidence 하나만 생성한다.
 *
 * 그리고 applicableSegments에
 * Segment A/B를 연결한다.
 *
 * =================================================
 *
 * natalEvidence:
 *
 * matchedPoints가 전부 NATAL.
 *
 * 이 경우 applicableSegments는 비어 있다.
 *
 * 원국 Evidence는 Timeline Scenario와 관계없이
 * 원국 자체에 속하기 때문이다.
 *
 * =================================================
 *
 * luckEvidence:
 *
 * DECADE / ANNUAL / MONTHLY matchedPoint가 존재.
 *
 * applicableSegments를 통해
 * 어느 Timeline Scenario에서 실제로
 * 해당 신살이 존재하는지 추적한다.
 *
 * =================================================
 *
 * sourceMatches:
 *
 * 정규화 전 17-5 raw Match.
 *
 * 원본 Evidence Source를 버리지 않는다.
 */
public record ShinsalEvidence(

        ShinsalType type,

        ShinsalRuleBasis basis,

        String ruleCode,

        String conventionId,

        ShinsalEvidenceCertainty certainty,

        List<ShinsalPoint> referencePoints,

        List<ShinsalPoint> matchedPoints,

        List<String> reasons,

        List<ShinsalRuleMatch> sourceMatches,

        List<LuckIntegratedSegmentAnalysis> applicableSegments

) {

    public ShinsalEvidence {

        Objects.requireNonNull(
                type,
                "ShinsalEvidence에는 ShinsalType이 필요합니다."
        );

        Objects.requireNonNull(
                basis,
                "ShinsalEvidence에는 ShinsalRuleBasis가 필요합니다."
        );

        Objects.requireNonNull(
                certainty,
                "ShinsalEvidence에는 ShinsalEvidenceCertainty가 필요합니다."
        );

        if (
                ruleCode == null
                        || ruleCode.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalEvidence에는 ruleCode가 필요합니다."
            );
        }

        ruleCode =
                ruleCode.trim();

        if (
                conventionId == null
                        || conventionId.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalEvidence에는 conventionId가 필요합니다."
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
                    "ShinsalEvidence에는 최소 하나의 matchedPoint가 필요합니다."
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
                        .distinct()
                        .toList();

        if (
                reasons.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalEvidence에는 최소 하나의 reason이 필요합니다."
            );
        }

        sourceMatches =
                sourceMatches == null
                        ? List.of()
                        : List.copyOf(
                        sourceMatches
                );

        if (
                sourceMatches.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "ShinsalEvidence에는 최소 하나의 sourceMatch가 필요합니다."
            );
        }

        for (
                ShinsalRuleMatch sourceMatch
                : sourceMatches
        ) {

            Objects.requireNonNull(
                    sourceMatch,
                    "sourceMatch는 null일 수 없습니다."
            );

            validateSourceMetadata(
                    type,
                    basis,
                    ruleCode,
                    conventionId,
                    sourceMatch
            );
        }

        applicableSegments =
                applicableSegments == null
                        ? List.of()
                        : List.copyOf(
                        applicableSegments
                );

        for (
                LuckIntegratedSegmentAnalysis segment
                : applicableSegments
        ) {

            Objects.requireNonNull(
                    segment,
                    "applicableSegment는 null일 수 없습니다."
            );
        }

        /*
         * =================================================
         * Segment 중복 Identity 금지
         * =================================================
         */

        for (
                int first = 0;
                first < applicableSegments.size();
                first++
        ) {

            for (
                    int second = first + 1;
                    second < applicableSegments.size();
                    second++
            ) {

                if (
                        applicableSegments.get(first)
                                == applicableSegments.get(second)
                ) {

                    throw new IllegalArgumentException(
                            "동일 Timeline Segment가 ShinsalEvidence에 중복 연결되었습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * NATAL Evidence
         * =================================================
         */

        if (
                isNatalMatchedPoints(
                        matchedPoints
                )
        ) {

            if (
                    !applicableSegments.isEmpty()
            ) {

                throw new IllegalArgumentException(
                        "원국 ShinsalEvidence에는 applicableSegments를 연결하지 않습니다."
                );
            }

            if (
                    certainty
                            != ShinsalEvidenceCertainty.DEFINITE
            ) {

                throw new IllegalArgumentException(
                        "검출된 원국 ShinsalEvidence는 DEFINITE여야 합니다."
                );
            }
        }

        /*
         * =================================================
         * Luck Evidence
         * =================================================
         */

        if (
                hasLuckMatchedPoints(
                        matchedPoints
                )

                        && applicableSegments.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "운 ShinsalEvidence에는 최소 하나의 applicableSegment가 필요합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본
     * =================================================
     */

    public boolean definite() {

        return certainty
                .definite();
    }

    public boolean possibleOnly() {

        return certainty
                .possibleOnly();
    }

    /*
     * =================================================
     * 원국 / 운
     * =================================================
     */

    public boolean natalEvidence() {

        return isNatalMatchedPoints(
                matchedPoints
        );
    }

    public boolean luckEvidence() {

        return hasLuckMatchedPoints(
                matchedPoints
        );
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    public Set<ShinsalLayer>
    matchedLayers() {

        return matchedPoints
                .stream()
                .map(
                        ShinsalPoint::layer
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    public boolean involvesLayer(
            ShinsalLayer layer
    ) {

        return layer != null
                && matchedLayers()
                .contains(
                        layer
                );
    }

    public boolean decadeEvidence() {

        return involvesLayer(
                ShinsalLayer.DECADE
        );
    }

    public boolean annualEvidence() {

        return involvesLayer(
                ShinsalLayer.ANNUAL
        );
    }

    public boolean monthlyEvidence() {

        return involvesLayer(
                ShinsalLayer.MONTHLY
        );
    }

    /*
     * =================================================
     * Timeline 적용
     * =================================================
     */

    public boolean appliesTo(
            LuckIntegratedSegmentAnalysis segment
    ) {

        if (
                segment == null
        ) {

            return false;
        }

        if (
                natalEvidence()
        ) {

            return true;
        }

        return applicableSegments
                .stream()
                .anyMatch(
                        candidate ->
                                candidate == segment
                );
    }

    public int applicableSegmentCount() {

        return applicableSegments
                .size();
    }

    /*
     * =================================================
     * Provenance
     * =================================================
     */

    public int sourceMatchCount() {

        return sourceMatches
                .size();
    }

    /*
     * =================================================
     * 정규화 Identity Key
     * =================================================
     *
     * 디버깅/로그/중복검증 용도.
     *
     * 길흉 의미는 없다.
     */

    public String identityKey() {

        return type.name()
                + "|"
                + basis.name()
                + "|"
                + ruleCode
                + "|"
                + conventionId
                + "|REF="
                + pointSignature(
                referencePoints
        )
                + "|MATCH="
                + pointSignature(
                matchedPoints
        );
    }

    /*
     * =================================================
     * Helper
     * =================================================
     */

    private static void validateSourceMetadata(

            ShinsalType type,

            ShinsalRuleBasis basis,

            String ruleCode,

            String conventionId,

            ShinsalRuleMatch sourceMatch

    ) {

        if (
                sourceMatch.type()
                        != type
        ) {

            throw new IllegalArgumentException(
                    "sourceMatch의 ShinsalType이 Evidence와 일치하지 않습니다."
            );
        }

        if (
                sourceMatch.basis()
                        != basis
        ) {

            throw new IllegalArgumentException(
                    "sourceMatch의 RuleBasis가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                !sourceMatch
                        .ruleCode()
                        .equals(
                                ruleCode
                        )
        ) {

            throw new IllegalArgumentException(
                    "sourceMatch의 ruleCode가 Evidence와 일치하지 않습니다."
            );
        }

        if (
                !sourceMatch
                        .conventionId()
                        .equals(
                                conventionId
                        )
        ) {

            throw new IllegalArgumentException(
                    "sourceMatch의 conventionId가 Evidence와 일치하지 않습니다."
            );
        }
    }

    private static boolean isNatalMatchedPoints(
            List<ShinsalPoint> points
    ) {

        return !points.isEmpty()
                && points
                .stream()
                .allMatch(
                        point ->
                                point.layer()
                                        == ShinsalLayer.NATAL
                );
    }

    private static boolean hasLuckMatchedPoints(
            List<ShinsalPoint> points
    ) {

        return points
                .stream()
                .anyMatch(
                        point ->
                                point.layer()
                                        != ShinsalLayer.NATAL
                );
    }

    private static String pointSignature(
            List<ShinsalPoint> points
    ) {

        LinkedHashSet<String> signatures =
                new LinkedHashSet<>();

        for (
                ShinsalPoint point
                : points
        ) {

            String value =
                    point.kind()
                            .heavenlyStem()
                            ? point.requireStem().name()
                            : point.requireBranch().name();

            signatures.add(
                    point.locationKey()
                            + ":"
                            + value
            );
        }

        return String.join(
                ",",
                signatures
        );
    }
}