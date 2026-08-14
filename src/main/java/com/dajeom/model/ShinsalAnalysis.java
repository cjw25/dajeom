package com.dajeom.model;

import com.dajeom.type.ShinsalEvidenceCertainty;
import com.dajeom.type.ShinsalLayer;
import com.dajeom.type.ShinsalType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 정밀화 17차-6.
 *
 * 신살 분석 최종 Aggregate.
 *
 * =================================================
 *
 * 포함:
 *
 * LuckIntegratedAnalysis
 * ShinsalTimelineAnalysis
 *
 * 원국 ShinsalEvidence
 * 운 ShinsalEvidence
 *
 * =================================================
 *
 * 이후 18차 생활영역에서는
 * 신살 Rule을 다시 실행하지 않고
 * 이 ShinsalAnalysis를 보조 Evidence로 사용한다.
 */
public record ShinsalAnalysis(

        LuckIntegratedAnalysis luckIntegratedAnalysis,

        ShinsalTimelineAnalysis timelineAnalysis,

        List<ShinsalEvidence> natalEvidences,

        List<ShinsalEvidence> luckEvidences

) {

    public ShinsalAnalysis {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "최종 신살 분석에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                timelineAnalysis,
                "최종 신살 분석에는 ShinsalTimelineAnalysis가 필요합니다."
        );

        if (
                timelineAnalysis
                        .luckIntegratedAnalysis()
                        != luckIntegratedAnalysis
        ) {

            throw new IllegalArgumentException(
                    "ShinsalTimelineAnalysis가 현재 LuckIntegratedAnalysis를 참조하지 않습니다."
            );
        }

        natalEvidences =
                natalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        natalEvidences
                );

        luckEvidences =
                luckEvidences == null
                        ? List.of()
                        : List.copyOf(
                        luckEvidences
                );

        /*
         * =================================================
         * NATAL
         * =================================================
         */

        for (
                ShinsalEvidence evidence
                : natalEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "원국 ShinsalEvidence는 null일 수 없습니다."
            );

            if (
                    !evidence.natalEvidence()
            ) {

                throw new IllegalArgumentException(
                        "natalEvidences에는 NATAL Evidence만 들어갈 수 있습니다."
                );
            }

            if (
                    !evidence.definite()
            ) {

                throw new IllegalArgumentException(
                        "검출된 원국 ShinsalEvidence는 DEFINITE여야 합니다."
                );
            }
        }

        /*
         * =================================================
         * LUCK
         * =================================================
         */

        int totalSegmentCount =
                luckIntegratedAnalysis
                        .segmentAnalyses()
                        .size();

        for (
                ShinsalEvidence evidence
                : luckEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "운 ShinsalEvidence는 null일 수 없습니다."
            );

            if (
                    !evidence.luckEvidence()
            ) {

                throw new IllegalArgumentException(
                        "luckEvidences에는 운 Layer Evidence가 필요합니다."
                );
            }

            /*
             * 모든 applicableSegment가
             * 실제 16차 Segment여야 한다.
             */
            for (
                    LuckIntegratedSegmentAnalysis segment
                    : evidence.applicableSegments()
            ) {

                boolean known =
                        luckIntegratedAnalysis
                                .segmentAnalyses()
                                .stream()
                                .anyMatch(
                                        candidate ->
                                                candidate == segment
                                );

                if (
                        !known
                ) {

                    throw new IllegalArgumentException(
                            "LuckIntegratedAnalysis에 존재하지 않는 Segment가 신살 Evidence에 연결되었습니다."
                    );
                }
            }

            /*
             * =================================================
             * Certainty 의미 강제
             * =================================================
             *
             * 모든 가능한 Timeline Segment에 존재
             * -> DEFINITE
             *
             * 일부 Segment에만 존재
             * -> POSSIBLE
             */

            boolean appliesToAll =
                    evidence
                            .applicableSegmentCount()
                            == totalSegmentCount;

            if (
                    appliesToAll
                            && !evidence.definite()
            ) {

                throw new IllegalArgumentException(
                        "모든 Timeline Segment에 적용되는 신살 Evidence는 DEFINITE여야 합니다."
                );
            }

            if (
                    !appliesToAll
                            && !evidence.possibleOnly()
            ) {

                throw new IllegalArgumentException(
                        "일부 Timeline Segment에만 적용되는 신살 Evidence는 POSSIBLE이어야 합니다."
                );
            }
        }

        /*
         * =================================================
         * 정규화 Identity 중복 금지
         * =================================================
         */

        validateNoDuplicateEvidence(
                natalEvidences,
                "원국"
        );

        validateNoDuplicateEvidence(
                luckEvidences,
                "운"
        );
    }

    /*
     * =================================================
     * 전체
     * =================================================
     */

    public List<ShinsalEvidence> evidences() {

        List<ShinsalEvidence> result =
                new ArrayList<>(
                        natalEvidences.size()
                                + luckEvidences.size()
                );

        result.addAll(
                natalEvidences
        );

        result.addAll(
                luckEvidences
        );

        return List.copyOf(
                result
        );
    }

    public boolean hasEvidences() {

        return !evidences()
                .isEmpty();
    }

    /*
     * =================================================
     * 원국
     * =================================================
     */

    public boolean hasNatalEvidence() {

        return !natalEvidences
                .isEmpty();
    }

    public List<ShinsalEvidence>
    natalEvidencesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return natalEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.type()
                                        == type
                )
                .toList();
    }

    /*
     * =================================================
     * 운
     * =================================================
     */

    public boolean hasLuckEvidence() {

        return !luckEvidences
                .isEmpty();
    }

    public List<ShinsalEvidence>
    luckEvidencesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return luckEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.type()
                                        == type
                )
                .toList();
    }

    /*
     * =================================================
     * Layer
     * =================================================
     */

    public List<ShinsalEvidence>
    evidencesOf(
            ShinsalLayer layer
    ) {

        if (
                layer == null
        ) {

            return List.of();
        }

        return evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.involvesLayer(
                                        layer
                                )
                )
                .toList();
    }

    public List<ShinsalEvidence>
    decadeEvidences() {

        return evidencesOf(
                ShinsalLayer.DECADE
        );
    }

    public List<ShinsalEvidence>
    annualEvidences() {

        return evidencesOf(
                ShinsalLayer.ANNUAL
        );
    }

    public List<ShinsalEvidence>
    monthlyEvidences() {

        return evidencesOf(
                ShinsalLayer.MONTHLY
        );
    }

    /*
     * =================================================
     * Certainty
     * =================================================
     */

    public List<ShinsalEvidence>
    definiteEvidences() {

        return evidences()
                .stream()
                .filter(
                        ShinsalEvidence::definite
                )
                .toList();
    }

    public List<ShinsalEvidence>
    possibleEvidences() {

        return evidences()
                .stream()
                .filter(
                        ShinsalEvidence::possibleOnly
                )
                .toList();
    }

    public List<ShinsalEvidence>
    definiteLuckEvidences() {

        return luckEvidences
                .stream()
                .filter(
                        ShinsalEvidence::definite
                )
                .toList();
    }

    public List<ShinsalEvidence>
    possibleLuckEvidences() {

        return luckEvidences
                .stream()
                .filter(
                        ShinsalEvidence::possibleOnly
                )
                .toList();
    }

    /*
     * =================================================
     * 특정 Type
     * =================================================
     */

    public List<ShinsalEvidence>
    evidencesOf(
            ShinsalType type
    ) {

        if (
                type == null
        ) {

            return List.of();
        }

        return evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.type()
                                        == type
                )
                .toList();
    }

    public boolean has(
            ShinsalType type
    ) {

        return !evidencesOf(
                type
        )
                .isEmpty();
    }

    public boolean hasDefinite(
            ShinsalType type
    ) {

        return evidencesOf(
                type
        )
                .stream()
                .anyMatch(
                        ShinsalEvidence::definite
                );
    }

    public boolean hasPossible(
            ShinsalType type
    ) {

        return evidencesOf(
                type
        )
                .stream()
                .anyMatch(
                        ShinsalEvidence::possibleOnly
                );
    }

    /*
     * =================================================
     * 특정 Segment
     * =================================================
     */

    public List<ShinsalEvidence>
    evidencesApplicableTo(
            LuckIntegratedSegmentAnalysis segment
    ) {

        if (
                segment == null
        ) {

            return List.of();
        }

        return evidences()
                .stream()
                .filter(
                        evidence ->
                                evidence.appliesTo(
                                        segment
                                )
                )
                .toList();
    }

    /*
     * =================================================
     * Type 집합
     * =================================================
     */

    public Set<ShinsalType>
    detectedTypes() {

        return evidences()
                .stream()
                .map(
                        ShinsalEvidence::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    public Set<ShinsalType>
    definiteTypes() {

        return definiteEvidences()
                .stream()
                .map(
                        ShinsalEvidence::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    public Set<ShinsalType>
    possibleTypes() {

        return possibleEvidences()
                .stream()
                .map(
                        ShinsalEvidence::type
                )
                .collect(
                        Collectors.toUnmodifiableSet()
                );
    }

    /*
     * =================================================
     * 16차
     * =================================================
     */

    public boolean timelineUncertaintyExists() {

        return luckIntegratedAnalysis
                .uncertaintyExists();
    }

    public boolean completeChart() {

        return luckIntegratedAnalysis
                .completeChart();
    }

    /*
     * =================================================
     * Validation
     * =================================================
     */

    private static void validateNoDuplicateEvidence(

            List<ShinsalEvidence> evidences,

            String label

    ) {

        Set<String> keys =
                new HashSet<>();

        for (
                ShinsalEvidence evidence
                : evidences
        ) {

            if (
                    !keys.add(
                            evidence.identityKey()
                    )
            ) {

                throw new IllegalArgumentException(
                        label
                                + " ShinsalEvidence가 정규화되지 않고 중복되었습니다: "
                                + evidence.identityKey()
                );
            }
        }
    }
}