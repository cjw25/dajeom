package com.dajeom.model;

import com.dajeom.type.LifeDomainEvidenceSourceType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 정밀화 18차-2.
 *
 * 3~17차 및 이후 확장된 deterministic 분석 결과를
 * 18차 생활영역 Evidence 구조로 변환한 결과.
 *
 * =================================================
 *
 * natalEvidences
 *
 * 원국 기반 Evidence.
 *
 * CORE_STRUCTURE
 * STRUCTURAL_CONTEXT
 * NATAL SHINSAL
 *
 * =================================================
 *
 * segmentEvidences
 *
 * 각 16차 Timeline Segment별:
 *
 * DECADE_CONTEXT
 * ANNUAL_CONTEXT
 * ANNUAL_FUNCTIONAL_CONTEXT
 * MONTHLY_CONTEXT
 * 운 SHINSAL
 *
 * =================================================
 *
 * 원국 Evidence는 Segment마다 복제하지 않는다.
 */
public record LifeDomainEvidenceResolution(

        LuckIntegratedAnalysis luckIntegratedAnalysis,

        ShinsalAnalysis shinsalAnalysis,

        List<LifeDomainEvidenceReference<?>> natalEvidences,

        List<LifeDomainSegmentEvidence> segmentEvidences

) {

    public LifeDomainEvidenceResolution {

        Objects.requireNonNull(
                luckIntegratedAnalysis,
                "LifeDomainEvidenceResolution에는 LuckIntegratedAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                shinsalAnalysis,
                "LifeDomainEvidenceResolution에는 ShinsalAnalysis가 필요합니다."
        );

        if (
                shinsalAnalysis
                        .luckIntegratedAnalysis()
                        != luckIntegratedAnalysis
        ) {

            throw new IllegalArgumentException(
                    "ShinsalAnalysis와 LuckIntegratedAnalysis의 Anchor가 일치하지 않습니다."
            );
        }

        natalEvidences =
                natalEvidences == null
                        ? List.of()
                        : List.copyOf(
                        natalEvidences
                );

        segmentEvidences =
                segmentEvidences == null
                        ? List.of()
                        : List.copyOf(
                        segmentEvidences
                );

        /*
         * =================================================
         * Natal Evidence
         * =================================================
         */

        Set<String> natalKeys =
                new HashSet<>();

        for (
                LifeDomainEvidenceReference<?> evidence
                : natalEvidences
        ) {

            Objects.requireNonNull(
                    evidence,
                    "Natal Evidence는 null일 수 없습니다."
            );

            if (
                    evidence.sourceType()
                            == LifeDomainEvidenceSourceType.DECADE_CONTEXT

                            || evidence.sourceType()
                            == LifeDomainEvidenceSourceType.ANNUAL_CONTEXT

                            || evidence.sourceType()
                            == LifeDomainEvidenceSourceType
                            .ANNUAL_FUNCTIONAL_CONTEXT

                            || evidence.sourceType()
                            == LifeDomainEvidenceSourceType.MONTHLY_CONTEXT
            ) {

                throw new IllegalArgumentException(
                        "natalEvidences에는 시간축 Context Source를 넣을 수 없습니다: "
                                + evidence.sourceType()
                );
            }

            /*
             * =================================================
             * PALACE
             * =================================================
             */

            if (
                    evidence.sourceType()
                            == LifeDomainEvidenceSourceType.PALACE

                            && evidence.source()
                            != luckIntegratedAnalysis.natalAnalysis()
            ) {

                throw new IllegalArgumentException(
                        "PALACE Evidence는 현재 LuckIntegratedAnalysis의 PalaceAnalysis를 참조해야 합니다."
                );
            }

            /*
             * =================================================
             * NATAL SHINSAL
             * =================================================
             */

            if (
                    evidence.sourceType()
                            == LifeDomainEvidenceSourceType.SHINSAL
            ) {

                if (
                        !(evidence.source()
                                instanceof ShinsalEvidence shinsalEvidence)
                ) {

                    throw new IllegalArgumentException(
                            "SHINSAL Source는 ShinsalEvidence여야 합니다."
                    );
                }

                if (
                        !shinsalEvidence.natalEvidence()
                ) {

                    throw new IllegalArgumentException(
                            "natalEvidences에는 원국 ShinsalEvidence만 들어갈 수 있습니다."
                    );
                }

                boolean known =
                        shinsalAnalysis
                                .natalEvidences()
                                .stream()
                                .anyMatch(
                                        candidate ->
                                                candidate
                                                        == shinsalEvidence
                                );

                if (
                        !known
                ) {

                    throw new IllegalArgumentException(
                            "ShinsalAnalysis에 존재하지 않는 원국 ShinsalEvidence가 연결되었습니다."
                    );
                }
            }

            String key =
                    evidence.sourceType().name()
                            + "|"
                            + evidence.sourceKey();

            if (
                    !natalKeys.add(
                            key
                    )
            ) {

                throw new IllegalArgumentException(
                        "원국 생활영역 Evidence가 중복되었습니다: "
                                + key
                );
            }
        }

        /*
         * =================================================
         * Timeline Segment 수
         * =================================================
         */

        if (
                segmentEvidences.size()
                        != luckIntegratedAnalysis
                        .segmentAnalyses()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "LifeDomainSegmentEvidence 수와 LuckIntegratedSegmentAnalysis 수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Timeline 순서 / Identity
         * =================================================
         */

        for (
                int index = 0;
                index < segmentEvidences.size();
                index++
        ) {

            LifeDomainSegmentEvidence segmentEvidence =
                    Objects.requireNonNull(
                            segmentEvidences.get(index),
                            "LifeDomainSegmentEvidence는 null일 수 없습니다."
                    );

            LuckIntegratedSegmentAnalysis expected =
                    luckIntegratedAnalysis
                            .segmentAnalyses()
                            .get(index);

            if (
                    segmentEvidence.segmentAnalysis()
                            != expected
            ) {

                throw new IllegalArgumentException(
                        "생활영역 Segment Evidence 순서가 LuckIntegratedAnalysis와 일치하지 않습니다: "
                                + index
                );
            }
        }
    }

    /*
     * =================================================
     * Segment 조회
     * =================================================
     */

    public Optional<LifeDomainSegmentEvidence>
    segmentEvidenceOf(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        if (
                segmentAnalysis == null
        ) {

            return Optional.empty();
        }

        return segmentEvidences
                .stream()
                .filter(
                        evidence ->
                                evidence.segmentAnalysis()
                                        == segmentAnalysis
                )
                .findFirst();
    }

    public LifeDomainSegmentEvidence
    segmentEvidenceAt(
            int index
    ) {

        if (
                index < 0
                        || index >= segmentEvidences.size()
        ) {

            throw new IndexOutOfBoundsException(
                    "LifeDomainSegmentEvidence index가 범위를 벗어났습니다: "
                            + index
            );
        }

        return segmentEvidences
                .get(index);
    }

    /*
     * =================================================
     * 특정 Segment에 적용되는 전체 Source
     *
     * 원국
     * +
     * 대운/세운/Annual Functional/월운
     * +
     * 해당 운 신살
     * =================================================
     */

    public List<LifeDomainEvidenceReference<?>>
    evidencesForSegment(
            LuckIntegratedSegmentAnalysis segmentAnalysis
    ) {

        LifeDomainSegmentEvidence segmentEvidence =
                segmentEvidenceOf(
                        segmentAnalysis
                )
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "현재 Resolution에 존재하지 않는 Timeline Segment입니다."
                                        )
                        );

        List<LifeDomainEvidenceReference<?>> result =
                new ArrayList<>();

        result.addAll(
                natalEvidences
        );

        result.addAll(
                segmentEvidence.temporalEvidences()
        );

        result.addAll(
                segmentEvidence.shinsalEvidences()
        );

        return List.copyOf(
                result
        );
    }

    public int segmentCount() {

        return segmentEvidences
                .size();
    }

    public boolean completeChart() {

        return luckIntegratedAnalysis
                .completeChart();
    }

    public boolean uncertaintyExists() {

        return luckIntegratedAnalysis
                .uncertaintyExists();
    }
}